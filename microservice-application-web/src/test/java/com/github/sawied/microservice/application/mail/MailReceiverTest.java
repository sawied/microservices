package com.github.sawied.microservice.application.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.trade.config.MailConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {MailConfig.class})
public class MailReceiverTest {
	
	@Test
	public void receiveMailSuccess() throws InterruptedException {
		while(true) {
			Thread.sleep(5000);	
		}
		
	}

}
