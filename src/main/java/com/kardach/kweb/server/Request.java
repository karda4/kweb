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

	private ByteBuffer buffer;
	private Processor processor = Processor.getInstance();
	
	private boolean completed = false;

	@Setter
	private HttpRequestMethod method;

	public Request() {
		buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}
	
	public void process(String request) {
		processor.process(request);
	}

	public ByteBuffer make() {
		log.info("HttpRequest:\n{}", this.toString());
		return toByteBuffer(processor.process(this.toString()));
	}

	private ByteBuffer toByteBuffer(Response<?> response) {
		buffer.clear();

		StringBuilder builder = new StringBuilder();
		// A Status-line
		/*builder.append(HTTP_Version).append(SP).append(response.getStatus().getValue()).append(SP)
				.append(response.getStatus().getReasonPhrase()).append(CRLF);
		// Zero or more header (General|Response|Entity) fields followed by CRLF
		builder.append(CRLF);
		// An empty line (CRLF) indicating the end of the header fields
		builder.append(CRLF);*/
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

}
