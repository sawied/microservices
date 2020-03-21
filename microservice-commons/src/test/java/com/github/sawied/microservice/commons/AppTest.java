package com.github.sawied.microservice.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

/**
 * Unit test for simple App.
 */
public class AppTest {
	private static final String TEST_STRING = "/zuul/123/90/89";

	@org.junit.Test
	public void regexAppRouterTest() {
		Pattern pattern = Pattern.compile("^/(apis|zuul)/(.*)$");
		Matcher matcher = pattern.matcher(TEST_STRING);
		Assert.assertTrue(matcher.find());
		Assert.assertEquals(TEST_STRING, matcher.group(0));
		Assert.assertEquals("zuul", matcher.group(1));
		Assert.assertEquals("123/90/89", matcher.group(2));

	}
}
