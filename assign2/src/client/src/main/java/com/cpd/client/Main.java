package com.cpd.client;

import com.cpd.shared.Consts;
import com.cpd.shared.PrintInterface;
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
            PrintInterface stub = (PrintInterface) registry.lookup("SayHello");
            String response = stub.sayHello();
            System.out.println("response: " + response);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}

