package com.kardach.kweb.rest.endpoint;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class EndpointNode {

	public static enum Type {
		URL,
		Variable
	}

	private Type type;
	private String value;
	private List<EndpointNode> next = new ArrayList<>();

	public Type getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public List<EndpointNode> getNext() {
		return next;
	}


}
