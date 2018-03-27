package com.kardach.k.web;

import java.io.IOException;

import com.kardach.k.web.server.IServer;
import com.kardach.k.web.server.epoll.EpollServer;
import com.kardach.k.web.server.socket.SocketServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {

	public static void main(String[] args) throws IOException {
		log.info("Starting K.WEB app");
		IServer server = new EpollServer();
		server.start();
		log.info("Closed K.WEB app");
	}
}
