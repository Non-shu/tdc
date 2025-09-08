// src/main/java/com/example/config/SecurityCommonConfig.java
package com.example.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityCommonConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .headers(h -> h.frameOptions(f -> f.sameOrigin())) // H2 콘솔 쓸 때 필요
        .authorizeHttpRequests(auth -> auth
          .requestMatchers(
            "/login", "/error",
            "/css/**", "/js/**", "/images/**",
            "/vendors/**", "/assets/**", "/webjars/**",
            "/h2-console/**", "/api/forms/**"
          ).permitAll()
          .anyRequest().authenticated()
        )
        .formLogin(form -> form
          .loginPage("/login")
          .loginProcessingUrl("/login")
          .defaultSuccessUrl("/", true)
          .failureUrl("/login?error")
          .permitAll()
        )
        .httpBasic(b -> b.disable())
        .logout(lo -> lo
          .logoutUrl("/logout")
          .logoutSuccessUrl("/login?logout")
          .permitAll()
        );
    return http.build();
  }
  
  @Bean
  CommandLineRunner printBcrypt(PasswordEncoder enc) {
    return args -> System.out.println(enc.encode("admin123")); // 예: "admin123"
  }
}
