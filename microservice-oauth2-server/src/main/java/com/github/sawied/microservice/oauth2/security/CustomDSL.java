package com.github.sawied.microservice.oauth2.security;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomDSL extends AbstractHttpConfigurer<CustomDSL, HttpSecurity> {

	@Override
	public void init(HttpSecurity builder) throws Exception {
		builder.httpBasic().authenticationDetailsSource(new CustomWebAuthenticationDetailsSource());
	}
	
	

}
