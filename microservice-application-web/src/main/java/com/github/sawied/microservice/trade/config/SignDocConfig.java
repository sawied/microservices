package com.github.sawied.microservice.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import com.github.sawied.microservice.trade.document.DocumentOperator;
import com.github.sawied.microservice.trade.document.PdfboxDocumentOperator;

@Configuration

public class SignDocConfig {
	
	@Configuration
	@Profile("default")
	@PropertySource("classpath:config/app.config.properties")
	static class Dev{}

	@Bean
	public DocumentOperator documentOperator(
			@Value("${sign.key.name}") String alias,
			@Value("${sign.keystore.file}") String path,
			@Value("${sign.keystore.passwd}") String passwd,
			@Value("${template.path}") String templatePath,
			@Value("${template.sign.page:0}") int signPage,
			@Value("${template.sign.rectangle}") int [] position,
			@Value("${font.resource}") String font)
			{
		
		ResourcePatternResolver resourcePatternResolver =new PathMatchingResourcePatternResolver();
		
		PdfboxDocumentOperator docOperator=new PdfboxDocumentOperator();
		docOperator.setAlias(alias);
		docOperator.setKeyStoreResource(resourcePatternResolver.getResource(path));
		docOperator.setStorepassword(passwd);
		docOperator.setTemplate(resourcePatternResolver.getResource(templatePath));
		docOperator.setPositions(position);
		docOperator.setSignPage(signPage);
		docOperator.setFontResource(resourcePatternResolver.getResource(font));
		return docOperator;
	}
	
}
