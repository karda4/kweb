package com.kardach.k.web.server.socket;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Request implements Callable<Response> {

	private SocketChannel socketChannel;

	public Request(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	@Override
	public Response call() throws Exception {
		log.info("Request started");
		socketChannel.close();
		return new Response(Response.Status.OK);
	}

}
