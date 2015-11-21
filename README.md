# `spring-test-junit5`

This project serves as a proof of concept for how the _Spring TestContext Framework_
can be fully integrated into the current [JUnit 5 Prototype] using a single `TestExtension`.

Currently, all that's needed to use the _Spring TestContext Framework_ with JUnit 5
is to annotate your JUnit 5 based test class with `@ExtendWith(SpringExtension.class)`
(see [`SpringExtension`]) and whatever Spring annotations you need (i.e., typically at
least `@ContextConfiguration`). See the source code of [`SpringExtensionTests`] for an
example.

# Running Tests with Gradle

Executing `gradlew clean test` from the command line should result in output similar to the following.

```
Test execution started. Number of static tests: 2
Engine started: junit5
Test started:     applicationContextInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#applicationContextInjected()]
Test succeeded:   applicationContextInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#applicationContextInjected()]
Test started:     springBeansInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#springBeansInjected()]
Test succeeded:   springBeansInjected [junit5:org.springframework.test.context.junit5.SpringExtensionTests#springBeansInjected()]
Engine finished: junit5
Test execution finished.

Test run finished after 810 ms
[         2 tests found     ]
[         2 tests started   ]
[         0 tests skipped   ]
[         0 tests aborted   ]
[         2 tests successful]
[         0 tests failed    ]
```

# Running Tests in the IDE

In order to execute the tests within an IDE, please uncomment
`// @RunWith(JUnit5.class)` in the [`SpringExtensionTests`] test class.


[JUnit 5 Prototype]: https://github.com/junit-team/junit-lambda/wiki/Prototype
[`SpringExtension`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit5/SpringExtension.java
[`SpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTests.java
