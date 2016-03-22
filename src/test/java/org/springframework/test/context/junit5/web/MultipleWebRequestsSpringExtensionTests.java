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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.gen5.api.BeforeEach;
import org.junit.gen5.api.Test;

import org.springframework.test.context.junit5.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests which demonstrate how to set up a {@link MockMvc}
 * instance in an {@link BeforeEach @BeforeEach} method with the
 * {@link SpringExtension} (registered via a custom
 * {@link SpringJUnit5WebConfig @SpringJUnit5WebConfig} composed annotation).
 *
 * <p>To run these tests in an IDE, simply run
 * {@link org.springframework.test.context.junit5.SpringExtensionTestSuite
 * SpringExtensionTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see SpringJUnit5WebConfig
 * @see org.springframework.test.context.junit5.web.WebSpringExtensionTests
 */
@SpringJUnit5WebConfig(WebConfig.class)
class MultipleWebRequestsSpringExtensionTests {

	MockMvc mockMvc;

	@BeforeEach
	void setUpMockMvc(WebApplicationContext wac) {
		this.mockMvc = webAppContextSetup(wac)
			.alwaysExpect(status().isOk())
			.alwaysExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))//
			.build();
	}

	@Test
	void getPerson42() throws Exception {
		this.mockMvc.perform(get("/person/42"))
			.andExpect(jsonPath("$.name", is("Dilbert")));
	}

	@Test
	void getPerson99() throws Exception {
		this.mockMvc.perform(get("/person/99"))
			.andExpect(jsonPath("$.name", is("Wally")));
	}

}
