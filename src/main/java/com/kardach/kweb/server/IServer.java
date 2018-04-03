package com.kardach.kweb.server;

public interface IServer {

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 8080;
	
	public void start();
}
