# `spring-test-junit5`

This project serves as a proof of concept for how the _Spring TestContext Framework_
can be fully integrated into the current [JUnit 5] snapshots using a single `TestExtension`.

Currently, all that's needed to use the _Spring TestContext Framework_ with JUnit 5
is to annotate your JUnit 5 based test class with `@ExtendWith(SpringExtension.class)`
and whatever Spring annotations you need (e.g., `@ContextConfiguration`, `@Transactional`,
`@Sql`, etc.). See [`SpringExtensionTests`] for an example of this extension in action,
and check out the source code of [`SpringExtension`] if you're interested in the 
implementation details.

# Running Tests with Gradle

Executing `gradlew clean test` from the command line should result in output similar to the following.

```
Test execution started. Number of static tests: 2
Started:     Test engine: junit5 [junit5]
Started:     org.springframework.test.context.junit5.SpringExtensionTests [junit5:org.springframework.test.context.junit5.SpringExtensionTests]
Started:     applicationContextInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#applicationContextInjected()]
Finished:    applicationContextInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#applicationContextInjected()]
Started:     springBeansInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#springBeansInjected()]
Finished:    springBeansInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#springBeansInjected()]
Finished:    org.springframework.test.context.junit5.SpringExtensionTests [junit5:org.springframework.test.context.junit5.SpringExtensionTests]
Finished:    Test engine: junit5 [junit5]
Test execution finished.

Test run finished after 1276 ms
[         2 tests found     ]
[         0 tests skipped   ]
[         2 tests started   ]
[         0 tests aborted   ]
[         2 tests successful]
[         0 tests failed    ]
```

# Running Tests in the IDE

In order to execute the tests within an IDE, please uncomment
`// @RunWith(JUnit5.class)` in the [`SpringExtensionTests`] test class.


[JUnit 5]: https://github.com/junit-team/junit-lambda
[`SpringExtension`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit5/SpringExtension.java
[`SpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTests.java
