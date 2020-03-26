package com.github.sawied.microservice.trade.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.autodiscover.exception.AutodiscoverLocalException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ITraceListener;

@Configuration
public class EWSConfig {

	
	@Bean(destroyMethod = "close")
	public ExchangeService exchangeService() throws Exception {
		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials("danan.2009@hotmail.com", "0311711w");
		service.setCredentials(credentials);
		service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
		/**
		service.autodiscoverUrl("danan.2009@hotmail.com", new IAutodiscoverRedirectionUrl() {
			
			@Override
			public boolean autodiscoverRedirectionUrlValidationCallback(String redirectionUrl)
					throws AutodiscoverLocalException {
				return redirectionUrl.toLowerCase().startsWith("https://");
			}
		});
		**/
		service.setTraceListener(new ITraceListener() {
			
			@Override
			public void trace(String traceType, String traceMessage) {
				System.out.println("traceType:"+traceType);
				System.out.println("traceMessage:"+traceMessage);
				
			}
		});
		service.setTraceEnabled(true);
	
		return service;
	}
	
	@Bean
	public ExchangeAPI exchengAPI() {
		return new ExchangeAPI();
	}
	
	
}
