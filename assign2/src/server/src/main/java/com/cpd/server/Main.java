package com.cpd.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            // Export the object.
            Registry registry = LocateRegistry.createRegistry(1099);
            // I don't know why we have to rebind at all.
            // However, this does set the string that you need to use in order to look up the remote class.
            ControlImpl remoteObj = new ControlImpl();
            registry.rebind("Master", remoteObj);

        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
        logger.info( "Server ready..." );
    }
}
