package com.siriusxi.cloud.infra.eds.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final String username;
  private final String password;

  @Autowired
  public SecurityConfig(
      @Value("${app.eureka.user}") String username,
      @Value("${app.eureka.pass}") String password) {
    this.username = username;
    this.password = "{noop}".concat(password);
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
            .withUser(username)
            .password(password)
            .authorities("USER");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        // Disable CRCF to allow services to register themselves with Eureka
        .csrf()
          .disable()
        .authorizeRequests()
          .anyRequest().authenticated()
          .and()
          .httpBasic();
  }
}
