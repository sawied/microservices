package com.github.sawied.microservice.application.mail;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.trade.config.EWSConfig;
import com.github.sawied.microservice.trade.config.ExchangeAPI;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {EWSConfig.class})
public class ExchangeTest {

	@Autowired
	private ExchangeAPI exchangeApi;
	
	@Test
	@Ignore
	public void receiveMailSuccess() throws InterruptedException {
		exchangeApi.sendMessage();
		exchangeApi.startup();
	}
	
	@Test
	public void pullMailSuccess() throws InterruptedException {
		//exchangeApi.sendMessage();
		//exchangeApi.markMailList();
		exchangeApi.retrieveMarkedMailList();
	}
	
}
