package com.github.sawied.microservice.jpa.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;


public class SecureLinkTest {

	
	@Test
	public void secureLinkTest() {
		String str = "srcret20190131235959/jweb/package.json";
		System.out.println(DigestUtils.md5Hex(str.getBytes()));
		Assert.assertEquals("73c3a37ef62f4849b2b796566f990c6a", str);
	}
	
	
}
