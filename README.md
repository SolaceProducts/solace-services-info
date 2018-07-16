

[![Build Status](https://travis-ci.org/SolaceProducts/solace-services-info.svg?branch=master)](https://travis-ci.org/SolaceProducts/solace-services-info)

# Solace Services Info

Solace Services Info is a lightweight library which defines the common models used to describe Solace PubSub+ service connections. Packaged within this project is also a utility which non-cloud-platform-deployed applications can use to fetch Solace PubSub+ service credentials.

## Contents

* [Overview](#overview)
* [Manifest Load Order and Expected Formats](#manifest-load-order-and-expected-formats)
* [Using it in your Application](#using-it-in-your-application)
* [Checking out and Building](#checking-out-and-building)
* [Contributing](#contributing)
* [Release Notes & Versioning](#release-notes-and-versioning)
* [Authors](#authors)
* [License](#license)
* [Resources](#resources)


---

## Overview

### The Models

This project contains within it a few models to represent the credentials for Solace PubSub+ services:

<dl>
    <dt>SolaceServiceCredentials</dt>
    <dd>An interface that represents the general expected contents of the credentials to a Solace PubSub+ service.</dd>
    <dt>SolaceServiceCredentialsImpl</dt>
    <dd>A Plain Old Java Object (POJO) implementation of SolaceServiceCredentials.</dd>
</dl>

### Solace PubSub+ Service Credentials Loader

The Solace PubSub+ Service Credentials Loader is a utility that attempts to standardize the way in which applications can fetch Solace PubSub+ service credentials from its environment.

This project is intended to be used in applications hosted on environments that do not have a standardized means of connecting to external services. For example, an application deployed in Cloud Foundry can use existing Cloud Foundry tools (e.g. [User-Provided Services](https://docs.cloudfoundry.org/devguide/services/user-provided.html)) to connect to external services and would have little value in using this project. Whereas, for example, if it were instead to be ran directly on a Windows or Unix environment, then this project could be used to skip the overhead required to fetch and marshal Solace PubSub+ service credentials.

The primary way of using this loader is through the [SolaceCredentialsLoader](src/main/java/com/solace/services/core/loader/SolaceCredentialsLoader.java). For example:

```java
SolaceCredentialsLoader solaceCredentialsLoader = new SolaceCredentialsLoader();

// Gets the map of service ID to credentials for all detected Solace PubSub+ services
Map<String, SolaceServiceCredentials> solaceServicesCredentials = solaceCredentialsLoader.getAllSolaceServiceInfo();

// Gets the credentials for the first detected Solace PubSub+ service
SolaceServiceCredentials solaceServicesCredentials = solaceCredentialsLoader.getSolaceServiceInfo();
```

The two following projects are real examples that use this as one of the options for fetching service credentials to auto-configure Solace Java/JMS connections:
* [Spring Boot Auto-Configuration for the Solace Java API](https://github.com/SolaceProducts/solace-java-spring-boot)
* [Spring Boot Auto-Configuration for the Solace JMS API](https://github.com/SolaceProducts/solace-jms-spring-boot)

Please refer to [Manifest Load Order and Expected Formats](#manifest-load-order-and-expected-formats) for more information about the supported manifest formats and the ways in which you can provide it.

## Manifest Load Order and Expected Formats

The credentials loader for Solace PubSub+ services works in two phases:
1. Fetch the raw JSON manifest from the application's environment as per a predefined lookup order.
2. Marshal the raw JSON manifest into one or more `SolaceServiceCredentials`.

The order in which the loader searches its environment is currently defined as follows:

1. `SOLCAP_SERVICES` as a JVM property which directly contains the manifest itself.
2. `SOLCAP_SERVICES` as an OS environment which directly contains the manifest itself.
3. `SOLACE_SERVICES_HOME` as a JVM property that specifies a path to a directory containing a `.solaceservices` file.
4. `SOLACE_SERVICES_HOME` as an OS environment that specifies a path to a directory containing a `.solaceservices` file.
5. Fallback of searching for a `.solaceservices` file in the user's home directory.

Now to marshal the JSON into `SolaceServiceCredentials` objects, the manifest must conform to one of the following formats:

| Manifest Format | Manifest Detection Handle | Service ID Resolution Order | Example |
| --------------- | ------------------------- | ------------------------------------| ------ |
| `VCAP_SERVICES`-Formatted Map of Services | An object-type root node with key "solace-pubsub" | <ol><li>An ID in the credentials</li><li>The service's meta-name</li><li>`{VPN-name}@{active-management-hostname}`</li></ol> | [Link](samples/vcap-formatted-manifest.json) |
| Array of Service Credentials | An array-type root node | <ol><li>An ID in the credentials</li><li>`{VPN-name}@{active-management-hostname}`</li></ol> | [Link](samples/service-credentials-list-manifest.json) |
| Service Credentials for a Single Service | Default | <ol><li>An ID in the credentials</li><li>`{VPN-name}@{active-management-hostname}`</li></ol> | [Link](samples/single-service-credentials-manifest.json) |


## Using it in your Application

The releases from this project are hosted in [Maven Central](https://mvnrepository.com/artifact/com.solace.cloud.core/solace-services-info).

### Using it with Gradle

```groovy
// Solace Services Info
compile("com.solace.cloud.core:solace-services-info:0.1.+")
```

### Using it with Maven

```xml
<!-- Solace Services Info -->
<dependency>
  <groupId>com.solace.cloud.core</groupId>
  <artifactId>solace-services-info</artifactId>
  <version>0.1.+</version>
</dependency>
```

## Checking out and Building

This project depends on maven for building. To build the jar locally, check out the project and build from source by doing the following:

    git clone https://github.com/SolaceProducts/solace-services-info.git
    cd solace-services-info
    mvn package

This will build a jar file which will be named similar to the following:

```
target/solace-services-info-0.2.0-SNAPSHOT.jar
```

You can install this file in your maven repository locally.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Release Notes and Versioning

This project uses [SemVer](http://semver.org/) for versioning. For the versions available and corresponding release notes, see the [Releases in this repository](https://github.com/SolaceProducts/solace-services-info/releases).

## Authors

See the list of [contributors](https://github.com/SolaceProducts/solace-services-info/contributors) who participated in this project.

## License

This project is licensed under the Apache License, Version 2.0. - See the [LICENSE](LICENSE) file for details.

## Resources

For more information about Solace technology in general please visit these resources:

- The Solace Developer Portal website at: http://dev.solace.com
- Understanding [Solace technology.](http://dev.solace.com/tech/)
- Ask the [Solace community](http://dev.solace.com/community/).
