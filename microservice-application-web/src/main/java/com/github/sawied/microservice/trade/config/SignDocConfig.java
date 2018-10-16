package com.github.sawied.microservice.trade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import com.github.sawied.microservice.trade.document.DocumentOperator;
import com.github.sawied.microservice.trade.document.PdfboxDocumentOperator;

@Configuration
@PropertySource("classpath:config/app.config.properties")
public class SignDocConfig {

	@Bean
	public DocumentOperator documentOperator(
			@Value("${sign.key.name}") String alias,
			@Value("${sign.keystore.file}") String path,
			@Value("${sign.keystore.passwd}") String passwd,
			@Value("${template.path}") String templatePath,
			@Value("${template.sign.page:0}") int signPage,
			@Value("${template.sign.rectangle}") int [] position
			)
			{
		
		ResourcePatternResolver resourcePatternResolver =new PathMatchingResourcePatternResolver();
		
		PdfboxDocumentOperator docOperator=new PdfboxDocumentOperator();
		docOperator.setAlias(alias);
		docOperator.setKeyStoreResource(resourcePatternResolver.getResource(path));
		docOperator.setStorepassword(passwd);
		docOperator.setTemplate(resourcePatternResolver.getResource(templatePath));
		docOperator.setPositions(position);
		docOperator.setSignPage(signPage);
		
		return docOperator;
	}
	
}
