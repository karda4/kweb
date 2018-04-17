package com.kardach.kweb.server;

import java.util.Arrays;

public class HttpRequestParser {

	public static final String HTTP_Version = "HTTP/1.1";
	public static final String SP = " ";
	public static final String CRLF = "\r\n";
	public static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();
	//public static final byte[] TWICE_CRLF = (CRLF + CRLF).getBytes();
	
	public static boolean parse(Request request) {
		byte[] data = request.getBuffer().array();

		final String requestLine = parseRequestLine(data);
		if(requestLine == null) {
			return false;
		}
		final String httpRequestMethod = parseHttpRequestMethod(data);
		if(httpRequestMethod == null) {
			return false;
		}
		HttpRequestMethod method = HttpRequestMethod.valueOf(httpRequestMethod);
		request.setMethod(method);
		return true;
	}
	
	private static String parseRequestLine(byte[] data) {
		int index = find(0, data, CRLF);
		if(index < 0) {
			return null;
		}
		return new String(data, 0, index);
	}

	private static String parseHttpRequestMethod(byte[] data) {
		int index = find(0, data, SP);
		if(index < 0) {
			return null;
		}
		return new String(data, 0, index);
	}
	
	private static int find(int index, byte[] data, String search) {
		return find(index, data, search.getBytes());
	}
	
	/**
	 * Finds position of search array in data array 
	 * 
	 * @param startIndex
	 * @param data - array to be searched
	 * @param search - searched array
	 * @return index of position if finded or -1 otherwise
	 */
	private static int find(int startIndex, byte[] data, byte[] search) {
		int dataLength = data.length;
		int index;
		int result = -1;
		for(int i = 0; i < dataLength; i++) {
			index = Arrays.binarySearch(data, startIndex + i, dataLength, search[i]);
			if(index < 0) {
				return -1;
			}
			if(i == 0) {
				result = index;
			}
		}
		return result;
	}
}
