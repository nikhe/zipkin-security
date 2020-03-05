package zipkin.module.security;

import com.linecorp.armeria.server.auth.AuthService;
import com.linecorp.armeria.server.auth.Authorizer;
import com.linecorp.armeria.server.auth.BasicToken;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.CompletableFuture.completedFuture;

@Configuration
@EnableConfigurationProperties(ZipkinSecurityProperties.class)
class ZipkinSecurityModule {

  /**
   * This protects the /api endpoints, while leaving others used by the UI and health-check
   * anonymous.
   */
  @Bean
  ArmeriaServerConfigurator securityConfigurator(ZipkinSecurityProperties props) {
    return (sb) -> {
      Authorizer<BasicToken> basicAuthorizer =
          (ctx, token) -> completedFuture(basicAuthorize(props, token));
      sb.routeDecorator().pathPrefix("/api")
          .build(AuthService.builder()
              .addBasicAuth(basicAuthorizer)
              .newDecorator());
    };
  }

  static boolean basicAuthorize(ZipkinSecurityProperties props, BasicToken token) {
    return props.getUsername().equals(token.username())
        && props.getPassword().equals(token.password());
  }
}
