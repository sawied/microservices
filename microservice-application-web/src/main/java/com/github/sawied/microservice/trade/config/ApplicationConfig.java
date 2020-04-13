package com.github.sawied.microservice.trade.config;


import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.github.sawied.microservice.trade.component.HttpProxyClientfactory;

@Configuration
public class ApplicationConfig {

	@Bean
	public HttpClient httpclient() throws Exception {
		return new HttpProxyClientfactory().getObject();
	}
	
	@Bean
	public RestTemplate restTemplate(HttpClient httpclient) {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpclient));
		
	}
	
	
}
