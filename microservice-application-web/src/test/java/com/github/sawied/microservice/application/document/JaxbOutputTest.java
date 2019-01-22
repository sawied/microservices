package com.github.sawied.microservice.application.document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Ignore;
import org.junit.Test;

public class JaxbOutputTest {

	
	@Test
	@Ignore
	public void jaxbMashller() {
		JAXBContext jaxbContent;
		try {
			jaxbContent = JAXBContext.newInstance("com.sc.edmi");
			Marshaller marshaller = jaxbContent.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			//marshaller.marshal(new ObjectFactory().createInquireCitizenship(ics), System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
