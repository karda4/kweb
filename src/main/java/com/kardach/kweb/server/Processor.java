package com.kardach.kweb.server;

public class Processor {

	private static Processor instance;
	
	private Processor() {}
	
	public static Processor getInstance() {
		if(instance == null) {
			instance = new Processor();
		}
		return instance;
	}
	
	public Response<?> process(String request) {
		return new Response<>(HttpStatus.OK);
	}
}
