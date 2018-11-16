package com.github.sawied.microservice.commons;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class SystemInfoContributor implements InfoContributor {


	public void contribute(Builder builder) {
		builder.withDetails(systemInfo());
	}
	
	
	
	private Map<String,Object> systemInfo(){
		 Map<String, Object> info = new HashMap<String,Object>();
		 info.put("processors", Runtime.getRuntime().availableProcessors());
		 info.put("freeMemory", Runtime.getRuntime().freeMemory());
		 info.put("maxMemory", Runtime.getRuntime().maxMemory());
		 info.put("totalMemory", Runtime.getRuntime().totalMemory());
		 info.put("os_name",  System.getProperty("os.name"));
		 info.put("os_arch",  System.getProperty("os.arch"));
		 info.put("os_version",  System.getProperty("os.version"));
		 info.put("user_name",  System.getProperty("user.name"));
		 info.put("java_version",  System.getProperty("java.version"));
		 info.put("java_vendor",  System.getProperty("java.vendor"));
		 return info;
	}
	

}
