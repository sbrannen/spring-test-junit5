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

import static org.springframework.test.util.ReflectionTestUtils.getField;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
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

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

/**
 * {@code SpringExtension} integrates the <em>Spring TestContext Framework</em>
 * into JUnit 5.
 *
 * <p>To use this class, simply annotate a JUnit 5 based test class with
 * {@code @ExtendWith(SpringExtension.class)}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see org.springframework.test.context.TestContextManager
 * @see org.springframework.test.context.junit5.SpringJUnit5Config
 * @see org.springframework.test.context.junit5.web.SpringJUnit5WebConfig
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
		Throwable testException = null; // context.getTestException();

		getTestContextManager(testClass).afterTestMethod(testInstance, testMethod, testException);
	}

	// --- MethodParameterResolver support -------------------------------------

	/**
	 * Currently only supports injection of the {@link ApplicationContext}.
	 */
	@Override
	public boolean supports(Parameter parameter, MethodInvocationContext methodInvocationContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {

		return ApplicationContext.class.isAssignableFrom(parameter.getType());
	}

	/**
	 * Currently only supports injection of the {@link ApplicationContext}.
	 */
	@Override
	public Object resolve(Parameter parameter, MethodInvocationContext methodInvocationContext,
			ExtensionContext extensionContext) throws ParameterResolutionException {

		return getApplicationContext(extensionContext.getTestClass());
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
	 * @return the application context, or {@code null} if the context could not be retrieved
	 */
	private ApplicationContext getApplicationContext(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		try {
			TestContext testContext = (TestContext) getField(getTestContextManager(testClass), "testContext");
			return testContext.getApplicationContext();
		}
		catch (Exception ex) {
			return null;
		}
	}

}
