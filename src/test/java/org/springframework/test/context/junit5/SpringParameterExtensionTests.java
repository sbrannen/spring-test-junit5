package org.springframework.test.context.junit5;

import org.junit.gen5.api.Disabled;
import org.junit.gen5.api.Test;
import org.junit.gen5.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.gen5.api.Assertions.assertEquals;
import static org.junit.gen5.api.Assertions.assertNotNull;
import static org.junit.gen5.api.Assertions.assertSame;

/**
 * @author Tadaya Tsuyukubo
 */
@ExtendWith(SpringParameterExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class SpringParameterExtensionTests {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void applicationContextInjectedToMethodParameter(ApplicationContext paramApplicationContext) {
		assertSame(this.applicationContext, paramApplicationContext, "same ApplicationContext should be resolved in parameter");
	}

	@Test
	void springBeansInjectedToParameterWithQualifier(@Qualifier("dilbert") Person dilbert) {
		assertNotNull(dilbert, "Person should be resolved in method parameter");
		assertEquals("Dilbert", dilbert.getName(), "Person's name");
	}

	@Test
	@Disabled  // generics is not supported yet
	void springBeansInjectedToParameterWithGenerics(List<Person> people) {
		assertEquals(2, people.size(), "Number of Person objects in context");
	}

}
