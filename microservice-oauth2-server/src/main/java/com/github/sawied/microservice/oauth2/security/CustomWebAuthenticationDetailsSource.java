package com.github.sawied.microservice.oauth2.security;

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;



public class CustomWebAuthenticationDetailsSource implements
AuthenticationDetailsSource<HttpServletRequest, CustomWebAuthenticationDetails> {
	
	
	public final static String FORWORD_HEADER_NAME="X_Forward_For";
	
	

	@Override
	public CustomWebAuthenticationDetails buildDetails(HttpServletRequest context) {
		CustomWebAuthenticationDetails webDetails = new CustomWebAuthenticationDetails(context);
		return webDetails;
	}

}
