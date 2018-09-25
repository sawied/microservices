package com.github.sawied.microservice.application.doc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.github.sawied.microservice.trade.Application;

import io.github.swagger2markup.Swagger2MarkupConverter;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { Application.class, SwaggerConfig.class }, webEnvironment = WebEnvironment.MOCK)
@AutoConfigureRestDocs(outputDir = "build/asciidoc/snippets")
@AutoConfigureMockMvc
public class Swagger2MarkupTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void apiSwaggerJson() throws Exception {
		MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
				.header(HttpHeaders.AUTHORIZATION, "Basic ZGFuYW46cGFzc3dvcmQ=").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		
		String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
		MockHttpServletResponse response = mvcResult.getResponse();
		String swaggerJson = response.getContentAsString();
		Files.createDirectories(Paths.get(outputDir));
		try (BufferedWriter writer = Files.newBufferedWriter(
				Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)) {
			writer.write(swaggerJson);
		}
		
		
		Swagger2MarkupConverter.from(Paths.get(outputDir, "swagger.json")).build().toPath(Paths.get("target/asciidoc/generated"));
	}
	

}
