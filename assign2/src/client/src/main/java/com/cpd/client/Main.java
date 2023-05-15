package com.cpd.client;

import com.cpd.shared.Consts;
import com.cpd.shared.ControlInterface;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public String getGreeting() {
        return "Hello cWorld!";
    }

    public static void main(String[] args) {
        System.out.println(new Main().getGreeting());
        System.out.println(new Consts().getGreeting());

        try {
            Registry registry = LocateRegistry.getRegistry("server");
            ControlInterface stub = (ControlInterface) registry.lookup("Master");
            String response = stub.login("username", "password");
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

