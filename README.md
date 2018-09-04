# Spring JUnit 5 Testing Support

This project served as the official prototype for [JUnit 5][] testing support
in the [Spring TestContext Framework][] and has been incorporated into
[Spring Framework][] 5.0 in conjunction with [SPR-13575][]. Consequently, no
further work is planned in this repository in terms of new features: new
features are only supported in Spring Framework 5.0+. Note, however, that this
project can in fact be used for JUnit Jupiter testing support in conjunction
with Spring Framework 4.3.x.

# Using the `SpringExtension`

Currently, all that's needed to use the _Spring TestContext Framework_ with JUnit 5
is to annotate a JUnit Jupiter based test class with `@ExtendWith(SpringExtension.class)`
and whatever Spring annotations you need (e.g., `@ContextConfiguration`, `@Transactional`,
`@Sql`, etc.), but make sure you use `@Test`, `@BeforeEach`, etc. from the appropriate
`org.junit.jupiter.api` package. See [`SpringExtensionTests`] for an example of this
extension in action, and check out the source code of [`SpringExtension`] if you're
interested in the  implementation details.

## Composing Annotations from Spring & JUnit

Spring has supported [composed annotations] for several years now, and as of JUnit 5
annotations in JUnit can also be used as meta-annotations. We can therefore create
custom annotations that are composed from Spring annotations **and** JUnit 5
annotations. Take a look at [`@SpringJUnitJupiterConfig`] for an example, and check out
[`ComposedSpringExtensionTests`] for an example of `@SpringJUnitJupiterConfig` in action.

# License

This project is released under version 2.0 of the [Apache License][].

# Artifacts

There are currently no downloadable artifacts for this project; however,
you may opt to install `spring-test-junit5` in your local Maven repository
or include a dependency on `spring-test-junit5` via JitPack. See the following
sections for further details.

## Local Maven Installation

If you install in a local Maven repository (see below)
the generated artifact will correspond to the following.

 - **Group ID**: `org.springframework.test`
 - **Artifact ID**: `spring-test-junit5`
 - **Version**: `1.0.0.BUILD-SNAPSHOT`

## JitPack

If you'd like to build against a release tag for `spring-test-junit5`, you
may be interested in using [JitPack][]. For example, to build against the
`1.2.0` tag, the following Maven coordinates will work.

 - **Group ID**: `com.github.sbrannen`
 - **Artifact ID**: `spring-test-junit5`
 - **Version**: `1.2.0`

### JitPack with Gradle

```groovy
repositories {
	mavenCentral()
	maven { url 'https://jitpack.io' }
}

// ...

dependencies {
	// ...
	testCompile('com.github.sbrannen:spring-test-junit5:1.2.0')
	// ...
}
```

### JitPack with Maven

```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>

<!-- ... -->

<dependencies>
	<dependency>
		<groupId>com.github.sbrannen</groupId>
		<artifactId>spring-test-junit5</artifactId>
		<version>1.2.0</version>
		<scope>test</scope>
	</dependency>

	<!-- ... -->

</dependencies>
```


# Building from Source

This project uses a [Gradle][]-based build system. In the instructions
below, `./gradlew` is invoked from the root of the project and serves as
a cross-platform, self-contained bootstrap mechanism for the build.

## Prerequisites and Dependencies

- [Git][]
- [JDK 8][JDK8]: update 121 or later
- [JUnit 5][]: JUnit Jupiter `5.2.0` and JUnit Platform `1.2.0`
- [Spring Framework][]: `4.3.18.RELEASE`

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

## Compile and Test

Compile code, run tests, and build JARs, distribution ZIP files, and docs:

`./gradlew build`

## Install `spring-test-junit5` in local Maven repository

`./gradlew install`

## Running Tests with Gradle

`./gradlew test`

## Building and Testing with JDK 9, 10, & 11

`spring-test-junit5` can also be built with and tested against JDK 9.0.4, JDK 10.0.2, and JDK 11 (build 11+28).
 
# Running Tests in the IDE

In order to execute all of the tests within an IDE as a single suite, simply run [`SpringExtensionTestSuite`] as a JUnit 4 test class.

----

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[composed annotations]: https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model#composed-annotations
[Git]: http://help.github.com/set-up-git-redirect
[Gradle]: http://gradle.org
[JDK8]: http://www.oracle.com/technetwork/java/javase/downloads
[JitPack]: https://jitpack.io/
[JUnit 5]: https://junit.org/junit5/
[SPR-13575]: https://jira.spring.io/browse/SPR-13575
[Spring Framework]: http://projects.spring.io/spring-framework/
[Spring TestContext Framework]: https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#testcontext-framework
[`@SpringJUnitJupiterConfig`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit/jupiter/SpringJUnitJupiterConfig.java
[`ComposedSpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit/jupiter/ComposedSpringExtensionTests.java
[`SpringExtensionTestSuite`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit/jupiter/SpringExtensionTestSuite.java
[`SpringExtensionTests`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/test/java/org/springframework/test/context/junit/jupiter/SpringExtensionTests.java
[`SpringExtension`]: https://github.com/sbrannen/spring-test-junit5/blob/master/src/main/java/org/springframework/test/context/junit/jupiter/SpringExtension.java
