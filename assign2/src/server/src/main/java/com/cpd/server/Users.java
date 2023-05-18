package com.cpd.server;

import com.cpd.shared.message.Stage;
import com.cpd.shared.tmap.TMap;

import java.time.Instant;

class Users {
    private final TMap<String, String> login;
    private final TMap<String, Stage> stages;
    private final TMap<String, Long> lastUpdate;

    Users() {
        login = new TMap<>();
        stages = new TMap<>();
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

    public TMap<String, Stage> getStages() {
        return stages;
    }

    public Stage getUserStage(String token) {
        return stages.get(token);
    }
}
