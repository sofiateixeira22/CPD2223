package com.cpd.server;

public class Manager {
    private static Manager instance;
    private static Users users;

    private Manager() {
        users = new Users();
    }

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public Users getUsers() {
        return users;
    }
}
