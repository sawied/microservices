package com.github.sawied.microservice.gateway.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class CompoundSecurityContextRepository implements SecurityContextRepository {

	
	private RequestMatcher secureMather = new AntPathRequestMatcher("/oauth2/secure");

	private NullSecurityContextRepository nullSecurityRepository;

	private HttpSessionSecurityContextRepository httpSessionSecurityContextRepository;
	
	private Boolean isAssociate =false;

	public CompoundSecurityContextRepository() {
		this.nullSecurityRepository = new NullSecurityContextRepository();
		this.httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
	}

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		return getRepositoryByAttribute(requestResponseHolder.getRequest()).loadContext(requestResponseHolder);
	}

	@Override
	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		getRepositoryByAttribute(request).saveContext(context, request, response);
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		return getRepositoryByAttribute(request).containsContext(request);
	}

	private SecurityContextRepository getRepositoryByAttribute(HttpServletRequest request) {
		SecurityContextRepository securityRepository = nullSecurityRepository;
		if (isSessionAssociation(request)) {
			securityRepository = httpSessionSecurityContextRepository;
		}
		return securityRepository;
	}

	private Boolean isSessionAssociation(HttpServletRequest request) {
		
		return !secureMather.matches(request)&&isAssociate;
	}

	public void setIsAssociate(Boolean isAssociate) {
		this.isAssociate = isAssociate;
	}
	
	

}
