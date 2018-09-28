package com.github.sawied.microservice.gateway.web;


import java.io.IOException;
import java.net.InetSocketAddress;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * add forword Header for Httpclient do HTTP Request call
 * @author sawied
 *
 */
public class ForwardHeaderHttpClientInterceptor implements ClientHttpRequestInterceptor{
	
	public final static String X_FORWARDED_FOR_HEADER="x-forword-for";

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		
		String xforwardedfor = request.getHeaders().getFirst(X_FORWARDED_FOR_HEADER);
		InetSocketAddress host = request.getHeaders().getHost();
		String remoteAddr= host==null?"":host.getHostName();
		if (xforwardedfor == null) {
			xforwardedfor = remoteAddr;
		}
		else if (!xforwardedfor.contains(remoteAddr)) { // Prevent duplicates
			xforwardedfor += ", " + remoteAddr;
		}
		request.getHeaders().set(X_FORWARDED_FOR_HEADER, xforwardedfor);
		return execution.execute(request, body);
	}

}
