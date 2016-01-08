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

import org.junit.gen5.api.Test;
import org.junit.gen5.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration tests which demonstrate that the Spring TestContext Framework can
 * be used with the current JUnit 5 snapshots via a single {@link SpringExtension}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 */
// TODO Uncomment the following line to run the tests within the IDE.
// @RunWith(JUnit5.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class SpringExtensionTests {

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	Person dilbert;

	@Autowired
	List<Person> people;

	@Test
	void applicationContextInjected() {
		assertNotNull(applicationContext, "ApplicationContext should have been @Autowired by Spring");
	}

	@Test
	void springBeansInjected() {
		assertNotNull(dilbert, "Person should have been @Autowired by Spring");
		assertEquals("Dilbert", dilbert.getName(), "Person's name");
		assertEquals(2, people.size(), "Number of Person objects in context");
	}

}
