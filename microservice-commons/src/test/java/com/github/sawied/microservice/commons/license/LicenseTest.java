package com.github.sawied.microservice.commons.license;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.HexDump;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Assert;
import org.junit.Test;

public class LicenseTest {
	
	
	@Test
	public void licenseTest() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] byteArray = loadKeyfromPEM("keys/pkcs8-private.key");
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(byteArray);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		Assert.assertNotNull(privateKey);
		
		License license = new License();
		license.setLicenseId();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 3);
		license.setExpiry(calendar.getTime());
		
		license.add(Feature.Create.stringFeature("bill", "abcdef"));
		license.sign(privateKey, "SHA-256");
		
		byte[] result=license.serialized();
		
		byte[] encode = Base64.getEncoder().encode(result);
		IOUtils.write(encode, System.out);
		
		
		
		
	}
	
	
	@Test
	public void licenseVerifySuccess() throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String base64License="Tl7OIQAAABYAAAACAAAABAAAAAZiaWxsYWJjZGVmAAAAHgAAAAsAAAAKZXhwaXJ5RGF0ZQAAAWwDXgbkAAAAAAAAARkAAAABAAAAEAAAAP1saWNlbnNlU2lnbmF0dXJlAzU2dpNywdMBBCU2PAL5ok0xhRiC1rc5Lou6jk4chO1e02QAEkjcZ+wkC/yqFMoa1NgTQ4y/xZL3BqtXzo+VuMvQhBwazqj0M5v/4/60R//jqXw6LLVRrOsoxBGjleMrJuGDUV8ZR6drm2wOGf+25WkToi20cfJ57h3ts5OWdxyv+jitGYf+pynC7cix/omBfauqOXTzmg+PhL8ng+QJEfqdrAosxaN/YKUJ9ahvnVzlabrGe1oVxP0nKyn0QSU+8ZwwRRNfwGs9en0Q+R2T+1Bh7y5CxedRRYrrQ/7uW4ms8ZjS1dgi3suZL3zy2u0hl0ysVzFcldDvh7nDCgAAACUAAAAMAAAACWxpY2Vuc2VpZJvuNifTntYYvV5cH9x3QgMAAAAAAAAAIgAAAAIAAAAPAAAAB3NpZ25hdHVyZURpZ2VzdFNIQS0yNTY=";
		byte[] decode = Base64.getDecoder().decode(base64License);
		License from = License.Create.from(decode);
		byte[] key = loadKeyfromPEM("keys/rsa_pub.key");
		
		KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        PublicKey pubKey = kf.generatePublic(keySpec);
		boolean validOrNot=from.verify(pubKey);
		Assert.assertTrue(validOrNot);
		
	}
	
	
	@Test
	public void licenseIsOK() {
		String base64License="Tl7OIQAAABYAAAACAAAABAAAAAZiaWxsYWJjZGVmAAAAHgAAAAsAAAAKZXhwaXJ5RGF0ZQAAAWwDXgbkAAAAAAAAARkAAAABAAAAEAAAAP1saWNlbnNlU2lnbmF0dXJlAzU2dpNywdMBBCU2PAL5ok0xhRiC1rc5Lou6jk4chO1e02QAEkjcZ+wkC/yqFMoa1NgTQ4y/xZL3BqtXzo+VuMvQhBwazqj0M5v/4/60R//jqXw6LLVRrOsoxBGjleMrJuGDUV8ZR6drm2wOGf+25WkToi20cfJ57h3ts5OWdxyv+jitGYf+pynC7cix/omBfauqOXTzmg+PhL8ng+QJEfqdrAosxaN/YKUJ9ahvnVzlabrGe1oVxP0nKyn0QSU+8ZwwRRNfwGs9en0Q+R2T+1Bh7y5CxedRRYrrQ/7uW4ms8ZjS1dgi3suZL3zy2u0hl0ysVzFcldDvh7nDCgAAACUAAAAMAAAACWxpY2Vuc2VpZJvuNifTntYYvV5cH9x3QgMAAAAAAAAAIgAAAAIAAAAPAAAAB3NpZ25hdHVyZURpZ2VzdFNIQS0yNTY=";
		byte[] decode = Base64.getDecoder().decode(base64License);
		License from = License.Create.from(decode);
		Assert.assertTrue(from.isOK());
	}
	
	
	
	public byte[] loadKeyfromPEM(String keyPath) throws FileNotFoundException, IOException {
		URL systemResource = ClassLoader.getSystemResource(keyPath);
		String filePath = systemResource.getFile();
		File pem = new File(filePath);
		
		LineIterator lineIterator = IOUtils.lineIterator(new FileInputStream(pem), "UTF-8");
		StringBuilder sb =new StringBuilder();
		while(lineIterator.hasNext()) {
			String line = lineIterator.next();
			if(!line.startsWith("--")) {
				sb.append(line);
			}
		}
		
		byte[] privatekey = Base64.getDecoder().decode(sb.toString());
		
		return privatekey;
		
	}

}
