# zipkin-security

## Overview

This is an example module that can be added to a [Zipkin Server](https://github.com/openzipkin/zipkin/tree/master/zipkin-server) deployment to secure the HTTP POST endpoint with a password.

## This is not recommended for production

This is example code and not meant to recommend security practice or how to manage credentials. Most typically, people will not customize the server. Instead, they will use authentication proxies that work with their corporate security infrastructure. This might be OAuth or some other form of authentication. One reason we have an [NGINX example](https://github.com/openzipkin/zipkin/tree/master/docker/lens) is to showcase use of a proxy.

The only reason this example was created was [so many keep asking to modify the server](https://github.com/openzipkin/zipkin/issues/782). They see Zipkin server uses Spring Boot and expect it to be the same as a normal application, when our server isn't normal. This code shows changing something like authentication has less to do with Spring Boot and more to do with the internals of how we implement our server, notably Armeria, and how we internally manage modules.

## Do not pay too much attention that this uses ENV variables
ENV variables are used only here as a toy: your real code will likely read from another source. If you make code based on this, and ENV variables aren't working for you, just change the code to what does work for you.

## This is not supported by OpenZipkin
If you decide to make a custom add-on instead of an alternate like a proxy, you are welcome to use this if it helps. However, it will be up to you to support it. The way we connect modules together is very sensitive to versions and considered an implementation detail. The OpenZipkin community supports our default builds, not custom ones.

Please don't ask our chat for Spring Boot support, like how to set properties instead of ENV variables etc. [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) covers that, if helpful to you. We don't have enough resources to support custom servers, so this code is "use at your own risk" technology.

## Quick start

JRE 8 is required to run Zipkin server.

Fetch the latest released
[executable jar for Zipkin server](https://search.maven.org/remote_content?g=io.zipkin.java&a=zipkin-server&v=LATEST&c=exec)
and build the module jar for security.

For example (from the project root):

```bash
$ curl -sSL https://zipkin.io/quickstart.sh | bash -s
$ ./mvnw clean install -DskipTests
$ cp target/*module.jar security.jar
$ java \
    -Dloader.path='security.jar,security.jar!/lib' \
    -Dspring.profiles.active=security \
    -cp zipkin.jar \
    org.springframework.boot.loader.PropertiesLauncher
```

After executing these steps, http POST access will require a password
 * http://localhost:9411/api/v2/spans (or the legacy endpoint http://localhost:9411/api/v1/spans)

The Zipkin server can be further configured as described in the
[Zipkin server documentation](https://github.com/openzipkin/zipkin/blob/master/zipkin-server/README.md).

### Configuration

Configuration can be applied either through environment variables or an external Zipkin
configuration file. The module includes default configuration that can be used as a 
[reference](src/main/resources/zipkin-server-security.yml)
for users that prefer a file based approach.

#### Environment Variables

|Environment Variable | Value                                 |
|---------------------|---------------------------------------|
|ZIPKIN_HTTP_USERNAME | Basic Auth username. Default: zipkin  |
|ZIPKIN_HTTP_PASSWORD | Basic Auth password. Default: harpoon |

### Running

```bash
$ java \
    -Dloader.path='security.jar,security.jar!/lib' \
    -Dspring.profiles.active=security \
    -cp zipkin.jar \
    org.springframework.boot.loader.PropertiesLauncher
```
