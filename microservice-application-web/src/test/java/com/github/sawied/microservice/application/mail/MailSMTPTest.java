package com.github.sawied.microservice.application.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.trade.config.SMTPConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {SMTPConfig.class})
public class MailSMTPTest {
	
	@Autowired
	private MailSender mailSender;
	
	@Test
	public void sendMailwithWrongFrom() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("noreplay@hotmail.com");
		message.setTo("zhangxiaowei@chinasofti.com");
		message.setText("noreply mailbox test.");
		mailSender.send(message);
	}

}
