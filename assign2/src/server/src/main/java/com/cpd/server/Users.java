package com.cpd.server;

import com.cpd.shared.tmap.TMap;

import java.time.Instant;

class Users {
    private TMap<String, String> login;
    private TMap<String, Long> lastUpdate;

    Users() {
        login = new TMap<>();
        lastUpdate = new TMap<>();
    }

    boolean login(String user, String pass) {
        if (login.contains(user)) {
            return login.get(user).equals(pass);
        }
        login.put(user, pass);
        return true;
    }

    void ping(String token) {
        if (lastUpdate.contains(token)) {
            long currentSeconds = Instant.now().getEpochSecond();
            lastUpdate.put(token, currentSeconds);
        }
    }
}
