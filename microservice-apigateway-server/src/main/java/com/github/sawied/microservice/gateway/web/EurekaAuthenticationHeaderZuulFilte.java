package com.github.sawied.microservice.gateway.web;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.Base64Utils;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class EurekaAuthenticationHeaderZuulFilte extends ZuulFilter{
	

	private static final Logger log = LoggerFactory.getLogger(EurekaAuthenticationHeaderZuulFilte.class);
	
	
	
	private String username;
	
	private String password;
	

	
	
	public EurekaAuthenticationHeaderZuulFilte(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean shouldFilter() {
		RequestContext context = RequestContext.getCurrentContext();
		String service_id=(String) context.get(FilterConstants.SERVICE_ID_KEY);
		if(service_id.contains("eureka")) {
			log.debug("it seems an eureka service and service id is {},will add authentication header for call", service_id);
			return true;
		}
		return false;
	}

	@Override
	public Object run() throws ZuulException {
		
		RequestContext context = RequestContext.getCurrentContext();
		String token = Base64Utils.encodeToString(
				(this.username + ":" + this.password).getBytes(StandardCharsets.UTF_8));
		context.addZuulRequestHeader("Authorization", "Basic " + token);
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 10000;
	}

}
