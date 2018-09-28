package com.github.sawied.microservice.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import com.github.sawied.microservice.gateway.web.AuthenticationHeaderZuulFilter;
import com.github.sawied.microservice.gateway.web.ForwardHeaderHttpClientInterceptor;

@Configuration
@EnableZuulProxy
public class GatewayConfig {
	
	public static final String OAUTH2_SERVICE_NAME="OAUTH2";
	
	

	@Bean
	@LoadBalanced
	public RestTemplate oauth2RestTemplate(@Value(value="${oauth2.service.username:api-gateway}") String oauth2_username,
			@Value(value="${oauth2.service.password:secret}") String oauth2_password) {
		 RestTemplate restTemplate = new RestTemplate();
		 restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(oauth2_username,oauth2_password));
		 restTemplate.getInterceptors().add(new ForwardHeaderHttpClientInterceptor());
		return restTemplate;
	}
	
	
	@Bean
	public AuthenticationHeaderZuulFilter authenticationHeaderZuulFilter() {
		return new AuthenticationHeaderZuulFilter();
	}
}
