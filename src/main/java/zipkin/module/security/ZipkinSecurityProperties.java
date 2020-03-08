package zipkin.module.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("zipkin.api")
class ZipkinSecurityProperties {
  String username = "zipkin";
  String password = "harpoon";

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
