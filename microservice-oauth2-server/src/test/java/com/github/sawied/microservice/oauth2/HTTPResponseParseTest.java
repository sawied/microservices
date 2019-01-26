package com.github.sawied.microservice.oauth2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class HTTPResponseParseTest {
	
	
	@Test
	//@Ignore
	public void parseHttpClient() {
		Resource resource=new ClassPathResource("sample/response2.txt");
		try {
			InputStream is=resource.getInputStream();
			Scanner scanner = new Scanner(is);
			Pattern p=Pattern.compile("\\[0x|\r[0-9a-f]{0,2}\\]"); 
			
			while(scanner.hasNextLine()) {
				
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				String line =scanner.nextLine();
				Matcher matcher = p.matcher(line);
				
				int beginIndex = 0;
				while(matcher.find()){
					int start = matcher.start();
					int end =matcher.end();
					String subString=line.substring(beginIndex, start);
					for(int i=0;i<subString.length();i++) {
						output.write(subString.charAt(i));
					}
					
					String mk= line.substring(start,end);
					if(mk.contains("0x")) {						
						int i=Integer.parseUnsignedInt(mk.substring(3,mk.length()-1),16);
						output.write(i);
					}
					if("[\r]".equals(mk)) {
						output.write(13);
					}
					beginIndex=end;
				}
							
				System.out.println(new String(output.toByteArray()));
			}
			scanner.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
