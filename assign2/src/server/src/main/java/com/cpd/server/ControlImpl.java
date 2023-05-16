package com.cpd.server;

import com.cpd.shared.Util;
import com.cpd.shared.message.MsgString;
import com.cpd.shared.message.Status;
import com.cpd.shared.ControlInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.logging.Logger;

public class ControlImpl extends UnicastRemoteObject implements ControlInterface {

  private static final Logger logger = Logger.getLogger(ControlImpl.class.getName());

  public ControlImpl() throws RemoteException {
    super();
  }

  public MsgString login(String user, String pass) throws RemoteException {
    String random = Util.sevenRandom();
    String tokenCat = user + pass + random;

    Status msgStatus = Status.OK;

    String token = Base64.getEncoder().encodeToString(tokenCat.getBytes());

    logger.info("New login from: " + user + " with token " + token);
    Shared sharedObj = Shared.getInstance();

    if (!sharedObj.getUsers().login(user, pass)) {
      msgStatus = Status.BAD_CREDENTIALS;
    }

    return new MsgString(msgStatus, token);

//     // create a new thread to handle the greeting
//    Thread greetingThread = new Thread(() -> {
//      for (long i = 0; i < 1000000000; i++) {
//        sharedObj.addUnsafe();
//      }
//    });
//    // start the greeting thread and return immediately
//    greetingThread.start();
//    try {
//      greetingThread.join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }

  }
}
