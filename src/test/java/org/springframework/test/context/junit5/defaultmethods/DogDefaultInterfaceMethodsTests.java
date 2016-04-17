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

package org.springframework.test.context.junit5.defaultmethods;

import org.junit.gen5.api.extension.ExtendWith;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit5.SpringExtension;
import org.springframework.test.context.junit5.TestConfig;
import org.springframework.test.context.junit5.comics.Dog;

/**
 * Parameterized test class for integration tests that demonstrate support for
 * default interface methods and Java generics in JUnit 5 test classes when used
 * with the the Spring TestContext Framework and the {@link SpringExtension}.
 *
 * @author Sam Brannen
 * @since 5.0
 */
// TODO Delete @ExtendWith once JUnit supports it on interfaces
@ExtendWith(SpringExtension.class)
// TODO Delete @ContextConfiguration once Spring supports it on interfaces
@ContextConfiguration(classes = TestConfig.class)
class DogDefaultInterfaceMethodsTests implements GenericComicCharactersDefaultInterfaceMethodsTests<Dog> {

	@Override
	public int getExpectedNumCharacters() {
		return 1;
	}

	@Override
	public String getExpectedName() {
		return "Dogbert";
	}

}
