package com.kardach.k.web.server.socket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response {

	public static enum Status {
		OK,
		ERROR
	}
	
	private Status status;
}
