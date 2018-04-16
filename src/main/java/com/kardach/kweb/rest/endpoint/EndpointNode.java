package com.kardach.kweb.rest.endpoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Endpoint node.
 * 
 * @author Yura Kardach
 *
 */
@Getter
@Setter
@ToString
public class EndpointNode {

	enum Type {
		ROOT, VARIABLE, PATH
	}

	private final Type type;
	private Set<String> variables;
	private String path;
	private Class<?> restController;
	private Method restMethod;

	private Map<String, EndpointNode> next = new HashMap<>();

	public EndpointNode(String value) {
		if (value == null) {
			this.type = Type.ROOT;
		} else {
			if (isPathVariable(value)) {
				this.type = Type.VARIABLE;
				value = value.substring(1, value.length() - 1);
				this.variables = new HashSet<>();
				this.variables.add(value);
			} else {
				this.type = Type.PATH;
				this.path = value;
			}
		}
	}

	private static boolean isPathVariable(String value) {
		return value.startsWith("{") && value.endsWith("}");
	}

	public boolean isPath() {
		return type.equals(Type.PATH);
	}
}
