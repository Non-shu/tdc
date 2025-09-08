// src/main/java/com/example/config/SecurityH2Config.java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Profile("h2")
@Configuration
public class SecurityH2Config {

  @Bean
  public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails admin = User.withUsername("admin")
        .password(encoder.encode("admin123"))
        .roles("ADMIN","USER")
        .build();
    UserDetails user = User.withUsername("user")
        .password(encoder.encode("user123"))
        .roles("USER")
        .build();
    return new InMemoryUserDetailsManager(admin, user);
  }
}
