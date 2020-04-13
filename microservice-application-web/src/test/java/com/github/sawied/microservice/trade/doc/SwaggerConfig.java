package com.github.sawied.microservice.trade.doc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

	@Bean
	public Docket tradeApi(ApiInfo info) {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.github.sawied.microservice.trade.api")).build()
				.pathMapping("/apis").apiInfo(info);
	}

	@Bean
	public ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("trade API document").version("v 1.0")
				.description("trade api document,for production environemnt, this document will be removed automaticly")
				.build();
	}
}