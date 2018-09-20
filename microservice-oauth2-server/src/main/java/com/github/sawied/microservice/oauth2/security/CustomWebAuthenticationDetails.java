package com.github.sawied.microservice.oauth2.security;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = -8545091690916978842L;

	public final static String FORWORD_HEADER_NAME = "X_Forward_For";

	private static Logger LOG = LoggerFactory.getLogger(CustomWebAuthenticationDetailsSource.class);

	private String realIp;

	public CustomWebAuthenticationDetails(HttpServletRequest request) {
		super(request);

		String realIp=request.getHeader(FORWORD_HEADER_NAME);
		if (StringUtils.isEmpty(realIp)) {
			this.realIp=realIp;
			LOG.info("extact real ip {} from {}", realIp,FORWORD_HEADER_NAME);
		}else {
			LOG.warn("can not extact real ip from {}", FORWORD_HEADER_NAME);
		}
	}

	public String getRealIp() {
		return realIp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((realIp == null) ? 0 : realIp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomWebAuthenticationDetails other = (CustomWebAuthenticationDetails) obj;
		if (realIp == null) {
			if (other.realIp != null)
				return false;
		} else if (!realIp.equals(other.realIp))
			return false;
		return true;
	}


	
	
	

}
