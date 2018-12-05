package com.github.sawied.microservice.gateway.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

public class ResponseErrorSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
	
	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		
		HttpServletResponse  response =event.getResponse();
		response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.getWriter().print("{error:\"invalid_session\"}");
		response.flushBuffer();
		
	}

}
