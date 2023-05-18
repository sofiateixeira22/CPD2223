package com.cpd.client;

import com.cpd.shared.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

public class ClientImpl extends UnicastRemoteObject implements ClientInterface {
    private static final Logger logger = Logger.getLogger(ClientImpl.class.getName());

    public ClientImpl() throws RemoteException {
        super();
    }

    @Override
    public void ping() {
        logger.info("Sent ping.");
    }
}
