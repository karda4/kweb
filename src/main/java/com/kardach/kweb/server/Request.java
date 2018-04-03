package com.kardach.kweb.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Request {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	public static final String HTTP_Version = "HTTP/1.1";
	public static final String SP = " ";
	public static final String CRLF = "\r\n";
	public static final byte[] CONTENT_LENGTH = "Content-Length".getBytes();
	public static final byte[] TWICE_CRLF = (CRLF + CRLF).getBytes();

	private ByteBuffer buffer;
	private Processor processor = Processor.getInstance();

	@Setter
	private HttpRequestMethod method;

	public Request() {
		buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}

	public ByteBuffer make() {
		log.info("HttpRequest:\n{}", this.toString());
		return toByteBuffer(processor.process(this.toString()));
	}

	private ByteBuffer toByteBuffer(Response<?> response) {
		buffer.clear();

		StringBuilder builder = new StringBuilder();
		// A Status-line
		builder.append(HTTP_Version).append(SP).append(response.getStatus().getValue()).append(SP)
				.append(response.getStatus().getReasonPhrase()).append(CRLF);
		// Zero or more header (General|Response|Entity) fields followed by CRLF
		builder.append(CRLF);
		// An empty line (CRLF) indicating the end of the header fields
		builder.append(CRLF);
		// Optionally a message-body

		String stringResponse = builder.toString();
		log.info("HttpResponse: {}", stringResponse);
		buffer.put(stringResponse.getBytes());
		return buffer;
	}

	@Override
	public String toString() {
		return new String(buffer.array(), StandardCharsets.UTF_8);
	}

	public boolean checkEndOfRequest() {
		byte[] data = this.getBuffer().array();

		parseHttpRequestMethod(data);
		switch(this.getMethod()) {
		case GET:
			long indexTwiceCRLF = searchAllIndex(0, data, Request.TWICE_CRLF);
			return indexTwiceCRLF >= 0;
		case DELETE:
			break;
		case POST:
			break;
		case PUT:
			break;
		default:
			break;
		}
		return false;
	}

	private void parseHttpRequestMethod(byte[] data) {
		if (this.getMethod() != null) {
			return;
		}
		for(HttpRequestMethod method : HttpRequestMethod.values()) {
			if(searchOneIndex(0, data, method.name())) {
				this.setMethod(method);
				break;
			}
		}
	}
	
	private long searchAllIndex(int startIndex, byte[] data, byte[] searchArray) {
		int dataLength = data.length;
		int searchLength = searchArray.length;
		if (startIndex + searchLength > dataLength) {
			return -1;
		}
		for (int i = startIndex; i <= dataLength - searchLength; i++) {
			if(searchOneIndex(i, data, searchArray)) {
				return i;
			}
		}
		return -1;
	}

	private boolean searchOneIndex(int index, byte[] data, String search) {
		return searchOneIndex(index, data, search.getBytes());
	}
	
	private boolean searchOneIndex(int index, byte[] data, byte[] searchArray) {
		int dataLength = data.length;
		int searchLength = searchArray.length;
		if (searchLength > dataLength) {
			return false;
		}
		if(index + searchLength > dataLength) {
			return false;
		}
		for (int i = 0; i < searchLength; i++) {
			if (data[i + index] != searchArray[i]) {
				return false;
			}
		}
		return true;
	}
}
