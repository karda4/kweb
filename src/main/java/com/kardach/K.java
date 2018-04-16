package com.kardach;

import com.kardach.kweb.rest.endpoint.EndpointsResolver;
import com.kardach.kweb.server.IServer;
import com.kardach.kweb.server.epoll.EpollServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class K {

	public static void start(Class<?> root) {
		log.info("Starting KWeb app");
		EndpointsResolver.scan(root);
		
		IServer server = new EpollServer();
		server.start();
		log.info("Closed KWeb app");
	}
}
