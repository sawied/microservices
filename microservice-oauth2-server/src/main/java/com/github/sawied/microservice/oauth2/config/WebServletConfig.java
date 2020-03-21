package com.github.sawied.microservice.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@ComponentScan(basePackageClasses= {com.github.sawied.microservice.commons.SystemInfoContributor.class,com.github.sawied.microservice.commons.security.SystemAPISecurity.class})
public class WebServletConfig {
	
	
	@Bean
	public ForwardedHeaderFilter forwardHeaderFilter() {
		return new ForwardedHeaderFilter();
	}

}
