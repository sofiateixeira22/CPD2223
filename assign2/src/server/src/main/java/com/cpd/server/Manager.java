package com.cpd.server;

import com.cpd.shared.message.Stage;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static com.cpd.shared.Util.sevenRandom;

public class Manager {
    private static Manager instance;
    private static final int ROOM_PLAYERS = 3;
    private static final int MAX_ROOMS = 5;
    private static Map<String, String> accounts;
    private static Lock accountsLock;
    private static Map<String, String> activeToken;
    private static Lock activeTokenLock;
    private static Map<String, Stage> usersStages;
    private static Lock usersStagesLock;
    private static Queue<String> queue;
    private static Lock queueLock;
    private static Map<String, ArrayList<String>> roomsUsers;
    private static Lock roomsUsersLock;
    private static Map<String, BiConsumer<String, Integer>> roomCB;
    private static Lock roomCBLock;
    private static final Logger logger = Logger.getLogger(Manager.class.getName());

    private Manager() {
        accounts = new HashMap<>();
        accountsLock = new ReentrantLock();
        activeToken = new HashMap<>();
        activeTokenLock = new ReentrantLock();
        usersStages = new HashMap<>();
        usersStagesLock = new ReentrantLock();
        queue = new LinkedList<>();
        queueLock = new ReentrantLock();
        roomsUsers = new HashMap<>();
        roomsUsersLock = new ReentrantLock();
        roomCB = new HashMap<>();
        roomCBLock = new ReentrantLock();
    }

    public static Manager getInstance() {
        if (instance == null) {
            logger.info("Started new Manager.");
            instance = new Manager();
        }
        return instance;
    }

    private String genRoomID(List<String> usersList) {
        String s = usersList.toString() +
                Instant.now() +
                sevenRandom();

        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    private void addToken(String user) {
        String random = sevenRandom();
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
            activeTokenLock.lock();
            usersStagesLock.lock();
            if (!accounts.containsKey(user)) {
                // new user
                accounts.put(user, pass);
                isValid = true;
            }

            // verify old user
            if (accounts.get(user).equals(pass)) {
                isValid = true; // valid pass
            }

            if (isValid) {
                addToken(user);
                String token = getTokenUnSafe(user);
                usersStages.put(token, Stage.LOGIN);
            }
        } finally {
            accountsLock.unlock();
            activeTokenLock.unlock();
            usersStagesLock.unlock();
        }
        return isValid;
    }

    public Stage getStage(String token) {
        Stage res = null;
        try {
            usersStagesLock.lock();
            if (usersStages.containsKey(token)) {
                res = usersStages.get(token);
            }
        } finally {
            usersStagesLock.unlock();
        }
        return res;
    }

    public void setRoomCB(String roomID, BiConsumer<String, Integer> callback) {
        try {
            roomCBLock.lock();
            roomCB.put(roomID, callback);
        } finally {
            roomCBLock.unlock();
        }
    }

    public void endGame(String roomID) {
        try {
            roomsUsersLock.lock();
            usersStagesLock.lock();
            var users = roomsUsers.get(roomID);
            for (var userToken : users) {
                usersStages.put(userToken, Stage.RESULTS);
            }
        } finally {
            roomsUsersLock.unlock();
            usersStagesLock.unlock();
        }
    }

    public void joinQueue(String token) {
        try {
            usersStagesLock.lock();
            queueLock.lock();
            roomsUsersLock.lock();
            Stage userStage = usersStages.get(token);
            if (userStage == Stage.LOGIN || userStage == Stage.RESULTS) {
                usersStages.put(token, Stage.QUEUE);
                queue.add(token);
                logger.info("User joining queue " + token);
            }
            if (queue.size() >= ROOM_PLAYERS) {
                logger.info("Room being created.");
                ArrayList<String> users = new ArrayList<>(ROOM_PLAYERS);
                for (int i = 0; i < ROOM_PLAYERS; i++) {
                    String userToken = queue.remove();
                    users.add(userToken);
                    usersStages.put(userToken, Stage.GAME);
                }
                users.sort(String::compareTo);
                String roomID = genRoomID(users);
                roomsUsers.put(roomID, users);
                logger.info("Assigned roomID: " + roomID + " to users: " + users);
            }
            // assign a room with thread
        } finally {
            queueLock.unlock();
            usersStagesLock.unlock();
            roomsUsersLock.unlock();
        }
    }
}
