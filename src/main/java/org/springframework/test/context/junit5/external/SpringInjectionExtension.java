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

package org.springframework.test.context.junit5.external;

import org.junit.gen5.api.extension.InstancePostProcessor;
import org.junit.gen5.api.extension.TestExtensionContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Inject dependencies to the test instance from {@link ApplicationContext} provided by {@link ApplicationContextProvider}.
 *
 * @author Tadaya Tsuyukubo
 * @since 5.0
 */
public class SpringInjectionExtension implements InstancePostProcessor {

	// TODO: static or not, based on how InstantPostProcessor gets created
	private final ConcurrentHashMap<Class<? extends ApplicationContextProvider>, ApplicationContextProvider> providerCache =
			new ConcurrentHashMap<>(64);

	@Override
	public void postProcessTestInstance(TestExtensionContext context) throws Exception {

		ExternalApplicationContext externalApplicationContext = AnnotationUtils
				.findAnnotation(context.getTestClass(), ExternalApplicationContext.class);
		Class<? extends ApplicationContextProvider> providerClass = externalApplicationContext.provider();

		// find or create ApplicationContextProvider
		providerCache.computeIfAbsent(providerClass, aClass -> {
			ApplicationContextProvider provider = BeanUtils.instantiateClass(providerClass);
			provider.initialize();
			return provider;
		});
		ApplicationContextProvider provider = providerCache.get(providerClass);

		// retrieve ApplicationContext
		ApplicationContext applicationContext = provider.getApplicationContext();
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

		// inject to test instance
		Object testInstance = context.getTestInstance();
		beanFactory.autowireBeanProperties(testInstance, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(testInstance, context.getName());

	}

}
