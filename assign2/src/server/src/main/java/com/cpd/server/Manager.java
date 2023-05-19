package com.cpd.server;

import com.cpd.shared.Util;
import com.cpd.shared.message.Stage;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Manager {
    private static Manager instance;
    private static final int ROOM_PLAYERS = 3;
    private static final Map<String, String> accounts = new HashMap<>();
    private static final Lock accountsLock = new ReentrantLock();
    private static final Map<String, String> activeToken = new HashMap<>();
    private static final Lock activeTokenLock = new ReentrantLock();
    private static final Map<String, Stage> userStages = new HashMap<>();
    private static final Lock userStagesLock = new ReentrantLock();
    private static final Queue<String> queue = new LinkedList<>();
    private static final Lock queueLock = new ReentrantLock();
    private static final Semaphore queueSemaphore = new Semaphore(ROOM_PLAYERS);
//    private static final TMap<String, LinkedList<Integer>> rooms = new TMap<>();
    private static final Logger logger = Logger.getLogger(Manager.class.getName());

    private Manager() {

    }

    public static Manager getInstance() {
        if (instance == null) {
            logger.info("Started new Manager.");
            instance = new Manager();
        }
        return instance;
    }

    private void addToken(String user) {
        String random = Util.sevenRandom();
        String rawToken = user + random;
        String token = Base64.getEncoder().encodeToString(rawToken.getBytes());

        activeToken.put(user, token);
    }

    /**
     * @param user existent account username
     * @param pass existent account password
     * @return String with token on valid credentials, or null otherwise
     */
    public String getToken(String user, String pass) {
        String res = null;
        try {
            accountsLock.lock();
            if (accounts.containsKey(user) && accounts.get(user).equals(pass)) {
                res = activeToken.get(user);
            }
        } finally {
            accountsLock.unlock();
        }
        return res;
    }

    private String getTokenUnSafe(String user) {
        if (accounts.containsKey(user)) {
            return activeToken.get(user);
        }
        return null;
    }

    public boolean register(String user, String pass) {
        boolean isValid = false;
        try {
            accountsLock.lock();
            if (!accounts.containsKey(user)) {
                // new user
                accounts.put(user, pass);
                isValid = true;
            }

            // verify old user
            if (accounts.get(user).equals(pass)) {
                isValid = true; // valid pass
            }
        } finally {
            accountsLock.unlock();
        }

        if (isValid) {
            try {
                activeTokenLock.lock();
                addToken(user);
                String token = getTokenUnSafe(user);
                userStagesLock.lock();
                userStages.put(token, Stage.LOGIN);
            } finally {
                accountsLock.unlock();
                userStagesLock.unlock();
            }
        }
        return isValid;
    }

    public Stage getStage(String token) {
        Stage res = null;
        try {
            userStagesLock.lock();
            if (userStages.containsKey(token)) {
                res = userStages.get(token);
            }
        } finally {
            userStagesLock.unlock();
        }
        return res;
    }

    public Stage update(String token) {
        return Stage.GAME;
    }

    public void joinQueue(String token) {
        queueLock.lock();

    }
}
