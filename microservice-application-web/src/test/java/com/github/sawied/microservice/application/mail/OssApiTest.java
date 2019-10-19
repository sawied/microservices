package com.github.sawied.microservice.application.mail;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.trade.config.OSSConfig;
import com.github.sawied.microservice.trade.service.OSSApi;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {OSSConfig.class})
public class OssApiTest {
	
	@Autowired
	private OSSApi ossAPi;
	
	@Test
	public void ossAPIUploadTest() throws IOException {
		assertNotNull(ossAPi);
		Resource  resource = new ByteArrayResource("Hello".getBytes());
		ossAPi.putObject("case/11.txt",resource);
	}
	
	

}
