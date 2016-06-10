# Spring JUnit 5 Testing Support

This project serves as the official prototype for [JUnit 5][] testing support
in the [Spring TestContext Framework][] which will eventually be incorporated
into [Spring Framework][] 5.0 in conjunction with [SPR-13575][].

# Using the `SpringExtension`

Currently, all that's needed to use the _Spring TestContext Framework_ with JUnit 5
is to annotate a JUnit 5 based test class with `@ExtendWith(SpringExtension.class)`
and whatever Spring annotations you need (e.g., `@ContextConfiguration`, `@Transactional`,
`@Sql`, etc.). See [`SpringExtensionTests`] for an example of this extension in action,
and check out the source code of [`SpringExtension`] if you're interested in the 
implementation details.

## Composing Annotations from Spring & JUnit

Spring has supported [composed annotations] for several years now, and as of JUnit 5
annotations in JUnit can also be used as meta-annotations. We can therefore create
custom annotations that are composed from Spring annotations **and** JUnit 5
annotations. Take a look at [`@SpringJUnit5Config`] for an example, and check out
[`ComposedSpringExtensionTests`] for an example of `@SpringJUnit5Config` in action.

# License

This project is released under version 2.0 of the [Apache License][].

# Artifacts

There are currently no downloadable artifacts for this project.
However, if you install in a local Maven repository (see below)
the generated artifact will correspond to the following.

 - **Group ID**: `org.springframework.test`
 - **Artifact ID**: `spring-test-junit5`
 - **Version**: `1.0.0.BUILD-SNAPSHOT`

# Building from Source

This project uses a [Gradle][]-based build system. In the instructions
below, `./gradlew` is invoked from the root of the project and serves as
a cross-platform, self-contained bootstrap mechanism for the build.

## Prerequisites and Dependencies

- [Git][]
- [JDK 8][JDK8] update 60 or later
- [JUnit 5][] `5.0.0-SNAPSHOT`
- [Spring Framework][] `4.3.0.RELEASE`

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## Compile and Test

Build all JARs, distribution ZIP files, and docs:

`./gradlew build`

## Install `spring-test-junit5` in local Maven repository

`./gradlew install`

# Running Tests with Gradle

Executing `gradlew clean test` from the command line should result in output similar to the following.

```
:junit5Test

Test run finished after 1295 ms
[        24 tests found     ]
[         0 tests skipped   ]
[        24 tests started   ]
[         0 tests aborted   ]
[        24 tests successful]
[         0 tests failed    ]
```

# Running Tests in the IDE

In order to execute the tests within an IDE, simply run [`SpringExtensionTestSuite`] as a JUnit 4 test class.

----

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[composed annotations]: https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model#composed-annotations
[Git]: http://help.github.com/set-up-git-redirect
[Gradle]: http://gradle.org
[JDK8]: http://www.oracle.com/technetwork/java/javase/downloads
[JUnit 5]: https://github.com/junit-team/junit5
[SPR-13575]: https://jira.spring.io/browse/SPR-13575
[Spring Framework]: http://projects.spring.io/spring-framework/
[Spring TestContext Framework]: http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#testcontext-framework
[`@SpringJUnit5Config`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit5/SpringJUnit5Config.java
[`ComposedSpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/ComposedSpringExtensionTests.java
[`SpringExtensionTestSuite`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTestSuite.java
[`SpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit5/SpringExtensionTests.java
[`SpringExtension`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit5/SpringExtension.java
