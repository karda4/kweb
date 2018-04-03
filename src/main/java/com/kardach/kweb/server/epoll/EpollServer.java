package com.kardach.kweb.server.epoll;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import com.kardach.kweb.server.IServer;
import com.kardach.kweb.server.Request;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EpollServer implements IServer {

	private final InetSocketAddress bindAddress;
	
	private ServerSocketChannel serverSocketChannel;
	
	public EpollServer() {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}
	
	public EpollServer(final String host, final int port) {
		this.bindAddress = new InetSocketAddress(host, port);
	}

	@Override
	public void start() {
		boolean serverChannelOpened = openServerSocketChannel();
		if(!serverChannelOpened) {
			return;
		}

		Selector selector = null;
		try {
			selector = SelectorProvider.provider().openSelector();
		} catch (IOException e) {
			log.error("Open Selector.  [{}]", e.getMessage());
			return;
		}
		try {
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (ClosedChannelException e) {
			log.error("Register ServerSocketChannel to Acceptable. [{}]", e.getMessage());
			return;
		}

		log.info("EpollServer is started on = [{}]", this.bindAddress);
		while (true) {
			try {
				selector.select();
			} catch (IOException e) {
				log.error("Await select. [{}]", e.getMessage());
			}
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();
			while (iter.hasNext()) {
				SelectionKey key = iter.next();
				iter.remove();
				if (key.isAcceptable()) {
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
					try {
						SocketChannel client = serverSocketChannel.accept();
						log.info("Acceptable client SocketChannel: [{}]", client.socket().getLocalAddress());
						client.configureBlocking(false);
						Request request = new Request();
						SelectionKey clientReadKey = client.register(selector, SelectionKey.OP_READ);
						clientReadKey.attach(request);
					} catch (IOException e) {
						log.error("Accept client SocketChannel.  [{}]", e.getMessage());
					}					
				}
				if (key.isReadable()) {
					readSocket(key);
				}
			}
		}
	}
	
	private boolean openServerSocketChannel() {
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.socket().bind(bindAddress);
			return true;
		} catch (IOException e) {
			log.error("Open ServerSocketChannel. [{}]", e.getMessage());
		}
		return false;
	}

	private void readSocket(SelectionKey key) {
		SocketChannel client = (SocketChannel) key.channel();
		Request request = (Request) key.attachment();
		int amount = -1;
		try {
			amount = client.read(request.getBuffer());
			String readed = new String(request.getBuffer().array(), (request.getBuffer().position()) - amount, amount);
			log.info("Had Read [{}] bytes: {}", amount, readed);
//			if() {
		} catch (IOException e) {
			log.error("Read from client SocketChannel. [{}]", e.getMessage());
		}
		if (amount == -1 || request.checkEndOfRequest()) {
			try {
				client.write(request.make());
			} catch (IOException e) {
				log.error("Write to client SocketChannel. [{}]", e.getMessage());
			}
			request = null;
			try {
				client.close();
			} catch (IOException e) {
				log.error("Close client SocketChannel. [{}]", e.getMessage());
			}
			client = null;
			key.cancel();
		}
	}

}
