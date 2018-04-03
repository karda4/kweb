package com.kardach.kweb.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class Response<T> {

	private final HttpStatus status;
	private T body;
	
}
