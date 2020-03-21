package com.github.sawied.microservice.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.github.sawied.microservice.trade.service.OSSApi;

@Configuration
@PropertySource("classpath:config/oss.property")
public class OSSConfig {

	
	@Bean
	public OSSApi ossApi() {
		return new OSSApi();
	}
	
}
