package com.github.sawied.microservice.application.document;


import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.sawied.microservice.trade.config.SignDocConfig;
import com.github.sawied.microservice.trade.document.DocumentOperator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {SignDocConfig.class})
public class DocumentOperatorTest {

	@Autowired
	DocumentOperator docOperator;
	
	
	private static final String workpath=System.getProperty("user.home")+"/mvtm/";
	private static final String outputPath=workpath+"target.pdf";
	private static final String imagePath="signDoc/sign.PNG";
	
	
	@Test
	public void signDocTest() throws Exception {
		Map<String,String> data=new HashMap<String,String>();
		FileOutputStream output = new FileOutputStream(new File(outputPath));
		docOperator.fillInForm(data,output);
		//then sign the document
		FileOutputStream os = new FileOutputStream(new File(workpath+UUID.randomUUID().toString()+".pdf"));
		docOperator.esignature(new FileSystemResource(outputPath), new ClassPathResource(imagePath), os);
		//docOperator.esignature(new ClassPathResource("template/form2.pdf"), new ClassPathResource(imagePath), os);
	}
	
	
}
