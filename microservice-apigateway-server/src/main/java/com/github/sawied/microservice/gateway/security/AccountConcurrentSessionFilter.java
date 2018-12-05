package com.github.sawied.microservice.gateway.security;

import java.io.IOException;
import java.util.Date;

import javax.cache.Cache;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountConcurrentSessionFilter extends ConcurrentSessionFilter implements InitializingBean{
	
	private final static Logger LOG = LoggerFactory.getLogger(AccountConcurrentSessionFilter.class);
	
	private ObjectMapper om = new ObjectMapper();
	
	private Cache<String,String> tokenCache;

	private SessionInformationExpiredStrategy sessionInformationExpiredStrategy;

	public AccountConcurrentSessionFilter(SessionRegistry sessionRegistry,
			SessionInformationExpiredStrategy sessionInformationExpiredStrategy) {
		super(sessionRegistry, sessionInformationExpiredStrategy);
		this.sessionInformationExpiredStrategy = sessionInformationExpiredStrategy;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		String token=(String) request.getAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE);
		LOG.debug("concurrent session filter try yo detect token in this request ,the result is {}", StringUtils.isEmpty(token));
		if(!StringUtils.isEmpty(token)) {
			try {
				String body=JwtHelper.decode(token).getClaims();
				String name = parseTokenUsername(body);
				if(tokenCache.containsKey(name)) {
					String cacheToken = tokenCache.get(name);
					if(!token.equals(cacheToken)) {
						LOG.error("it seems have a invaild token passed in when {} do service call.",name);
						SessionInformationExpiredEvent sessionExpiredEvent = new SessionInformationExpiredEvent(new SessionInformation(name,token,new Date()), (HttpServletRequest)request, (HttpServletResponse)res);
						sessionInformationExpiredStrategy.onExpiredSessionDetected(sessionExpiredEvent);
						return;
						//throw new InvaildTokenException("the token used to do service call has been expired.");
					}
				}
			} catch (IOException e) {
				LOG.debug("token parse failed, due to exception", e);
				throw new InvaildTokenException("the token be used to do authentication ,but parse unsuccessfully.",e);
			}
		}
		
		super.doFilter(request, res, chain);
	}
	
	private String parseTokenUsername(String body) throws IOException {
		JsonNode jsonNode = om.readTree(body);
		return jsonNode.path("user_name").asText();
	}

	public void setTokenCache(Cache<String, String> tokenCache) {
		this.tokenCache = tokenCache;
	}
	
	@Override
	public void afterPropertiesSet() {
		Assert.notNull(tokenCache, "tokenCache required");
	}
	

}
