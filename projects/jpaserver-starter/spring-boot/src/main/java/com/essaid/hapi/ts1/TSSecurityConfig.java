package com.essaid.hapi.ts1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@Configuration
//@Import({TSFSecurityFhirConfig.class, TSFSecurityRootConfig.class})
@EnableWebSecurity
//@ConditionalOnProperty(prefix = "ts", name = "enable_auth", havingValue = "true")
public class TSSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  TSProperties tsProperties;

  @Autowired
  public void initialize(AuthenticationManagerBuilder builder) throws Exception {
    builder.eraseCredentials(false);
    PasswordEncoder passwordEncoder = getPasswordEncoder();
    InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> mem = builder.inMemoryAuthentication();
    mem.withUser(tsProperties.getRead_user())
        .password(passwordEncoder.encode(tsProperties.getRead_password()))
        .roles(tsProperties.getRead_role());
    mem.withUser(tsProperties.getWrite_user())
        .password(passwordEncoder.encode(tsProperties.getWrite_password()))
        .roles(tsProperties.getWrite_role());
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }


  @Configuration
  @Order(1)
  public static class TSFSecurityFhirConfig extends WebSecurityConfigurerAdapter {

    TSProperties tsProperties;

    TSFSecurityFhirConfig(TSProperties tsProperties) {
      this.tsProperties = tsProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      if (tsProperties.isEnable_auth()) {
        http.antMatcher("/fhir/**")
            .csrf().disable()
            .authorizeRequests().anyRequest()
            .access(tsProperties.getEnabled_access()).and().httpBasic();
      } else {
        http.antMatcher("/fhir/**")
            .csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
      }
    }

    private AuthenticationEntryPoint getEntryPoint() {
      BasicAuthenticationEntryPoint ep = new BasicAuthenticationEntryPoint();
      ep.setRealmName("/fhir realm");
      return ep;
    }

  }

  @Configuration
  @Order(2)
  public static class TSFSecurityRootConfig extends WebSecurityConfigurerAdapter {

    TSProperties tsProperties;

    TSFSecurityRootConfig(TSProperties tsProperties) {
      this.tsProperties = tsProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      if (tsProperties.isEnable_auth()) {
        http.antMatcher("/**")
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
            .csrf().disable()
            .authorizeRequests().anyRequest()
            .access(tsProperties.getEnabled_access()).and().formLogin();
      } else {
        http.antMatcher("/**")
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS).and()
            .csrf().disable()
            .authorizeRequests().anyRequest().permitAll();
      }
    }
  }
}
