package com.github.sawied.microservice.trade.config;

import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.integration.mail.support.MailUtils;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageHeaders;

public class MailContentHeaderMapper  implements HeaderMapper<MimeMessage>{
	
	@Override
	public Map<String, Object> toHeaders(MimeMessage source) {
		Map<String, Object> headers = MailUtils.extractStandardHeaders(source);
		MimeMessageParser parser = new MimeMessageParser(source);
		try {
			parser.parse();
			String subject=parser.getSubject();
			System.out.println("SUBJECT:" + subject);
			if(parser.hasPlainContent()) {
				System.out.println("TEXT:" + parser.getPlainContent());
			}
			if(parser.hasHtmlContent()) {
				System.out.println("HTML:" + parser.getHtmlContent());
			}
			if(parser.hasAttachments()) {
				System.out.println("Attachement:" + parser.getAttachmentList().size());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headers;
	}

	@Override
	public void fromHeaders(MessageHeaders headers, MimeMessage target) {
		throw new UnsupportedOperationException("Mapping to a mail message is not currently supported");
	}
}
