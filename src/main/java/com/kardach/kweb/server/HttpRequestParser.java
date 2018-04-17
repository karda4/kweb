package com.kardach.kweb.server;

public class HttpRequestParser {

	public static final String HTTP_Version = "HTTP/1.1";
	public static final String SP = " ";
	public static final String CRLF = "\r\n";
	public static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();
	
	public static boolean parse(final Request request) {
		final byte[] data = request.getBuffer().array();

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
	
	private static String parseRequestLine(final byte[] data) {
		int index = find(0, data, CRLF);
		if(index < 0) {
			return null;
		}
		return new String(data, 0, index);
	}

	private static String parseHttpRequestMethod(final byte[] data) {
		int index = find(0, data, SP);
		if(index < 0) {
			return null;
		}
		return new String(data, 0, index);
	}
	
	private static int find(final int index, final byte[] data, final String search) {
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
		final int dataLength = data.length;
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
		return result;
	}
	
	private static int find(final byte[] data, int indexFrom, int indexTo, byte key) {
		for(int i = indexFrom; i < indexTo; i++) {
			if(data[i] == key) {
				return i;
			}
		}
		return -1;
	}
}
