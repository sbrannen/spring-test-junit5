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

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.gen5.api.DisplayName;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration tests which demonstrate that the Spring TestContext Framework can
 * be used with the current JUnit 5 snapshots via a single {@link SpringExtension}.
 *
 * <p>To run these tests in an IDE, simply run {@link SpringExtensionTestSuite}
 * as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see ComposedSpringExtensionTests
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@DisplayName("Basic SpringExtension Tests")
class SpringExtensionTests {

	@Autowired
	Person dilbert;

	@Autowired
	List<Person> people;

	@Test
	@DisplayName("ApplicationContext injected into method")
	void applicationContextInjected(ApplicationContext applicationContext) {
		assertNotNull(applicationContext, "ApplicationContext should have been injected into method by Spring");
		assertEquals(dilbert, applicationContext.getBean("dilbert", Person.class));
	}

	@Test
	@DisplayName("Spring @Beans injected into fields")
	void springBeansInjected() {
		assertNotNull(dilbert, "Person should have been @Autowired by Spring");
		assertEquals("Dilbert", dilbert.getName(), "Person's name");
		assertEquals(2, people.size(), "Number of Person objects in context");
	}

}
