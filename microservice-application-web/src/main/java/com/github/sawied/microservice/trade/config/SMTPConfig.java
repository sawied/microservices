package com.github.sawied.microservice.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class SMTPConfig {
	
	
	@Bean
	public MailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("smtp.office365.com");
		mailSender.setPort(587);
		mailSender.setUsername("danan.2009@hotmail.com");
		mailSender.setPassword("0311711w");
		return mailSender;
	}

}
