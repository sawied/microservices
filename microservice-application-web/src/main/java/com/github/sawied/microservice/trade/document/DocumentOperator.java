package com.github.sawied.microservice.trade.document;

import java.io.OutputStream;
import java.util.Map;

import org.springframework.core.io.Resource;

public interface DocumentOperator {

	/**
	 * <p>fill in form data and then write to output stream</p>
	 * @author sawied
	 * @param data data for fill in
	 * @param os where fill in form document write to
	 *  
	 */
	void fillInForm(Map<String, String> data, OutputStream os) throws Exception;

	/**
	 * do e-signature with propose image
	 * @param resource document for e-signature
	 * @param imageResource image for appearance image ,normally it's personal hand writing.
	 * @param os
	 */
	void esignature(Resource resource, Resource imageResource, OutputStream os);

}