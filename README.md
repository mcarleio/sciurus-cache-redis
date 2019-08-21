# Sciurus Cache Redis

This is an implementation of Sciurus' CustomCache for Redis.

[![Maven Central][maven-image]][maven-url] 
[![License][license-image]](LICENSE)
[![Build status][travis-image]][travis-url]
[![Build status][codecov-image]][codecov-url]
[![Code Quality][codequality-image]][codequality-url]

## General Usage

You need to include Sciurus Cache Redis as dependency and declare Sciurus as an aspect library in the `aspectj-maven-plugin`:

1. Include Sciurus Cache Redis as dependency
    ```xml
    <dependency>
        <groupId>io.mcarle</groupId>
        <artifactId>sciurus-cache-redis</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```
    
2. Include aspectj-maven-plugin and define Sciurus as `aspectLibrary`
    ```xml
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>aspectj-maven-plugin</artifactId>
        <version>1.11</version>
        <executions>
            <execution>
                <goals>
                    <goal>compile</goal>
                    <goal>test-compile</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <complianceLevel>${maven.compiler.source}</complianceLevel>
            <source>${maven.compiler.source}</source>
            <target>${maven.compiler.target}</target>
            <aspectLibraries>
                <aspectLibrary>
                    <groupId>io.mcarle</groupId>
                    <artifactId>sciurus</artifactId>
                </aspectLibrary>
            </aspectLibraries>
        </configuration>
    </plugin>
    ```

## How to use?
First, see [the Sciurus project][sciurus-github] to see how to use Sciurus' cache functionality (you will also find a simple example project there).

To register [`RedisCache`](src/main/java/io/mcarle/sciurus/cache/redis/RedisCache.java) to Sciurus, you simply do 
```java
Sciurus.registerCache(
    "<name of your cache>",
    new RedisCache(RedisClient.create("redis://localhost:6379/0"))
);
```  

## Further inforamtion
This implementation for storing the cached values in Redis is using Java's serialization mechanism.
Therefore, you have to make sure that the classes adhere Java's serialization mechanism!

**Hint**: Sciurus will catch any exception and then simply executes the original method and return the result.
The exception is only logged and your application will more or less work, as if there is no cache enabled.


## License

Unless explicitly stated otherwise all files in this repository are licensed under the Apache Software License 2.0

Copyright 2019 Marcel Carlé

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[sciurus-github]: https://github.com/mcarleio/sciurus
[maven-image]: https://img.shields.io/maven-central/v/io.mcarle/sciurus-cache-redis.svg
[maven-url]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.mcarle%22%20a%3A%22sciurus-cache-redis%22
[license-image]: https://img.shields.io/github/license/mcarleio/sciurus-cache-redis.svg
[license-url]: http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.mcarle%22%20a%3A%22sciurus-cache-redis%22
[travis-image]: https://img.shields.io/travis/mcarleio/sciurus-cache-redis.svg
[travis-url]: https://travis-ci.org/mcarleio/sciurus-cache-redis
[codecov-image]: https://img.shields.io/codecov/c/github/mcarleio/sciurus-cache-redis.svg
[codecov-url]: https://codecov.io/gh/mcarleio/sciurus-cache-redis
[codequality-image]: https://scrutinizer-ci.com/g/mcarleio/sciurus-cache-redis/badges/quality-score.png?b=master
[codequality-url]: https://scrutinizer-ci.com/g/mcarleio/sciurus-cache-redis/?branch=master