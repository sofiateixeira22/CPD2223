package com.cpd.server;

import com.cpd.shared.Consts;
import com.cpd.shared.PrintInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public String getGreeting() {
        return "Hello sWorld!";
    }

    public static void main(String[] args) {
        System.out.println(new Main().getGreeting());
        System.out.println(new Consts().getGreeting());

        PrintInterfaceImpl implementation = new PrintInterfaceImpl();
        try {
            // Export the object.
            PrintInterface stub = (PrintInterface) UnicastRemoteObject.exportObject(implementation, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            // I don't know why we have to rebind at all.
            // However, this does set the string that you need to use in order to lookup the remote class.
            registry.rebind("SayHello", stub);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println( "Bound!" );
        System.out.println( "Server will wait forever for messages." );
    }
}
