/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.springframework.test.context.junit5.web;

import org.springframework.test.context.junit5.Person;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sam Brannen
 * @since 5.0
 */
@RestController
class PersonController {

	@RequestMapping("/person/{id}")
	public Person getPerson(@PathVariable long id) {
		return new Person("Dilbert");
	}

}