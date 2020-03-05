package zipkin.module.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zipkin.collector.http")
class ZipkinSecurityProperties {
  private String username;
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
