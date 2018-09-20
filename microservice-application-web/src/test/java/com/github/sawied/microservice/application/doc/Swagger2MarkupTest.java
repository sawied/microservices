package com.github.sawied.microservice.application.doc;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.github.sawied.microservice.trade.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class, SwaggerConfig.class},webEnvironment=WebEnvironment.MOCK)
public class Swagger2MarkupTest {
	
	@Autowired
	private MockMvc mvc;

}
