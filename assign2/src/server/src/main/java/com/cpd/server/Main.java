package com.cpd.server;

import com.cpd.shared.Consts;
import com.cpd.shared.ControlInterface;
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

    try {
      ControlImpl remoteObj = new ControlImpl();
      // Export the object.
      Registry registry = LocateRegistry.createRegistry(1099);
      // I don't know why we have to rebind at all.
      // However, this does set the string that you need to use in order to lookup the remote class.
      registry.rebind("Master", remoteObj);
    } catch (Exception e) {
      System.err.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println( "Bound!" );
    System.out.println( "Server will wait forever for messages." );
  }
}

