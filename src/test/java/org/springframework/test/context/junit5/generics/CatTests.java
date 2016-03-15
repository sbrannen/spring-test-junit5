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

package org.springframework.test.context.junit5.generics;

import org.junit.gen5.api.extension.ExtendWith;
import org.springframework.test.context.junit5.SpringExtension;
import org.springframework.test.context.junit5.comics.Cat;

/**
 * Parameterized test class for integration tests that demonstrate support for
 * Java generics in JUnit 5 test classes when used with the the Spring
 * TestContext Framework and the {@link SpringExtension}.
 *
 * @author Sam Brannen
 * @since 5.0
 */
// TODO Determine why we have to redeclare @ExtendWith(SpringExtension.class).
@ExtendWith(SpringExtension.class)
class CatTests extends GenericComicCharactersTests<Cat> {

	@Override
	protected int getExpectedNumCharacters() {
		return 2;
	}

	@Override
	protected String getExpectedName() {
		return "Catbert";
	}

}
