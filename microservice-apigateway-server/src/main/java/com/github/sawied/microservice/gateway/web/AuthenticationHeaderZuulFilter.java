package com.github.sawied.microservice.gateway.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class AuthenticationHeaderZuulFilter extends ZuulFilter{

	@Override
	public boolean shouldFilter() {
		Authentication  authentication=SecurityContextHolder.getContext().getAuthentication();
		return authentication!=null;
	}

	@Override
	public Object run() throws ZuulException {
		Authentication  authentication=SecurityContextHolder.getContext().getAuthentication();
		RequestContext context = RequestContext.getCurrentContext();
		context.addZuulRequestHeader("X-Authentication-Name", authentication.getName());
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 100;
	}

}
