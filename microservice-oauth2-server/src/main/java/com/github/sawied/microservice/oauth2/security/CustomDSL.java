package com.github.sawied.microservice.oauth2.security;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomDSL extends AbstractHttpConfigurer<CustomDSL, HttpSecurity> {

	@Override
	public void configure(HttpSecurity builder) throws Exception {
		
		//builder.formLogin().authenticationDetailsSource(new CustomWebAuthenticationDetailsSource());
		
	}

}
