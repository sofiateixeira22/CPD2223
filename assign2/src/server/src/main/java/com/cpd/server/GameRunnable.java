package com.cpd.server;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class GameRunnable implements Runnable{
    private final String roomID;
    private final Map<String, Integer> answers = new HashMap<>();
    private final Lock answersLock = new ReentrantLock();
    private static final Logger logger = Logger.getLogger(GameRunnable.class.getName());


    public GameRunnable(String roomID) {
        this.roomID = roomID;
    }

    @Override
    public void run() {
        logger.info("GameRunner: " + roomID + " start.");
        Manager manager = Manager.getInstance();
        manager.setRoomTimeStart(roomID, Instant.now().getEpochSecond());
        manager.setRoomCB(roomID, (token, value) -> {
            try {
                answersLock.lock();
                answers.put(token, value);
                logger.info("New item @" + roomID + " User: " + token + " Value: " + value);
            } finally {
                answersLock.unlock();
            }
        });
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String maxToken = "";
        int maxValue = -1;

        logger.warning(answers.toString());

        for (var token : answers.keySet()) {
            var value = answers.get(token);
            if (value > maxValue) {
                maxToken = token;
                maxValue = value;
            }
        }

        logger.info("User " + maxToken + " won with value: " + maxValue);

        manager.endGame(roomID);
    }
}
