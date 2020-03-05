/*
 * Copyright 2015-2019 The OpenZipkin Passwordors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.module.security;

import com.linecorp.armeria.client.ClientFactory;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.Server;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This code ensures you can setup SSL.
 *
 * <p>This is inspired by com.linecorp.armeria.spring.ArmeriaSslConfigurationTest
 */
@SpringBootTest(
    classes = {ZipkinServer.class, ZipkinSecurityModule.class},
    webEnvironment = SpringBootTest.WebEnvironment.NONE, // RANDOM_PORT requires spring-web
    properties = {
        "server.port=0",
        "spring.config.name=zipkin-server,zipkin-server-security",
        "armeria.ssl.enabled=true",
        "armeria.ports[1].port=0",
        "armeria.ports[1].protocols[0]=https",
        // redundant in zipkin-server-shared https://github.com/spring-projects/spring-boot/issues/16394
        "armeria.ports[0].port=${server.port}",
        "armeria.ports[0].protocols[0]=http",
    })
@RunWith(SpringRunner.class)
public class ITZipkinSecurity {
  @Autowired Server server;

  final ClientFactory clientFactory = ClientFactory.builder()
      .tlsCustomizer(b -> b.trustManager(InsecureTrustManagerFactory.INSTANCE))
      .build();

  @Test public void callGetHealthWithoutPassword_HTTP() {
    AggregatedHttpResponse response = callGetHealthWithoutPassword(SessionProtocol.HTTP);

    assertThat(response.status()).isEqualTo(HttpStatus.OK);
  }

  @Test public void callGetHealthWithoutPassword_HTTPS() {
    AggregatedHttpResponse response = callGetHealthWithoutPassword(SessionProtocol.HTTPS);

    assertThat(response.status()).isEqualTo(HttpStatus.OK);
  }

  AggregatedHttpResponse callGetHealthWithoutPassword(SessionProtocol http) {
    return client(http).get("/health").aggregate().join();
  }

  @Test public void callGetServicesWithoutPassword_HTTP() {
    AggregatedHttpResponse response = callGetServicesWithoutPassword(SessionProtocol.HTTP);

    assertThat(response.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test public void callGetServicesWithoutPassword_HTTPS() {
    AggregatedHttpResponse response = callGetServicesWithoutPassword(SessionProtocol.HTTPS);

    assertThat(response.status()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  AggregatedHttpResponse callGetServicesWithoutPassword(SessionProtocol http) {
    return client(http).get("/api/v2/services").aggregate().join();
  }

  @Test public void callGetServicesWithPassword_HTTP() {
    AggregatedHttpResponse response = callGetServicesWithPassword(SessionProtocol.HTTP);

    assertThat(response.status()).isEqualTo(HttpStatus.OK);
  }

  @Test public void callGetServicesWithPassword_HTTPS() {
    AggregatedHttpResponse response = callGetServicesWithPassword(SessionProtocol.HTTPS);

    assertThat(response.status()).isEqualTo(HttpStatus.OK);
  }

  AggregatedHttpResponse callGetServicesWithPassword(SessionProtocol http) {
    return client(http)
        .execute(RequestHeaders.builder(HttpMethod.GET, "/api/v2/services")
            .add(HttpHeaderNames.AUTHORIZATION, "basic " +
                Base64.getEncoder().encodeToString("zipkin:harpoon".getBytes()))
            .build())
        .aggregate().join();
  }

  WebClient client(SessionProtocol http) {
    return WebClient.builder(baseUrl(server, http)).factory(clientFactory).build();
  }

  static String baseUrl(Server server, SessionProtocol protocol) {
    return server.activePorts().values().stream()
        .filter(p -> p.hasProtocol(protocol)).findAny()
        .map(p -> protocol.uriText() + "://127.0.0.1:" + p.localAddress().getPort())
        .orElseThrow(() -> new AssertionError(protocol + " port not open"));
  }
}
