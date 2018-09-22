package com.github.sawied.microservice.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class WebServletConfig {
	
	
	@Bean
	public ForwardedHeaderFilter forwardHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

}
