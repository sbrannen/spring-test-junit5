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

package org.springframework.test.context.junit5.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.gen5.api.extension.ExtendWith;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit5.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Custom annotation that demonstrates the composability of annotations
 * from JUnit 5 and the Spring TestContext Framework.
 *
 * <p>Note that this annotation is meta-annotated with JUnit 5's
 * {@link ExtendWith @ExtendWith} as well as Spring's
 * {@link ContextConfiguration @ContextConfiguration} and
 * {@link WebAppConfiguration @WebAppConfiguration}.
 *
 * @author Sam Brannen
 * @since 5.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringJUnit5WebConfig {

	@AliasFor(annotation = ContextConfiguration.class, attribute = "classes")
	Class<?>[] value() default {};

	@AliasFor(annotation = ContextConfiguration.class, attribute = "classes")
	Class<?>[] classes() default {};

	@AliasFor(annotation = ContextConfiguration.class, attribute = "locations")
	String[] locations() default {};

	@AliasFor(annotation = ContextConfiguration.class, attribute = "initializers")
	Class<? extends ApplicationContextInitializer<? extends ConfigurableApplicationContext>>[] initializers() default {};

	@AliasFor(annotation = ContextConfiguration.class, attribute = "inheritLocations")
	boolean inheritLocations() default true;

	@AliasFor(annotation = ContextConfiguration.class, attribute = "inheritInitializers")
	boolean inheritInitializers() default true;

	@AliasFor(annotation = ContextConfiguration.class, attribute = "name")
	String name() default "";

}
