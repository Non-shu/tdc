package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    http
	      .authorizeHttpRequests(auth -> auth
	          .requestMatchers("/login",
	                           "/css/**","/js/**","/assets/**","/vendors/**").permitAll()
	          .requestMatchers("/h2","/h2/**").permitAll()              // H2 콘솔 허용
	          .anyRequest().authenticated()
	      )
	      .formLogin(login -> login
	          .loginPage("/login")
	          .loginProcessingUrl("/login")
	          .defaultSuccessUrl("/admin", true)
	          .permitAll()
	      )
	      .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll())

	      // ★ H2 콘솔은 프레임 필요 → sameOrigin 또는 disable
	      .headers(h -> h.frameOptions(f -> f.sameOrigin()))

	      // ★ CSRF는 H2 콘솔 경로만 예외
	      .csrf(csrf -> csrf
	          .ignoringRequestMatchers(new AntPathRequestMatcher("/h2/**"))
	          .ignoringRequestMatchers(new AntPathRequestMatcher("/h2"))   // path=/h2일 때도 대비
	      );

	    return http.build();
	  }
	}