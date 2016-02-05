package org.springframework.test.context.junit5;

import org.junit.gen5.api.extension.ExtensionContext;
import org.junit.gen5.api.extension.MethodInvocationContext;
import org.junit.gen5.api.extension.MethodParameterResolver;
import org.junit.gen5.api.extension.ParameterResolutionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Tadaya Tsuyukubo
 */
public class SpringParameterExtension extends SpringExtension implements MethodParameterResolver {

	@Override
	public boolean supports(Parameter parameter, MethodInvocationContext methodInvocationContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return true;  // TODO: should check
	}

	@Override
	public Object resolve(Parameter parameter, MethodInvocationContext methodInvocationContext, ExtensionContext extensionContext) throws ParameterResolutionException {

		ApplicationContext applicationContext = getApplicationContext(extensionContext.getTestClass());

		// special case for application context
		if (ApplicationContext.class.isAssignableFrom(parameter.getType())) {
			return applicationContext;
		}

		// TODO: need to resolve beans more sophisticated way. for example, generics support

		Object bean;

		Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
		if (qualifier != null) {
			bean = applicationContext.getBean(qualifier.value());
			if (bean != null) {
				return bean;
			}
		}

		bean = applicationContext.getBean(parameter.getType());
		if (bean != null) {
			return bean;
		}
		return applicationContext.getBean(parameter.getName());
	}

	private ApplicationContext getApplicationContext(Class<?> testClass) {
		TestContextManager manager = getTestContextManager(testClass);

		// hack to get TestContext
		Method method = ReflectionUtils.findMethod(TestContextManager.class, "getTestContext");
		ReflectionUtils.makeAccessible(method);
		TestContext testContext = (TestContext) ReflectionUtils.invokeMethod(method, manager);

		return testContext.getApplicationContext();
	}
}
