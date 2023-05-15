package com.cpd.server;

import com.cpd.shared.ControlInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;

public class ControlImpl extends UnicastRemoteObject implements ControlInterface {

  public ControlImpl() throws RemoteException {
    super();
  }

  public String login(String user, String pass) throws RemoteException {  
    String greeting = "Hello, " + user + "!";
    Shared sharedObj = Shared.getInstance();

    // create a new thread to handle the greeting
    Thread greetingThread = new Thread(() -> {
      System.out.println("Greeting thread started.");
      for (long i = 0; i < 1000000000; i++) {
        sharedObj.add();
      }
      System.out.println(sharedObj.get());
    });

    // start the greeting thread and return immediately
    greetingThread.start();

//    String encodedString = Base64.getEncoder().encodeToString((user+pass).getBytes());
//    System.out.println(encodedString);

    try {
      greetingThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return greeting;
  }
}
