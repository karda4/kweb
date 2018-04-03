package com.kardach.kweb.server.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.kardach.kweb.exception.ServerException;
import com.kardach.kweb.server.IServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketServer implements IServer {

	private static ServerSocketChannel serverSocketChannel;

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	private int port = DEFAULT_PORT;
	
	@Override
	public void start() {
		start(port);
	}

	private void start(int port) {
		try {
			serverSocketChannel = ServerSocketChannel.open();
		} catch (IOException e) {
			String errorMessage = "Can't open ServerSocketChannel: " + e.getMessage();
			log.error(errorMessage);
			close();
		}
		try {
			serverSocketChannel.socket().bind(new InetSocketAddress(port));
		} catch (IOException e1) {
			String errorMessage = "Can't bind ServerSocketChannel on port=" + port + ": " + e1.getMessage();
			log.error(errorMessage);
			close();
		}
		log.info("Started server on port " + port);
		SocketChannel socketChannel = null;
		while (true) {
			try {
				socketChannel = serverSocketChannel.accept();
			} catch (IOException e) {
				String errorMessage = "Can't accept SocketChannel: " + e.getMessage();
				log.error(errorMessage);
			}

			Future<Response> future = executorService.submit(new Request(socketChannel));
			// do something with socketChannel...
		}
	}

	public void close() {
		if (serverSocketChannel != null) {
			try {
				log.info("Closing server");
				serverSocketChannel.close();
			} catch (IOException e) {
				String errorMessage = "Can't close ServerSocketChannel: " + e.getMessage();
				log.error(errorMessage);
				throw new ServerException(errorMessage);
			}
		}
	}
}
