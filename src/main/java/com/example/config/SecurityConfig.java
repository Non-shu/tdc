package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		 http
		    .authorizeHttpRequests(auth -> auth
		      .requestMatchers(
		        "/assets/**",      // ✅ dist에서 옮긴 정적파일
		        "/webjars/**",
		        "/", "/home", "/login"
		      ).permitAll()
		      .anyRequest().authenticated()
		    )
		    .formLogin(form -> form               // 원하면 커스텀 로그인 페이지로 교체 가능
		      .loginPage("/login").permitAll()
		      .defaultSuccessUrl("/", true)
		    )
		    .logout(Customizer.withDefaults());
		  return http.build();
		}

	  // 임시 인메모리 유저
	  @Bean
	  UserDetailsService userDetailsService() {
	    return new InMemoryUserDetailsManager(
	      User.withUsername("test").password("{noop}1234").roles("USER").build()
	    );
	  }
}
