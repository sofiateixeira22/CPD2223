package com.cpd.server;

import com.cpd.shared.ControlInterface;
import com.cpd.shared.Util;
import com.cpd.shared.message.MsgInfo;
import com.cpd.shared.message.MsgString;
import com.cpd.shared.message.Stage;
import com.cpd.shared.message.Status;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.Instant;
import java.util.Base64;
import java.util.logging.Logger;

import static java.lang.Long.max;

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
        String roomID;
        Long timeLeft = null;

        stage = manager.getStage(token);
        roomID = manager.getRoomID(token);

        if (stage == null) {
            return new MsgInfo(Status.ERROR, null, null, null);
        }

        if (stage == Stage.GAME) {
            timeLeft = 2L;
        }

        return new MsgInfo(status, stage, roomID, timeLeft);
    }

    @Override
    public MsgInfo findNewGame(String token) throws RemoteException {
        logger.info("User " + token + " in queue");
        Manager manager = Manager.getInstance();
        manager.joinQueue(token);
        return new MsgInfo(Status.OK, Stage.QUEUE, null, null);
    }

    @Override
    public MsgInfo play(String token, String roomID, int value) throws RemoteException {
        Status status = Status.ERROR;
        Stage stage = null;
        Long timeLeft = null;
        Long timeStart = null;

        Manager manager = Manager.getInstance();

        if (!manager.play(token, roomID, value)) {
            return new MsgInfo(status, null, null, null);
        }

        stage = manager.getStage(token);

        timeStart = manager.getRoomTimeStart(roomID);
        long timeEnd = timeStart + Manager.GAME_TIME_LENGTH_MS;
        long timeNow = Instant.now().getEpochSecond();
        timeLeft = max(0, timeEnd - timeNow);

        return new MsgInfo(status, stage, roomID, timeLeft);
    }
}
