/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.junit5;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.gen5.api.extension.AfterAllExtensionPoint;
import org.junit.gen5.api.extension.AfterEachExtensionPoint;
import org.junit.gen5.api.extension.BeforeAllExtensionPoint;
import org.junit.gen5.api.extension.BeforeEachExtensionPoint;
import org.junit.gen5.api.extension.ContainerExtensionContext;
import org.junit.gen5.api.extension.ExtensionContext;
import org.junit.gen5.api.extension.InstancePostProcessor;
import org.junit.gen5.api.extension.MethodInvocationContext;
import org.junit.gen5.api.extension.MethodParameterResolver;
import org.junit.gen5.api.extension.ParameterResolutionException;
import org.junit.gen5.api.extension.TestExtensionContext;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@code SpringExtension} integrates the <em>Spring TestContext Framework</em>
 * into JUnit 5.
 *
 * <p>To use this class, simply annotate a JUnit 5 based test class with
 * {@code @ExtendWith(SpringExtension.class)}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see org.springframework.test.context.junit5.SpringBean
 * @see org.springframework.test.context.junit5.SpringJUnit5Config
 * @see org.springframework.test.context.junit5.web.SpringJUnit5WebConfig
 * @see org.springframework.test.context.TestContextManager
 */
public class SpringExtension implements BeforeAllExtensionPoint, AfterAllExtensionPoint, InstancePostProcessor,
		BeforeEachExtensionPoint, AfterEachExtensionPoint, MethodParameterResolver {

	/**
	 * Cache of {@code TestContextManagers} keyed by test class.
	 */
	private final Map<Class<?>, TestContextManager> tcmCache = new ConcurrentHashMap<Class<?>, TestContextManager>(64);

	/**
	 * Delegates to {@link TestContextManager#beforeTestClass}.
	 */
	@Override
	public void beforeAll(ContainerExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass();
		getTestContextManager(testClass).beforeTestClass();
	}

	/**
	 * Delegates to {@link TestContextManager#afterTestClass}.
	 */
	@Override
	public void afterAll(ContainerExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass();
		try {
			getTestContextManager(testClass).afterTestClass();
		}
		finally {
			this.tcmCache.remove(testClass);
		}
	}

	/**
	 * Delegates to {@link TestContextManager#prepareTestInstance}.
	 */
	@Override
	public void postProcessTestInstance(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass();
		Object testInstance = context.getTestInstance();
		getTestContextManager(testClass).prepareTestInstance(testInstance);
	}

	/**
	 * Delegates to {@link TestContextManager#beforeTestMethod}.
	 */
	@Override
	public void beforeEach(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass();
		Object testInstance = context.getTestInstance();
		Method testMethod = context.getTestMethod();

		getTestContextManager(testClass).beforeTestMethod(testInstance, testMethod);
	}

	/**
	 * Delegates to {@link TestContextManager#afterTestMethod}.
	 */
	@Override
	public void afterEach(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass();
		Object testInstance = context.getTestInstance();
		Method testMethod = context.getTestMethod();
		// TODO Retrieve exception from TestExtensionContext once supported by JUnit 5.
		Throwable testException = null; // context.getTestException();

		getTestContextManager(testClass).afterTestMethod(testInstance, testMethod, testException);
	}

	// --- MethodParameterResolver support -------------------------------------

	/**
	 * Supports method injection for parameters of type {@link ApplicationContext}
	 * (or a sub-type thereof) and parameters annotated with {@link SpringBean @SpringBean}
	 * or {@link Value @Value}.
	 */
	@Override
	public boolean supports(Parameter parameter, MethodInvocationContext methodInvocationContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {

		return requiresApplicationContext(parameter) || findMergedAnnotation(parameter, Autowired.class).isPresent()
				|| findMergedAnnotation(parameter, Value.class).isPresent();
	}

	/**
	 * Resolves values for parameters of type {@link ApplicationContext} (or a
	 * sub-type thereof) and parameters annotated with {@link SpringBean @SpringBean}
	 * or {@link Value @Value}.
	 *
	 * <p>If an {@code @SpringBean}-annotated parameter is also annotated with
	 * {@link Qualifier @Qualifier}, {@link Qualifier#value} will be used as the
	 * <em>qualifier</em> for resolving ambiguities; otherwise,
	 * {@link Parameter#getName()} will be used as the <em>qualifier</em>. Note
	 * that {@link SpringBean#value} or {@link SpringBean#qualifier} may be used
	 * as an alternative to an explicit {@code @Qualifier} declaration.
	 */
	@Override
	public Object resolve(Parameter parameter, MethodInvocationContext methodInvocationContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {

		ApplicationContext applicationContext = getApplicationContext(extensionContext.getTestClass());

		if (requiresApplicationContext(parameter)) {
			return applicationContext;
		}

		AutowireCapableBeanFactory bf = applicationContext.getAutowireCapableBeanFactory();

		Optional<Autowired> autowired = findMergedAnnotation(parameter, Autowired.class);
		if (autowired.isPresent()) {
			Class<?> type = parameter.getType();

			// look up single bean by type
			if (bf instanceof ListableBeanFactory) {
				Map<String, ?> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory) bf, type);
				if (beans.size() == 1) {
					return beans.values().iterator().next();
				}
			}

			// look up by type and qualifier
			try {
				String qualifier = resolveQualifier(parameter);
				return BeanFactoryAnnotationUtils.qualifiedBeanOfType(bf, type, qualifier);
			}
			catch (Exception ex) {
				if (autowired.get().required()) {
					throw ex;
				}
				// else
				return null;
			}
		}

		Optional<Value> value = findMergedAnnotation(parameter, Value.class);
		if (value.isPresent() && (bf instanceof ConfigurableBeanFactory)) {
			return resolveValue((ConfigurableBeanFactory) bf, value.get().value(), parameter.getType());
		}

		// else
		throw new ParameterResolutionException(String.format("%s failed to resolve parameter [%s] in method [%s]",
			getClass().getSimpleName(), parameter, methodInvocationContext.getMethod().toGenericString()));
	}

	private static <A extends Annotation> Optional<A> findMergedAnnotation(AnnotatedElement element,
			Class<A> annotationType) {

		return Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(element, annotationType));
	}

	private boolean requiresApplicationContext(Parameter parameter) {
		return ApplicationContext.class.isAssignableFrom(parameter.getType());
	}

	private String resolveQualifier(Parameter parameter) {
		// @formatter:off
		return findMergedAnnotation(parameter, Qualifier.class)
				.map(Qualifier::value)
				.filter(StringUtils::hasText)
				.orElse(parameter.getName());
		// @formatter:on
	}

	private Object resolveValue(ConfigurableBeanFactory beanFactory, final String stringValue, Class<?> requiredType) {
		String resolvedStringValue = beanFactory.resolveEmbeddedValue(stringValue);
		Object value = resolvedStringValue;

		BeanExpressionResolver expressionResolver = beanFactory.getBeanExpressionResolver();
		if (expressionResolver != null) {
			BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);
			value = expressionResolver.evaluate(resolvedStringValue, expressionContext);
		}

		return beanFactory.getTypeConverter().convertIfNecessary(value, requiredType);
	}

	// -------------------------------------------------------------------------

	/**
	 * Get the {@link TestContextManager} associated with the supplied test class.
	 * @param testClass the test class to be managed; never {@code null}
	 */
	private TestContextManager getTestContextManager(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		return this.tcmCache.computeIfAbsent(testClass, TestContextManager::new);
	}

	/**
	 * Get the {@link ApplicationContext} associated with the supplied test class.
	 * @param testClass the test class whose context should be retrieved; never {@code null}
	 * @return the application context
	 */
	private ApplicationContext getApplicationContext(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		TestContextManager testContextManager = getTestContextManager(testClass);
		// TODO Remove use of reflection once we upgrade to Spring 4.3 RC1 or higher.
		TestContext testContext = (TestContext) ReflectionTestUtils.getField(testContextManager, "testContext");
		return testContext.getApplicationContext();
	}

}
