package com.kardach.kweb.rest.endpoint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EndpointMethod {

	private Class<?> controller;
	private String name;
	private String[] args;
}
