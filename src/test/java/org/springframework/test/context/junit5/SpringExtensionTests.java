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
import static org.junit.gen5.api.Assertions.assertNull;

import java.util.List;

import org.junit.gen5.api.Disabled;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.TestInfo;
import org.junit.gen5.api.TestReporter;
import org.junit.gen5.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

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
@TestPropertySource(properties = "enigma = 42")
class SpringExtensionTests {

	@Autowired
	Person dilbert;

	@Autowired
	List<Person> people;

	@Autowired
	Dog dog;

	@Value("${enigma}")
	Integer enigma;

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
		assertEquals(2, this.people.size(), "Number of Person objects in context");

		assertNotNull(this.dog, "Dogbert should have been @Autowired by Spring");
		assertEquals("Dogbert", this.dog.getName(), "Dog's name");

		assertNotNull(this.enigma, "Enigma should have been injected via @Value by Spring");
		assertEquals(new Integer(42), this.enigma, "enigma");
	}

	@Test
	void autowiredParameterForSingleBeanOfType(@SpringBean Dog doggy) {
		assertNotNull(doggy, "Dogbert should have been @Autowired by Spring");
		assertEquals("Dogbert", doggy.getName(), "Dog's name");
	}

	@Test
	void autowiredParameterWithExplicitQualifier(@SpringBean("wally") Person person) {
		assertNotNull(person, "Wally should have been @Autowired by Spring");
		assertEquals("Wally", person.getName(), "Person's name");
	}

	/**
	 * NOTE: Test code must be compiled with "-parameters" in order for the parameter
	 * name to be used as the qualifier; otherwise, use {@code @SpringBean("wally")}.
	 */
	@Test
	void autowiredParameterWithQualifierBasedOnParameterName(@SpringBean Person wally) {
		assertNotNull(wally, "Wally should have been @Autowired by Spring");
		assertEquals("Wally", wally.getName(), "Person's name");
	}

	@Test
	void autowiredParameterThatDoesNotExistButIsNotRequired(@SpringBean(required = false) Number number) {
		assertNull(number, "Optional number should have been @Autowired as 'null' by Spring");
	}

	@Test
	@Disabled("Disabled until JUnit's MethodInvoker properly supports primitive types for parameters")
	void valueParameterWithPrimitiveType(@Value("99") int num) {
		assertEquals(99, num);
	}

	@Test
	void valueParameterFromPropertyPlaceholder(@Value("${enigma}") Integer enigmaParam) {
		assertNotNull(enigmaParam, "Enigma should have been injected via @Value by Spring");
		assertEquals(new Integer(42), enigmaParam, "enigma");
	}

	@Test
	void valueParameterFromDefaultValueForPropertyPlaceholder(@Value("${bogus:false}") Boolean defaultValue) {
		assertNotNull(defaultValue, "Default value should have been injected via @Value by Spring");
		assertEquals(false, defaultValue, "default value");
	}

	@Test
	void valueParameterFromSpelExpression(@Value("#{@dilbert.name}") String name,
			@Value("#{'Hello ' + ${enigma}}") String hello) {

		assertNotNull(name, "Dilbert's name should have been injected via SpEL expression in @Value by Spring");
		assertEquals("Dilbert", name, "name from SpEL expression");

		assertNotNull(hello, "hello should have been injected via SpEL expression in @Value by Spring");
		assertEquals("Hello 42", hello, "hello from SpEL expression");
	}

	@Test
	void junitAndSpringMethodInjectionCombined(@SpringBean("wally") Person person, TestInfo testInfo,
			ApplicationContext context, TestReporter testReporter) {

		// JUnit 5
		assertNotNull(testInfo, "TestInfo should have been injected by JUnit");
		assertNotNull(testReporter, "TestReporter should have been injected by JUnit");

		// Spring
		assertNotNull(context, "ApplicationContext should have been injected by Spring");
		assertNotNull(person, "Person should have been @Autowired by Spring");
	}

}
