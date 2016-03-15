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

package org.springframework.test.context.junit5.web;

import static org.hamcrest.CoreMatchers.is;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.gen5.api.DisplayName;
import org.junit.gen5.api.Test;

import org.springframework.test.context.junit5.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests which demonstrate use of the Spring MVC Test Framework
 * and the Spring TestContext Framework with current JUnit 5 snapshots
 * and the {@link SpringExtension} (via a custom
 * {@link SpringJUnit5WebConfig @SpringJUnit5WebConfig} composed annotation).
 *
 * <p>Note how the {@link #springMvcTest(WebApplicationContext)} test method
 * has the {@link WebApplicationContext} injected as a method parameter.
 * This allows the {@link MockMvc} instance to be configured local to the
 * test method without any fields in the test class.
 *
 * <p>To run these tests in an IDE, simply run
 * {@link org.springframework.test.context.junit5.SpringExtensionTestSuite
 * SpringExtensionTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see SpringJUnit5WebConfig
 * @see org.springframework.test.context.junit5.SpringExtensionTests
 * @see org.springframework.test.context.junit5.ComposedSpringExtensionTests
 */
@SpringJUnit5WebConfig(WebConfig.class)
@DisplayName("Web SpringExtension Tests")
class WebSpringExtensionTests {

	@Test
	void springMvcTest(WebApplicationContext wac) throws Exception {
		webAppContextSetup(wac).build()
			.perform(get("/person/42"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("Dilbert")));
	}

}
