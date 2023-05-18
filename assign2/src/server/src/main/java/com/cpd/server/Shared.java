package com.cpd.server;

public class Shared {
  private static Shared instance;
  private static Users users;

  private Shared() {
    users = new Users();
  }

  public static Shared getInstance() {
    if (instance == null) {
      instance = new Shared();
    }
    return instance;
  }

  public Users getUsers() {
    return users;
  }
}
