package com.kardach.kweb.server;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@ToString
public class HttpRequest {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	private ByteBuffer buffer;
	private Processor processor = Processor.getInstance();
	
	@Setter
	private boolean completed = false;

	@Setter
	private HttpRequestMethod method;
	@Setter
	private String URI;
	@Setter
	private String httpVersion;
	@Setter
	private Map<String, String> headers = new HashMap<>();
	@Setter
	private String body;

	public HttpRequest() {
		buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
	}
	
	public void process(String request) {
		processor.process(request);
	}

	public ByteBuffer make() {
		log.info("HttpRequest:\n{}", this.toString());
		return toByteBuffer(processor.process(this.toString()));
	}

	private ByteBuffer toByteBuffer(HttpResponse<?> response) {
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

}
