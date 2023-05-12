package com.cpd.client;

import java.io.*;
import java.net.*;
import com.cpd.shared.Consts;

public class Main {
    private static final String SERVER_ADDRESS = "server";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println(new Consts().getGreeting());
        try (
            // Create a new socket channel and connect to the server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            // Write a message to the server
            String message = "Hello from the client!";
            out.println(message);
            System.out.println("Sent message to server: " + message);

            // Read a response from the server
            String response = in.readLine();
            System.out.println("Received message from server: " + response);

            // Close the socket channel
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
