package com.cpd.server;

import com.cpd.shared.Consts;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
      // However, this does set the string that you need to use in order to look up the remote class.
      registry.rebind("Master", remoteObj);
    } catch (Exception e) {
      System.err.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
    System.out.println( "Bound!" );
    System.out.println( "Server will wait forever for messages." );
  }
}

