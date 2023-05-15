package com.cpd.server;

public class Shared {
  private static Shared instance;
  private Shared() {}

  private int times = 0;

  public static Shared getInstance() {
    if (instance == null) {
      instance = new Shared();
    }
    return instance;
  }

  public void add() {
    times++;
  }

  public int get() {
    return times;
  }
}
