package com.kardach.kweb.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

	public static final String HTTP_Version = "HTTP/1.1";
	public static final String SP = " ";
	public static final String CRLF = "\r\n";
	public static final String TWICE_CRLF = CRLF + CRLF;
	public static final String CONTENT_LENGTH = "Content-Length";
	
	public static boolean parse(final HttpRequest request) {
		if(request.isCompleted()) {
			return true;
		}
		final byte[] data = request.getBuffer().array();

		final String requestLine = parseRequestLine(data);
		if(requestLine == null) {
			return false;
		}
		String[] requestLineParts = requestLine.split(SP);
		
		final String httpRequestMethod = requestLineParts[0];
		HttpRequestMethod method = HttpRequestMethod.valueOf(httpRequestMethod);
		request.setMethod(method);
		
		final String httpRequestURI = requestLineParts[1];
		request.setURI(httpRequestURI);
		
		final String httpVersion = requestLineParts[2];
		request.setHttpVersion(httpVersion);
		
		//headers
		final int headersIndex = requestLine.length() + CRLF.length();
		final String headersString = parseHeadersString(data, headersIndex);
		if(headersString == null) {
			return false;
		}
		Map<String, String> headers = parseHeaders(headersString);
		if(headers == null) {
			return false;
		}
		request.setHeaders(headers);

		//body
		final String contentLegthValue = headers.get(CONTENT_LENGTH);
		if(contentLegthValue != null) {
			final int bodySize = Integer.valueOf(contentLegthValue);
			final int bodyIndex = headersIndex + headersString.length() + TWICE_CRLF.length();
			if(bodyIndex + bodySize > data.length) {
				return false;
			}
			final String body = new String(data, bodyIndex, bodySize);
			request.setBody(body);
		}
		request.setCompleted(true);
		return true;
	}
	
	private static String parseRequestLine(final byte[] data) {
		int index = find(data, 0, CRLF);
		if(index < 0) {
			return null;
		}
		return new String(data, 0, index);
	}
	
	private static String parseHeadersString(final byte[] data, final int indexFrom) {
		int indexTo = find(data, indexFrom, TWICE_CRLF);
		if(indexTo < 0) {
			return null;
		}
		return new String(data, indexFrom, indexTo - indexFrom);
	}
	
	private static Map<String, String> parseHeaders(final String headers) {
		Map<String, String> headersMap = new HashMap<>();
		Arrays.stream(headers.split(CRLF)).forEach(i -> {
			String[] parts = i.split(":");
			headersMap.put(parts[0].trim(), parts[1].trim());
		});
		return headersMap;
	}

	private static int find(final byte[] data, final int index, final String search) {
		return find(data, index, search.getBytes());
	}
	
	/**
	 * Finds position of search array in data array 
	 * 
	 * @param startIndex
	 * @param data - array to be searched
	 * @param search - searched array
	 * @return index of position if finded or -1 otherwise
	 */
	private static int find(final byte[] data, final int startIndex, final byte[] search) {
		for(int i = startIndex; i < data.length - search.length; i++) {
			boolean finded = true;
			for(int j = 0; j < search.length; j++) {
				if(data[i + j] != search[j]) {
					finded = false;
					break;
				}
			}
			if(finded) {
				return i;
			}
		}
		return -1;
		
		/*final int dataLength = data.length;
		final int searchLength = search.length;
		int index;
		int result = -1;
		for(int i = 0; i < searchLength; i++) {
			index = find(data, startIndex + i, dataLength, search[i]);
			if(index < 0) {
				return -1;
			}
			if(i == 0) {
				result = index;
			}
		}
		return result;*/
	}
	
	private static int find(final byte[] data, final int indexFrom, final int indexTo, final byte key) {
		for(int i = indexFrom; i < indexTo; i++) {
			if(data[i] == key) {
				return i;
			}
		}
		return -1;
	}
}
