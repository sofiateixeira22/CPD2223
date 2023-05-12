package com.cpd.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.cpd.shared.Consts;

public class Main {

    private static final int SERVER_PORT = 8080;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        System.out.println(new Consts().getGreeting());
        try {
            // Create a new selector
            Selector selector = Selector.open();

            // Create a new server socket channel and register it with the selector
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port " + SERVER_PORT);

            // Wait for incoming connections and handle them
            while (true) {
                int numReadyChannels = selector.select();
                if (numReadyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isAcceptable()) {
                        // Accept a new connection and register it with the selector
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("New client connected from " + socketChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        // Read data from the socket and echo it back to the client
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        int numBytesRead = socketChannel.read(buffer);
                        if (numBytesRead == -1) {
                            // Connection has been closed by the client
                            key.cancel();
                            System.out.println("Client disconnected from " + socketChannel.getRemoteAddress());
                            socketChannel.close();
                        } else {
                            // Echo the data back to the client
                            buffer.flip();
                            socketChannel.write(buffer);
                        }
                    }

                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
