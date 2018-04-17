package com.kardach.kweb.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class HttpRequestParserTest {

	private final static String GET_REQUEST_COMPLETE = "GET http://localhost:8080/path/script.cgi?field1=value1&field2=value2 HTTP/1.1\r\n";
	private final static String GET_REQUEST_INCOMPLETE = "GET http://localhost:8080/pat";
	
	@Test
	public void testParse_CompleteGetRequest() {
		Request request = generate(GET_REQUEST_COMPLETE);
		boolean result = HttpRequestParser.parse(request);
		
		assertTrue(result);
		assertTrue(request.isCompleted());
		assertEquals(HttpRequestMethod.GET, request.getMethod());
	}
	
	@Test
	public void testParse_IncompleteGetRequest() {
		Request request = generate(GET_REQUEST_INCOMPLETE);
		boolean result = HttpRequestParser.parse(request);
		
		assertFalse(result);
		assertFalse(request.isCompleted());
		assertEquals(HttpRequestMethod.GET, request.getMethod());
	}
	
	private Request generate(String req) {
		Request result = new Request();
		result.getBuffer().put(req.getBytes());
		return result;
	}
}
