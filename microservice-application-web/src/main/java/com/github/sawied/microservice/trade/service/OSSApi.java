package com.github.sawied.microservice.trade.service;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;


public class OSSApi {
	
	private static final Logger LOG = LoggerFactory.getLogger(OSSApi.class);
	
	@Value("${oss.endpoint}")
	private String endpoint;
	
	@Value("${oss.accessKeyId}")
	private String accessKeyId;
	
	@Value("${oss.secretAccessKey}")
	private String secretAccessKey;
	
	@Value("${oss.bucketName}")
	private String bucketName;
	
	public void putObject(String key,Resource resource) throws IOException {
		OSS oss = this.buildOss();
		ObjectMetadata metadata = new ObjectMetadata();
		InputStream inputStream = null;
		try {
			inputStream = resource.getInputStream();
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, inputStream, metadata);
			PutObjectResult result = oss.putObject(putObjectRequest);
			LOG.debug("uploaded resource to oss server {}",result.getRequestId());
		} catch (IOException e) {
			LOG.error("occur IO exception when saving resource to OSS");
			throw e;
		}finally {
			if(inputStream!=null) {
				inputStream.close();
			}
		}
		
	}
	
	public void load(String key,OutputStream outputStram) throws IOException {
		OSS oss = this.buildOss();
		OSSObject ossObject = oss.getObject(bucketName, key);
		InputStream content = ossObject.getObjectContent();
		try {
			IOUtils.copy(content, outputStram);
			IOUtils.closeQuietly(content);
			IOUtils.closeQuietly(outputStram);
		} catch (IOException e) {
			LOG.error("occur IO exception when downloading resource to OSS");
			throw e;
		}
	}
	
	public OSS buildOss() {
		OSS ossclient=new OSSClientBuilder().build(endpoint, accessKeyId, secretAccessKey);
	 return ossclient;
	}

}
