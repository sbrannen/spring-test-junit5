/*
 * Copyright 2015 the original author or authors.
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.gen5.api.extension.AfterAllCallbacks;
import org.junit.gen5.api.extension.AfterEachCallbacks;
import org.junit.gen5.api.extension.BeforeAllCallbacks;
import org.junit.gen5.api.extension.BeforeEachCallbacks;
import org.junit.gen5.api.extension.InstancePostProcessor;
import org.junit.gen5.api.extension.TestExecutionContext;
import org.junit.gen5.api.extension.TestExtension;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.Assert;

/**
 * {@code SpringExtension} demonstrates how the Spring TestContext Framework
 * can be fully integrated into the JUnit 5 prototype using a single
 * {@link TestExtension}.
 *
 * @author Sam Brannen
 * @since 5.0
 */
public class SpringExtension implements BeforeAllCallbacks, AfterAllCallbacks, InstancePostProcessor,
		BeforeEachCallbacks, AfterEachCallbacks {

	/**
	 * Cache of {@code TestContextManagers} keyed by test class.
	 */
	private final Map<Class<?>, TestContextManager> testContextManagerCache = new ConcurrentHashMap<Class<?>, TestContextManager>(
		64);

	@Override
	public void preBeforeAll(TestExecutionContext testExecutionContext) throws Exception {
		Class<?> testClass = testExecutionContext.getTestClass().get();

		getTestContextManager(testClass).beforeTestClass();
	}

	@Override
	public void postAfterAll(TestExecutionContext testExecutionContext) throws Exception {
		Class<?> testClass = testExecutionContext.getTestClass().get();

		try {
			getTestContextManager(testClass).afterTestClass();
		}
		finally {
			this.testContextManagerCache.remove(testClass);
		}
	}

	@Override
	public void postProcessTestInstance(TestExecutionContext testExecutionContext, Object testInstance)
			throws Exception {

		Class<?> testClass = testExecutionContext.getTestClass().get();

		getTestContextManager(testClass).prepareTestInstance(testInstance);
	}

	@Override
	public void preBeforeEach(TestExecutionContext testExecutionContext, Object testInstance) throws Exception {
		Class<?> testClass = testExecutionContext.getTestClass().get();
		Method testMethod = testExecutionContext.getTestMethod().get();

		getTestContextManager(testClass).beforeTestMethod(testInstance, testMethod);
	}

	@Override
	public void postAfterEach(TestExecutionContext testExecutionContext, Object testInstance) throws Exception {
		Class<?> testClass = testExecutionContext.getTestClass().get();
		Method testMethod = testExecutionContext.getTestMethod().get();
		Throwable testException = testExecutionContext.getTestException().orElse(null);

		getTestContextManager(testClass).afterTestMethod(testInstance, testMethod, testException);
	}

	/**
	 * Get the {@link TestContextManager} associated with the supplied test class.
	 * @param testClass the test class to be managed; never {@code null}
	 */
	private TestContextManager getTestContextManager(Class<?> testClass) {
		Assert.notNull(testClass, "testClass must not be null");
		return this.testContextManagerCache.computeIfAbsent(testClass, TestContextManager::new);
	}

}
