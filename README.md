# zipkin-security

## Overview

This is an example module that can be added to a [Zipkin Server](https://github.com/openzipkin/zipkin/tree/master/zipkin-server) deployment to secure the HTTP POST endpoint with a password.

## This is not recommended for production

This is example code and not meant to recommend security practice or how to manage credentials. Most typically, people will not customize the server. Instead, they will use authentication proxies that work with their corporate security infrastructure. This might be OAuth or some other form of authentication. One reason we have an [NGINX example](https://github.com/openzipkin/zipkin/tree/master/docker/lens) is to showcase use of a proxy.

The only reason this example was created was [so many keep asking to modify the server](https://github.com/openzipkin/zipkin/issues/782). They see Zipkin server uses Spring Boot and expect it to be the same as a normal application, when our server isn't normal. This code shows changing something like authentication has less to do with Spring Boot and more to do with the internals of how we implement our server, notably Armeria, and how we internally manage modules.

## Do not pay too much attention that this uses a properties file
The "zipkin-server-security.properties" file is only here as a toy: your real code will likely read from another, possibly encrypted, source that can be updated at runtime. If you make code based on this, and properties aren't working for you, just change the code to what does work for you.

## This is not supported by OpenZipkin
If you raise an issue against this repository, it might be answered, but please don't ask OpenZipkin chat for support. This is not an OpenZipkin supported project, rather a personal example.

Rather than asking questions here about Spring Boot topics such as how to set properties, please look at [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) or the many answers available on StackOverflow or other Spring Boot forums.

Finally, using this is more difficult than an alternative such as an authenticated proxy. That's precisely why it is not supported. If the struggle is too difficult, or keeping up to date is difficult, please consider using another approach. The way this project works is very sensitive to versions and considered an implementation detail of Zipkin.

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

See the [Running](#running) section for more examples.

After executing these steps, HTTP POST access will require a password
 * http://localhost:9411/api/v2/spans (or the legacy endpoint http://localhost:9411/api/v1/spans)

Ex. Unless you pass the default password, POST requests will fail with a 401: Here's the default password:
```bash
$ curl -u zipkin:harpoon -X POST -s localhost:9411/api/v2/spans -H'Content-Type: application/json' -d '[]'
```

### Configuration
This example only uses two Spring properties:

|Spring property           | Value                                 |
|--------------------------|---------------------------------------|
|zipkin.api.username | Basic Auth username. Default: zipkin  |
|zipkin.api.password | Basic Auth password. Default: harpoon |

You can change the default credentials by editing "zipkin-server-security.properties" and restarting.

The Zipkin server can be further configured as described in the
[Zipkin server documentation](https://github.com/openzipkin/zipkin/blob/master/zipkin-server/README.md).

### Running
Here is how to run Zipkin with the security module in Shell and PowerShell (Windows).

*Note* In both cases, this uses the "zipkin-server-security.properties" file in the current directory for the username and password. Edit this to change it.

Shell (`sh` or `bash`):
``` bash
java -Dloader.path='security.jar,security.jar!/lib' -Dspring.profiles.active=security -cp zipkin.jar org.springframework.boot.loader.PropertiesLauncher
```

PowerShell (Windows):
``` bash
java '-Dloader.path=security.jar,security.jar!/lib' '-Dspring.profiles.active=security' -cp zipkin.jar org.springframework.boot.loader.PropertiesLauncher
```

After either of these, uploads to the server will be constrained to the password:

```bash
# this is unauthorized as it doesn't have a password
curl -X POST -s localhost:9411/api/v2/spans -H'Content-Type: application/json' -d '[]'
# this is unauthorized as it has the wrong password
curl -u flash:thunder -X POST -s localhost:9411/api/v2/spans -H'Content-Type: application/json' -d '[]'
# this is authorized as it has the right password
curl -u zipkin:harpoon -X POST -s localhost:9411/api/v2/spans -H'Content-Type: application/json' -d '[]'
```
