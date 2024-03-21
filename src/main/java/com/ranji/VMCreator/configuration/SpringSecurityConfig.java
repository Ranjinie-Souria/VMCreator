package com.ranji.VMCreator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.
				authorizeHttpRequests(auth -> {
					auth.requestMatchers("/").authenticated();
					auth.anyRequest().authenticated();
			}).formLogin(Customizer.withDefaults()).build();
	}

	@Bean
	public UserDetailsService users() {
		UserDetails saduser = User.builder()
				.username("saduser")
				.password(passwordEncoder().encode("123"))
				.roles("SAD_USER").build();
		UserDetails user = User.builder()
				.username("user")
				.password(passwordEncoder().encode("123"))
				.roles("USER").build();
		UserDetails superuser = User.builder()
				.username("superuser")
				.password(passwordEncoder().encode("123"))
				.roles("SUPER_USER").build();
		return new InMemoryUserDetailsManager(saduser, user, superuser);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}


