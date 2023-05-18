package com.cpd.server;

import com.cpd.shared.ClientInterface;
import com.cpd.shared.Util;
import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;
import com.cpd.shared.message.Stage;
import com.cpd.shared.message.Status;
import com.cpd.shared.ControlInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Base64;
import java.util.logging.Logger;

public class ControlImpl extends UnicastRemoteObject implements ControlInterface {

    private static final Logger logger = Logger.getLogger(ControlImpl.class.getName());

    public ControlImpl() throws RemoteException {
        super();
    }

    @Override
    public MsgString login(String user, String pass) throws RemoteException {
        String random = Util.sevenRandom();
        String tokenCat = user + pass + random;

        Status msgStatus = Status.OK;

        String token = Base64.getEncoder().encodeToString(tokenCat.getBytes());

        logger.info("New login from: " + user + " with token " + token);
        Shared sharedObj = Shared.getInstance();

        if (!sharedObj.getUsers().login(user, pass)) {
            msgStatus = Status.BAD_CREDENTIALS;
        } else {
            Shared.getInstance().getUsers().getStages().put(token, Stage.LOGIN);
        }
        // create a new thread to handle the greeting
//        Thread pingThread = new Thread(() -> {
//            try {
//                Thread.sleep(3000);
//                logger.info("thread token " + token);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
        // start the greeting thread and return immediately
//        pingThread.start();
//    try {
//      greetingThread.join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }

        return new MsgString(msgStatus, token);
    }

    @Override
    public MsgInfo ping(String token) throws RemoteException {
        Status status = Status.OK;
        Stage stage;
        Integer round = null;
        Long timeleft = null;

        stage = Shared.getInstance().getUsers().getUserStage(token);

        if (stage == Stage.GAME) {
            round = 1;
            timeleft = 2L;
        }

        return new MsgInfo(status, stage, round, timeleft);
    }
}
