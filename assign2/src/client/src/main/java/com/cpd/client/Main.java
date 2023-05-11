package com.cpd.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Main {
    private static final String SERVER_ADDRESS = "server";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try {
            // Create a new socket channel and connect to the server
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(SERVER_ADDRESS, SERVER_PORT));

            // Write a message to the server
            String message = "Hello from the client!";
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);
            System.out.println("Sent message to server: " + message);

            // Read a response from the server
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
            socketChannel.read(responseBuffer);
            responseBuffer.flip();
            String response = new String(responseBuffer.array()).trim();
            System.out.println("Received message from server: " + response);

            // Close the socket channel
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
