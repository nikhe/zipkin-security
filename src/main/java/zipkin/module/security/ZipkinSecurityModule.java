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

  @Bean ArmeriaServerConfigurator securityConfigurator(ZipkinSecurityProperties props) {
    return sb -> {
      // NOTE: this actually blocks everything, not just POST!
      Authorizer<BasicToken> basicAuthorizer = (ctx, token) -> completedFuture(
          props.getUsername().equals(token.username()) && props.getPassword()
              .equals(token.password()));
      sb.decorator(AuthService.builder().addBasicAuth(basicAuthorizer).newDecorator());
    };
  }
}
