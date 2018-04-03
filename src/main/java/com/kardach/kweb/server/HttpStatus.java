package com.kardach.kweb.server;

import lombok.Getter;

@Getter
public enum HttpStatus {
	
	OK(200, "OK"),
	BAD_REQUEST(400, "Bad Request"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	INTERNAL_SERVER_ERROR(500, "Internal Server Error");
	
	private final int value;

	private final String reasonPhrase;


	HttpStatus(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}
}
