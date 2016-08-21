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

package org.springframework.test.context.junit.jupiter.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringExtensionTestSuite;
import org.springframework.test.context.junit.jupiter.comics.Person;

/**
 * Integration tests which demonstrate that the Spring TestContext Framework can
 * be used with the current JUnit 5 snapshots via a single {@link SpringExtension}
 * in conjunction with XML Spring configuration files.
 *
 * <p>To run these tests in an IDE, simply run {@link SpringExtensionTestSuite}
 * as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:test-config.xml")
class XmlSpringExtensionTests {

	@Autowired
	Person dilbert;

	@Autowired
	List<Person> people;

	@Test
	void applicationContextInjectedIntoMethod(ApplicationContext applicationContext) {
		assertNotNull(applicationContext, "ApplicationContext should have been injected by Spring");
		assertEquals(this.dilbert, applicationContext.getBean("dilbert", Person.class));
	}

	@Test
	void genericApplicationContextInjectedIntoMethod(GenericApplicationContext applicationContext) {
		assertNotNull(applicationContext, "GenericApplicationContext should have been injected by Spring");
		assertEquals(this.dilbert, applicationContext.getBean("dilbert", Person.class));
	}

	@Test
	void autowiredFields() {
		assertNotNull(this.dilbert, "Dilbert should have been @Autowired by Spring");
		assertEquals("Dilbert", this.dilbert.getName(), "Person's name");
		assertEquals(2, this.people.size(), "Number of people in context");
	}

	@Test
	void autowiredParameterWithExplicitQualifier(@Qualifier("wally") Person person) {
		assertNotNull(person, "Wally should have been @Autowired by Spring");
		assertEquals("Wally", person.getName(), "Person's name");
	}

	/**
	 * NOTE: Test code must be compiled with "-g" (debug symbols) or "-parameters" in order
	 * for the parameter name to be used as the qualifier; otherwise, use
	 * {@code @Qualifier("wally")}.
	 */
	@Test
	void autowiredParameterWithImplicitQualifierBasedOnParameterName(@Autowired Person wally) {
		assertNotNull(wally, "Wally should have been @Autowired by Spring");
		assertEquals("Wally", wally.getName(), "Person's name");
	}

	@Test
	void autowiredParameterOfList(@Autowired List<Person> peopleParam) {
		assertNotNull(peopleParam, "list of people should have been @Autowired by Spring");
		assertEquals(2, peopleParam.size(), "Number of people in context");
	}

}
