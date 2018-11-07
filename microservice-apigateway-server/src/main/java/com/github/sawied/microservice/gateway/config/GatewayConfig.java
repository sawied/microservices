package com.github.sawied.microservice.gateway.config;

import java.time.Duration;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.github.sawied.microservice.gateway.security.Account;
import com.github.sawied.microservice.gateway.web.AuthenticationHeaderZuulFilter;
import com.github.sawied.microservice.gateway.web.EurekaAuthenticationHeaderZuulFilte;
import com.github.sawied.microservice.gateway.web.ForwardHeaderHttpClientInterceptor;

@Configuration
@EnableZuulProxy
@EnableCaching
@PropertySource("classpath:config/api-gateway-config.properties")
public class GatewayConfig {
	
	public static final String OAUTH2_SERVICE_NAME="MICROSERVICE-OAUTH2-SERVER";
	
	@Bean
	public WebMvcConfigurer corsConfig() {
		return new WebMvcConfigurer(){

			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowedHeaders("*");
			}
			
		};
	}
	

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
	
	
	@Bean 
	public EurekaAuthenticationHeaderZuulFilte eurekaAuthenticationZuulFilter(@Value(value="${eureka.service.username:admin}") String username,
			@Value(value="${eureka.service.password:secret}") String password) {
		return new EurekaAuthenticationHeaderZuulFilte(username,password);
	}
	
	@Bean
	public CacheManager  cacheManager(){
		
		CachingProvider provider = Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
		CacheManager cacheManager  = provider.getCacheManager();
		
		return cacheManager;
		
	}
	
	@Bean
	public Cache<String, Account> customerInfo(CacheManager cacheManager) {
		
		CacheConfigurationBuilder<String, Account> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Account.class, ResourcePoolsBuilder.heap(100))
				.withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10)));
		
		return cacheManager.createCache("customerInfo", Eh107Configuration.fromEhcacheCacheConfiguration(cacheConfiguration));
		
	}
}
