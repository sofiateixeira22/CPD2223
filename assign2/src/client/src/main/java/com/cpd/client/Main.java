package com.cpd.client;

import com.cpd.shared.ControlInterface;
import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        String user = System.getenv("RMI_USER");
        String pass = System.getenv("RMI_PASS");

        logger.info("user: " + user + " pass: " + pass);

        try {
            Registry registry_server = LocateRegistry.getRegistry("server");
            ControlInterface stub = (ControlInterface) registry_server.lookup("Master");
            MsgString response = stub.login(user, pass);
            logger.info("response: " + response.status());

            String token = response.payload();
            logger.info("assigned token: " + token);

            Thread pingThread = new PingThread(token);
            pingThread.start();

//            Thread pingThread = new Thread(() -> {
//                try {
//                    Thread.sleep(3000);
//                    Registry reg = LocateRegistry.getRegistry("server");
//                    ControlInterface stub2 = (ControlInterface) reg.lookup("Master");
//                    MsgInfo resp = stub2.ping(token);
//                    logger.info("response: " + resp.status() + " with " + resp.stage());
//                } catch (InterruptedException | RemoteException | NotBoundException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            pingThread.start();

            MsgInfo queue_info = stub.findNewGame(token);
            logger.info("enter queue: " + queue_info.status() + "@" + queue_info.stage());
            MsgInfo response2 = stub.findNewGame(token);
            logger.info("Status: " + response2.status() + " Stage: " + response2.stage());

            Thread waitThread = new Thread(() -> {
                try {
                    Thread.sleep(4000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            waitThread.start();
            waitThread.join();

            String roomID = stub.ping(token).roomID();
            int rnd = new Random().nextInt(10);
            MsgInfo response3 = stub.play(token, roomID, rnd);

        } catch (Exception e) {
            logger.severe("Client exception: " + e);
            e.printStackTrace();
        }
    }
}
