package com.cpd.server;

import com.cpd.shared.message.Stage;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

import static com.cpd.shared.Util.sevenRandom;

public class Manager {
    private static Manager instance;
    public static final int ROOM_PLAYERS = 3;
    public static final int MAX_ROOMS = 2;
    public static final long GAME_TIME_LENGTH_MS = 15000L;
    ExecutorService executor = Executors.newFixedThreadPool(MAX_ROOMS);
    private static final Map<String, String> accounts = new HashMap<>();
    private static final Lock accountsLock = new ReentrantLock();
    private static final Map<String, String> userGameRoomID = new HashMap<>();
    private static final Lock userGameRoomIDLock = new ReentrantLock();
    private static final Map<String, String> activeToken = new HashMap<>();
    private static final Lock activeTokenLock = new ReentrantLock();
    private static final Map<String, Stage> usersStages = new HashMap<>();
    private static final Lock usersStagesLock = new ReentrantLock();
    private static final Queue<String> queue = new LinkedList<>();
    private static final Lock queueLock = new ReentrantLock();
    private static final Map<String, ArrayList<String>> roomsUsers = new HashMap<>();
    private static final Lock roomsUsersLock = new ReentrantLock();
    private static final Map<String, Long> roomsTimeStart = new HashMap<>();
    private static final Lock roomsTimeStartLock = new ReentrantLock();
    private static final Map<String, BiConsumer<String, Integer>> roomCB = new HashMap<>();
    private static final Lock roomCBLock = new ReentrantLock();
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

    private String genRoomID() {
        String s = sevenRandom() +
                sevenRandom() +
                Instant.now();

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

    public boolean checkUserRoomID(String token, String roomID) {
        try {
            userGameRoomIDLock.lock();
            return userGameRoomID.get(token).equals(roomID);
        } finally {
            userGameRoomIDLock.unlock();
        }
    }

    public Long getRoomTimeStart(String roomID) {
        try {
            roomsTimeStartLock.lock();
            return roomsTimeStart.get(roomID);
        } finally {
            roomsTimeStartLock.unlock();
        }
    }

    public void setRoomTimeStart(String roomID, long time) {
        try {
            roomsTimeStartLock.lock();
            roomsTimeStart.put(roomID, time);
        } finally {
            roomsTimeStartLock.unlock();
        }
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

    public boolean play(String token, String roomID, int value) {
        logger.info("play; token: " + token + " roomID: " + roomID + " value: " + value);
        if (!checkUserRoomID(token, roomID)) {
            return false;
        }
        try {
            roomCBLock.lock();
            roomCB.get(roomID).accept(token, value);
        } finally {
            roomCBLock.unlock();
        }
        return true;
    }

    public void endGame(String roomID) {
        try {
            roomsUsersLock.lock();
            usersStagesLock.lock();
            var users = roomsUsers.get(roomID);
            for (var userToken : users) {
                usersStages.put(userToken, Stage.RESULTS);
            }
            logger.info("Ending game " + roomID);
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
            userGameRoomIDLock.lock();
            Stage userStage = usersStages.get(token);
            if (userStage == Stage.LOGIN || userStage == Stage.RESULTS) {
                usersStages.put(token, Stage.QUEUE);
                queue.add(token);
                logger.info("User joining queue " + token);
            }
            if (queue.size() >= ROOM_PLAYERS) {
                String roomID = genRoomID();
                logger.info("Room being created: " + roomID);
                ArrayList<String> users = new ArrayList<>(ROOM_PLAYERS);
                for (int i = 0; i < ROOM_PLAYERS; i++) {
                    String userToken = queue.remove();
                    users.add(userToken);
                    usersStages.put(userToken, Stage.GAME);
                    userGameRoomID.put(userToken, roomID);
                }
                users.sort(String::compareTo);
                roomsUsers.put(roomID, users);
                logger.info("Assigned roomID: " + roomID + " to users: " + users);
                Runnable gameRunnable = new GameRunnable(roomID);
                executor.execute(gameRunnable);
            }
            // assign a room with thread
        } finally {
            queueLock.unlock();
            usersStagesLock.unlock();
            roomsUsersLock.unlock();
            userGameRoomIDLock.unlock();
        }
    }

    public String getRoomID(String token) {
        try {
            userGameRoomIDLock.lock();
            if (userGameRoomID.containsKey(token)) {
                return userGameRoomID.get(token);
            }
            return null;
        } finally {
            userGameRoomIDLock.unlock();
        }
    }
}
