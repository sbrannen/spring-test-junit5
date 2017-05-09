/*
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.test.context.junit5.external;

import org.junit.gen5.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotNull;
import static org.junit.gen5.api.Assertions.assertSame;
import static org.springframework.test.context.junit5.external.ExternalApplicationContextTests.MyApplicationContextProvider;

/**
 * @author Tadaya Tsuyukubo
 * @since 5.0
 */
@ExternalApplicationContext(provider = MyApplicationContextProvider.class)
public class ExternalApplicationContextTests {

	@Autowired
	String message;

	@Autowired
	Foo foo;

	@Autowired
	ApplicationContext externalApplicationContext;

	@Test
	void variablesInjected() {
		assertEquals("Hello from outside", message);

		assertNotNull(foo);
		assertEquals("FOO", foo.name);
	}

	@Test
	void applicationContextInjected() {
		assertNotNull(externalApplicationContext, "External ApplicationContext should have been injected");
		assertSame(MyApplicationContextProvider.applicationContext, externalApplicationContext);
		assertEquals(foo, externalApplicationContext.getBean(Foo.class));
	}

	static class MyApplicationContextProvider implements ApplicationContextProvider {

		static ApplicationContext applicationContext;

		@Override
		public void initialize() {
			applicationContext = new AnnotationConfigApplicationContext(MyConfiguration.class);
		}

		@Override
		public ApplicationContext getApplicationContext() {
			return applicationContext;
		}
	}

	@Configuration
	static class MyConfiguration {
		@Bean
		String message() {
			return "Hello from outside";
		}

		@Bean
		Foo foo() {
			return new Foo("FOO");
		}
	}

	static class Foo {
		String name;

		public Foo(String name) {
			this.name = name;
		}
	}

}
