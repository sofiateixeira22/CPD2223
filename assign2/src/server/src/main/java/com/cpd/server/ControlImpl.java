package com.cpd.server;

import com.cpd.shared.ControlInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ControlImpl extends UnicastRemoteObject implements ControlInterface {

  public ControlImpl() throws RemoteException {
    super();
  }

  public String login(String user, String pass) throws RemoteException {  
    String greeting = "Hello, " + user + "!";
    Shared sharedObj = Shared.getInstance();
    sharedObj.add();
    System.out.println(sharedObj.get());

    // create a new thread to handle the greeting
    Thread greetingThread = new Thread(() -> {
      System.out.println("Greeting thread started.");
      try {
	Thread.sleep(5000); // simulate some work
      } catch (InterruptedException e) {
	e.printStackTrace();
      }
      System.out.println(greeting);
    });

    // start the greeting thread and return immediately
    greetingThread.start();
    try {
      greetingThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    return greeting;
  }
}
