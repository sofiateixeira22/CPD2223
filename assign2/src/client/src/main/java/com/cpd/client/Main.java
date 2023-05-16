package com.cpd.client;

import com.cpd.shared.Consts;
import com.cpd.shared.ControlInterface;
import com.cpd.shared.Util;
import com.cpd.shared.message.MsgString;

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
            MsgString response = stub.login("user", "password" + Util.sevenRandom());
            System.out.println("response: " + response.status());
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
