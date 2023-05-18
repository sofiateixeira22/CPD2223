package com.cpd.client;

import com.cpd.shared.ClientInterface;
import com.cpd.shared.Consts;
import com.cpd.shared.ControlInterface;
import com.cpd.shared.Util;
import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static String token;

    public String getGreeting() {
        return "Hello cWorld!";
    }

    public static void main(String[] args) {
        System.out.println(new Main().getGreeting());
        System.out.println(new Consts().getGreeting());

        String user = System.getenv("RMI_USER");
        String pass = System.getenv("RMI_PASS");

        logger.info("user: " + user + " pass: " + pass);

        try {
            Registry registry_server = LocateRegistry.getRegistry("server");
            ControlInterface stub = (ControlInterface) registry_server.lookup("Master");
            MsgString response = stub.login(user, pass);
            logger.info("response: " + response.status());

            token = response.payload();
            logger.info("assigned token: " + token);

            Thread pingThread = new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Registry reg = LocateRegistry.getRegistry("server");
                    ControlInterface stub2 = (ControlInterface) reg.lookup("Master");
                    MsgInfo resp = stub2.ping(token);
                    logger.info("response: " + resp.status() + " with " + resp.stage());
                } catch (InterruptedException | RemoteException | NotBoundException e) {
                    throw new RuntimeException(e);
                }
            });
            pingThread.start();
        } catch (Exception e) {
            logger.severe("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
