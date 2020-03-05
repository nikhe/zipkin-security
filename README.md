# zipkin-security

## Overview

This is a module that can be added to a [Zipkin Server](https://github.com/openzipkin/zipkin/tree/master/zipkin-server)
deployment to secure the HTTP POST endpoint with a password.

## Experimental
* Note: This is currently experimental! *

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
[reference](https://github.com/adriancole/zipkin-security/tree/master/src/main/resources/zipkin-server-security.yml)
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
