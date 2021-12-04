# [sttk-java/reasonederror][repo-url] [![GitHub.io][io-img]][io-url] [![CI Status][ci-img]][ci-url] [![MIT License][mit-img]][mit-url]

The error processing library for Java.


- [What is this?](#what-is-this)
- [Features](#features)
- [Usage](#usage)
- [Resolve dependency for your project using this library](#resolve-dep)
- [Supporting JDK versions](#supporting-jdk-versions)
- [License](#license)


<a name="what-is-this"></a>
## What is this?

This is a library for error processes in Java program.

The central class of this library is `ReasonedException`.
This class uses an `enum` value as a reason of an error.
Thereby this class makes it easy to define many errors for verious situations without creating new exception classes.
In addition, to supplement this reason by an `enum` value, `ReasonedException` can also take some parameters which represents a situation when an error is caused.


<a name="features"></a>
## Features

This library provides the following features:

- Exception with a reason
- Creation notification


### Exception with a reason

`ReasonedException` in this library is an exception class with a reason of an error.
A reason is defined by an `enum` value.
The name of the `enum` value indicates what the reason is.
Since An `enum` value is always unique in a Java program, a reason by an `enum` value can identify an error.
This will free you from efforts to implement many exception classes for various errors.

By defining an `enum` value for a reason in a class which throws the `ReasonedException`, the class can be identified with the `enum` value because an enclosing class of an `enum` type can be solved with Java reflection.

`ReasonedException` does not have public constructors, but has `by` static method to create this instance instead.
Also, it has `with` static method to take some parameters which represents a situation when an error is caused.
These parameters are received in forms of key-value pairs, and can be obtained with `#getSituation` or `#getSituationValue` methods.


### Creation notification

By register `CreationHandler`s to `ReasonedExceptionConfiguration`, these handlers are notified whenever `ReasonedException`s are created.

There are two methods in `ReasonedExceptionConfiguration` to register `CreationHandler`s: `#addSyncHandler` and `#addAsyncHandler`.
`#addSyncHandler` is for a handler which is executed synchronously just after a `ReasonedException` is created, and `#addAsyncHandler` is for a handler executed asynchronously.
These methods are effective only before calling `#fix`.


<a name="usage"></a>
## Usage

This section explains the usage of classes in this library.


### Creates and throws a `ReasonedException`

At first, defines `enum` values which represent reasons of errors.
It is desirable that these `enum` values are defined in the class in which exceptions for the reasons are caused, because it makes possible to solve the causing class from the package path of the `enum` type.

```
  // Defines error reasons by enum.
  public enum Error {
    FailToDoSomething,
    ...
  }
```

A way to create and throw a `ReasonedException` is as follows:

```
  throw ReasonedException.by(Error.FailToDoSomething);
```

If there are parameters which help to know a situation when an error is caused, pass them by using `with` method:

```
  throw ReasonedException
    .with("paramName1", paramValue1)
    .with("paramName2", paramValue2)
    .by(Error.FailToDoSomething);
```

If there is a causal exception, pass it to `.by` method:

```
  try {
    ...
  } catch (Exception e) {
    throw ReasonedException
      .with("paramName1", paramValue1)
      .with("paramName2", paramValue2)
      .by(Error.FailToDoSomething, e);
  }
```


### Notify a creation of `ReasonedException`

Registers creation handlers to `ReasonedExceptionConfiguration` in start-up process of an applition.

```
  var config = new ReasonedExceptionConfiguration();
  config.addSyncHandler((exc, odt) -> {
    // (1)
  });
  config.addAsyncHandler((exc, odt) -> {
    // (2)
  });
  config.fix();  // fixes configuration to disable to add more handlers.
```

Whenever a `ReasonedException` is created, these registered handlers are called.
The `(1)` handler is executed synchronously just after creating, and `(2)` is executed asynchronously in another thread.


<a name="resolve-dep"></a>
## Resolve dependency for your project using this library

This library is registered in Maven 2 compatible repository named: [local-m2-repository][m2-repo-url].
This repository needs to be git-cloned and located in local directory.
This section assumes that this repository is located just under the parent directory of your project root directory (`${project.root.dir}/../local-m2-repository`).


### for Maven

`pom.xml`:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <repositories>
    <repository>
      <id>local-m2-repository</id>
      <url>file://${user.dir}/../local-m2-repository</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>sttk-java</groupId>
      <artifactId>reasonederror</artifactId>
      <version>0.1.0</version>
      <scope>compile</scope>
    </dependency>
  </dependencies>
</project>
```

### for Gradle

`build.gradle`:

```
repositories {
  maven {
    url "../local-m2-repository"
  }
}

dependencies {
  implementation "sttk-java:reasonederror:0.1.0"
}
```

### for Apache Ivy

`build.xml`:

```
<project name="your-project" xmlns:ivy="antlib:org.apache.ivy.ant">
  <target name="resolve">
    <ivy:retrieve/>
  </target>
</project>
```

`ivy.xml`:

```
<ivy-module version="2.0">
  <info organisation="your-group" module="your-module"/>
  <dependencies>
    <dependency org="sttk-java" name="reasonederror" rev="0.1.0" conf="default"/>
  </dependencies>
</ivy-module>
```

`ivysettings.xml`:

```
<ivysettings>
  <settings defaultResolver="local-m2-repository" />
  <resolvers>
    <filesystem name="local-m2-repository" m2compatible="true">
      <artifact pattern="${basedir}/../local-m2-repository/[organisation]/[module]/[revision]/[module]-[revision](-[classifier]).[ext]" />
    </filesystem>
  </resolvers>
</ivysettings>
```


<a name="supporting-jdk-versions"></a>
## Supporting JDK versions

This library supports JDK 11 or later.

### Actually Checked JDK versions

- Oracle OpenJDK 18.ea.26　　*※ not check coverage because jacoco does not work*
- Oracle OpenJDK 17.0.1
- Oracle OpenJDK 16.0.2
- Oracle OpenJDK 11.0.2
- Amazon Corretto 17.0.1.12.1
- Amazon Corretto 16.0.2.7.1
- Amazon Corretto 11.0.12.7.2
- Microsoft 16.0.2.7.1
- Microsoft 11.0.13
- Temurin 17.0.1
- Temurin 16.0.2
- Temurin 11.0.13
- GraalVM 21.3.0.r17
- GraalVM 21.2.0.r16
- GraalVM 21.3.3.r11


<a name="license"></a>
## License

Copyright (C) 2021 Takayuki Sato

This program is free software under MIT License.
See the file LICENSE in this distribution for more details.


[repo-url]: https://github.com/sttk-java/reasonederror
[ci-img]: https://github.com/sttk-java/reasonederror/actions/workflows/java-ci.yml/badge.svg?branch=main
[ci-url]: https://github.com/sttk-java/reasonederror/actions
[io-img]: https://img.shields.io/badge/github.io-Javadoc-4d7a97.svg
[io-url]: https://sttk-java.github.io/reasonederror/
[mit-img]: https://img.shields.io/badge/license-MIT-green.svg
[mit-url]: https://opensource.org/licenses/MIT

[m2-repo-url]:https://github.com/sttk-java/local-m2-repository
