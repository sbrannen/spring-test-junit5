/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.test.context.junit.jupiter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.support.ParameterAutowireUtils;
import org.springframework.util.Assert;

/**
 * {@code SpringExtension} integrates the <em>Spring TestContext Framework</em>
 * into JUnit 5's Jupiter programming model.
 *
 * <p>To use this class, simply annotate a JUnit Jupiter based test class with
 * {@code @ExtendWith(SpringExtension.class)}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see org.springframework.test.context.junit.jupiter.SpringJUnitJupiterConfig
 * @see org.springframework.test.context.junit.jupiter.web.SpringJUnitJupiterWebConfig
 * @see org.springframework.test.context.TestContextManager
 */
public class SpringExtension implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor,
		BeforeEachCallback, AfterEachCallback, ParameterResolver {

	/**
	 * Cache of {@code TestContextManagers} keyed by test class.
	 */
	private final Map<Class<?>, TestContextManager> tcmCache = new ConcurrentHashMap<Class<?>, TestContextManager>(64);

	/**
	 * Delegates to {@link TestContextManager#beforeTestClass}.
	 */
	@Override
	public void beforeAll(ContainerExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass().get();
		getTestContextManager(testClass).beforeTestClass();
	}

	/**
	 * Delegates to {@link TestContextManager#afterTestClass}.
	 */
	@Override
	public void afterAll(ContainerExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass().get();
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
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
		getTestContextManager(context.getTestClass().get()).prepareTestInstance(testInstance);
	}

	/**
	 * Delegates to {@link TestContextManager#beforeTestMethod}.
	 */
	@Override
	public void beforeEach(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass().get();
		Object testInstance = context.getTestInstance();
		Method testMethod = context.getTestMethod().get();
		getTestContextManager(testClass).beforeTestMethod(testInstance, testMethod);
	}

	/**
	 * Delegates to {@link TestContextManager#afterTestMethod}.
	 */
	@Override
	public void afterEach(TestExtensionContext context) throws Exception {
		Class<?> testClass = context.getTestClass().get();
		Object testInstance = context.getTestInstance();
		Method testMethod = context.getTestMethod().get();
		// TODO Retrieve exception from TestExtensionContext once supported by JUnit Jupiter.
		Throwable testException = null; // context.getTestException();
		getTestContextManager(testClass).afterTestMethod(testInstance, testMethod, testException);
	}

	/**
	 * Determine if the value for the {@link Parameter} in the supplied
	 * {@link ParameterContext} should be autowired from the test's
	 * {@link ApplicationContext}.
	 * <p>Returns {@code true} if the parameter is declared in a {@link Constructor}
	 * that is annotated with {@link Autowired @Autowired} and otherwise delegates
	 * to {@link ParameterAutowireUtils#isAutowirable}.
	 * <p><strong>WARNING</strong>: if the parameter is declared in a {@code Constructor}
	 * that is annotated with {@code @Autowired}, Spring will assume the responsibility
	 * for resolving all parameters in the constructor. Consequently, no other
	 * registered {@link ParameterResolver} will be able to resolve parameters.
	 *
	 * @see #resolve
	 * @see ParameterAutowireUtils#isAutowirable
	 */
	@Override
	public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Parameter parameter = parameterContext.getParameter();
		Executable executable = parameter.getDeclaringExecutable();
		return (executable instanceof Constructor && AnnotatedElementUtils.hasAnnotation(executable, Autowired.class))
				|| ParameterAutowireUtils.isAutowirable(parameter);
	}

	/**
	 * Resolve a value for the {@link Parameter} in the supplied
	 * {@link ParameterContext} by retrieving the corresponding dependency
	 * from the test's {@link ApplicationContext}.
	 * <p>Delegates to {@link ParameterAutowireUtils#resolveDependency}.
	 * @see #supports
	 * @see ParameterAutowireUtils#resolveDependency
	 */
	@Override
	public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) {
		Parameter parameter = parameterContext.getParameter();
		Class<?> testClass = extensionContext.getTestClass().get();
		ApplicationContext applicationContext = getApplicationContext(testClass);
		return ParameterAutowireUtils.resolveDependency(parameter, testClass, applicationContext);
	}

	// -------------------------------------------------------------------------

	/**
	 * Get the {@link TestContextManager} associated with the supplied test class.
	 * @param testClass the test class to be managed; never {@code null}
	 * @return the {@code TestContextManager}; never {@code null}
	 */
	private TestContextManager getTestContextManager(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		return this.tcmCache.computeIfAbsent(testClass, TestContextManager::new);
	}

	/**
	 * Get the {@link ApplicationContext} associated with the supplied test class.
	 * @param testClass the test class whose context should be retrieved; never {@code null}
	 * @return the application context
	 * @throws IllegalStateException if an error occurs while retrieving the
	 * application context
	 * @see org.springframework.test.context.TestContext#getApplicationContext()
	 */
	private ApplicationContext getApplicationContext(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		return getTestContextManager(testClass).getTestContext().getApplicationContext();
	}

}
