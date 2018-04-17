package com.kardach.kweb.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

public class HttpRequestParserTest {

	private final static String GET_REQUEST_COMPLETE = "GET http://localhost:8080/path/script.cgi?field1=value1&field2=value2 HTTP/1.1\r\n";
	private final static String GET_REQUEST_INCOMPLETE = "GET http://localhost:8080/pat";
	
	@Test
	public void testParse_CompleteGetRequest() {
		HttpRequest request = generate(GET_REQUEST_COMPLETE);
		boolean result = HttpRequestParser.parse(request);
		
		assertTrue(result);
		assertTrue(request.isCompleted());
		assertEquals(HttpRequestMethod.GET, request.getMethod());
	}
	
	@Test
	public void testParse_IncompleteGetRequest() {
		HttpRequest request = generate(GET_REQUEST_INCOMPLETE);
		boolean result = HttpRequestParser.parse(request);
		
		assertFalse(result);
		assertFalse(request.isCompleted());
		assertEquals(HttpRequestMethod.GET, request.getMethod());
	}
	
	@Test
	public void testFind_InStart() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		byte[] search = {2, 1};
		byte[] data = {2, 1, 3, 5};
		Method method = HttpRequestParser.class.getDeclaredMethod("find", byte[].class, int.class, byte[].class);
		method.setAccessible(true);
		int result = (int) method.invoke(HttpRequestParser.class, data, 0,  search);
		assertEquals(0, result);
	}
	
	@Test
	public void testFind_InMiddle() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		byte[] search = {1, 3};
		byte[] data = {2, 1, 3, 5};
		Method method = HttpRequestParser.class.getDeclaredMethod("find", byte[].class, int.class, byte[].class);
		method.setAccessible(true);
		int result = (int) method.invoke(HttpRequestParser.class, data, 0, search);
		assertEquals(1, result);
	}
	
	@Test
	public void testFind_AtTheEnd() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		byte[] search = {3, 5};
		byte[] data = {2, 1, 3, 5};
		Method method = HttpRequestParser.class.getDeclaredMethod("find", byte[].class, int.class, byte[].class);
		method.setAccessible(true);
		int result = (int) method.invoke(HttpRequestParser.class, data, 0, search);
		assertEquals(2, result);
	}
	
	private HttpRequest generate(String req) {
		HttpRequest result = new HttpRequest();
		result.getBuffer().put(req.getBytes());
		return result;
	}
}
