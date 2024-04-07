# [ReasonedException][repo-url] [![Maven Central][mvn-img]][mvn-url] [![GitHub.io][io-img]][io-url] [![CI Status][ci-img]][ci-url] [![MIT License][mit-img]][mit-url]

The exception class with reason.

In Java programming, it's quite cumbersome to define exception classes for
various exception cases. However, handling multiple exception cases
with one exception class can make it difficult to identify specific cases.

The `ReasonedException` class provided by this package resolves these issues by
accepting a `Record` object representing the reason as an argument.
Since the `Record` class can be defined with minimal implementation, defining a
`Record` class for each exception case and passing it to `ReasonedException`
enables to create exception objects that can identify the specific exception
cases.

In addition, the `Record` class allows to define fields, their getters, and
constructors without need for cumbersome implementation.
This means that a `Record` object as a reason can hold the values of variables
representing the situation when an exception occurs.


## Install

This package can be installed from [Maven Central Repository][mvn-url].

The examples of declaring that repository and the dependency on this package in
Maven `pom.xml` and Gradle `build.gradle` are as follows:

### for Maven

```
  <dependencies>
    <dependency>
      <groupId>io.github.sttk</groupId>
      <artifactId>reasonedexception</artifactId>
      <version>0.1.0</version>
    </dependency>
  </dependencies>
```

### for Gradle

```
repositories {
  mavenCentral()
}
dependencies {
  implementation 'io.github.sttk:reasonedexception:0.1.0'
}
```


## Usage

The following code creates a `ReasonedException` and throws it.

```
public class SampleClass {

  record IndexOutOfRange(int index, int min, int max) {}

  public void sampleMethod() throws ReasonedException {
    ...
    throw new ReasonedException(InvalidOutOfRange(i, 0, array.length));
  }
}
```

And the following code catches the exception and identifies the reason with
a switch expression.

```
  try {
    sampleMethod();
  } catch (ReasonedException e) {
    switch (e.getReason()) {
      case IndexOutOfRange r -> {
        int index = r.index();
        int min = r.min();
        int max = r.max();
        ...
      }
      ...
    }
  }
```


## Native build

This library supports native build with GraalVM.

See the following pages to setup native build environment on Linux/macOS or Windows.
- [Setup native build environment on Linux/macOS](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Setup native build environment on Windows](https://www.graalvm.org/latest/docs/getting-started/windows/#prerequisites-for-native-image-on-windows)

And see the following pages to build native image with Maven or Gradle.
- [Native image building with Maven plugin](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html)
- [Native image building with Gradle plugin](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)

**NOTE:** If serialization for `ReasonedException` is used, it is needed to
specify to specify the serialization settings for derived classes of `Record`
indicating the reason and derived classes of `Throwable` indicating the causing
exception classes in the `serialization-config.json` file.

## Supporting JDK versions

This framework supports JDK 21 or later.

### Actually checked JDK versions:

- GraalVM CE 21.0.2+13.1 (openjdk version 21.0.2)


## License

Copyright (C) 2024 Takayuki Sato

This program is free software under MIT License.<br>
See the file LICENSE in this distribution for more details.


[repo-url]: https://github.com/sttk/reasonedexception
[mvn-img]: https://img.shields.io/badge/maven_central-0.1.0-276bdd.svg
[mvn-url]: https://central.sonatype.com/artifact/io.github.sttk/reasonedexception/0.1.0
[io-img]: https://img.shields.io/badge/github.io-Javadoc-4d7a97.svg
[io-url]: https://sttk.github.io/reasonedexception/
[ci-img]: https://github.com/sttk/reasonedexception/actions/workflows/java-ci.yml/badge.svg?branch=main
[ci-url]: https://github.com/sttk/reasonedexception/actions
[mit-img]: https://img.shields.io/badge/license-MIT-green.svg
[mit-url]: https://opensource.org/licenses/MIT
