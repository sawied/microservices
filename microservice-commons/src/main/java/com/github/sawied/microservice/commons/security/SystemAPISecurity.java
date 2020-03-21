package com.github.sawied.microservice.commons.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@Order(-10)
public class SystemAPISecurity extends WebSecurityConfigurerAdapter {

	@Value("${system.security.user.name:system}")
	private String system_user;

	@Value("${system.security.user.password:{noop}password}")
	private String system_password;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.debug(true);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder()).withUser(system_user).password(system_password).roles("system");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().antMatchers("/actuator/**").and().authorizeRequests().anyRequest().authenticated().and()
				.httpBasic().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
	}

}
