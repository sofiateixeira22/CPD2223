package com.cpd.server;

import com.cpd.shared.ControlInterface;
import com.cpd.shared.Util;
import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;
import com.cpd.shared.message.Stage;
import com.cpd.shared.message.Status;

import java.rmi.RemoteException;
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

        Manager manager = Manager.getInstance();
        boolean valid = manager.register(user, pass);
        String token = null;
        Status msgStatus = Status.OK;

        if (valid) {
            token = manager.getToken(user, pass);
        } else {
            msgStatus = Status.BAD_CREDENTIALS;
        }

        logger.info("New login from: " + user + " with token " + token);
        return new MsgString(msgStatus, token);
    }

    @Override
    public MsgInfo ping(String token) throws RemoteException {

        Manager manager = Manager.getInstance();
        Status status = Status.OK;
        Stage stage;
        Long timeLeft = null;

        stage = manager.getStage(token);

        if (stage == null) {
            return new MsgInfo(Status.ERROR, null, null);
        }

        if (stage == Stage.GAME) {
            timeLeft = 2L;
        }

        return new MsgInfo(status, stage, timeLeft);
    }

    @Override
    public MsgInfo findNewGame(String token) throws RemoteException {
        logger.info("User " + token + " in queue");
        Manager manager = Manager.getInstance();
        manager.joinQueue(token);
        return new MsgInfo(Status.OK, Stage.QUEUE, null);
    }
}
