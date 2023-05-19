package com.cpd.client;

import com.cpd.shared.ControlInterface;
import com.cpd.shared.message.MsgInfo;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class PingThread extends Thread {
    private static final Logger logger = Logger.getLogger(PingThread.class.getName());
    private final String token;

    public PingThread(String token) {
        this.token = token;
    }

    public void run() {
        logger.info("Ping Thread run");
        try {
            Thread.sleep(3000);
            Registry reg = LocateRegistry.getRegistry("server");
            ControlInterface stub2 = (ControlInterface) reg.lookup("Master");
            MsgInfo resp = stub2.ping(token);
            logger.info("response: " + resp.status() + " with " + resp.stage());
        } catch (InterruptedException | RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
