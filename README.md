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
Test run finished after 512 ms
[         4 tests found     ]
[         0 tests skipped   ]
[         4 tests started   ]
[         0 tests aborted   ]
[         4 tests successful]
[         0 tests failed    ]
```

# Running Tests in the IDE

In order to execute the tests within an IDE, simply run [`SpringExtensionTestSuite`] as a JUnit 4 test class.


[JUnit 5]: https://github.com/junit-team/junit-lambda
[`SpringExtension`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit5/SpringExtension.java
[`SpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTests.java
[`SpringExtensionTestSuite`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTestSuite.java
