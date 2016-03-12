/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.springframework.test.context.junit5;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AliasFor;

/**
 * Annotation used to signal that a single method parameter should be autowired
 * by Spring's dependency injection facilities.
 *
 * <p><strong>WARNING</strong>: {@code @SpringBean} is a <em>temporary solution</em>
 * until {@code @Autowired} is supported on parameters.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see Autowired
 * @see Qualifier
 */
@Autowired
@Qualifier
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpringBean {

	/**
	 * Alias for {@link Qualifier#value}.
	 */
	@AliasFor(annotation = Qualifier.class, attribute = "value")
	String value() default "";

	/**
	 * Alias for {@link Qualifier#value}.
	 */
	@AliasFor(annotation = Qualifier.class, attribute = "value")
	String qualifier() default "";

	/**
	 * Alias for {@link Autowired#required}.
	 */
	@AliasFor(annotation = Autowired.class, attribute = "required")
	boolean required() default true;

}
