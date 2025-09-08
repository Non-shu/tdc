// src/main/java/com/example/config/SecurityJdbcConfig.java
package com.example.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Profile("mariadb")
@Configuration
public class SecurityJdbcConfig {

  @Bean
  public UserDetailsService userDetailsService(DataSource ds) {
	  JdbcUserDetailsManager mgr = new JdbcUserDetailsManager(ds);
	  mgr.setUsersByUsernameQuery("""
		      SELECT e.login_id AS username,
		             e.emp_pw   AS password,
		             CASE WHEN e.enabled=1 AND (e.use_yn IS NULL OR e.use_yn='Y') THEN 1 ELSE 0 END AS enabled
		      FROM emp e WHERE e.login_id = ?
		    """);
		    mgr.setAuthoritiesByUsernameQuery("""
		      SELECT username, authority
		      FROM v_emp_authorities
		      WHERE username = ?
		    """);
		    return mgr;
		  }
		}