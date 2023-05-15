package com.cpd.server;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Shared {
  private static Shared instance;
  private Shared() {}

  private long times = 0;
  private final Lock lock = new ReentrantLock();

  public static Shared getInstance() {
    if (instance == null) {
      instance = new Shared();
    }
    return instance;
  }

  public void add() {
    lock.lock();
    try {
      times++;
    } finally {
      lock.unlock();
    }
  }

  public void addUnsafe() {
    times++;
  }

  public long get() {
    return times;
  }

  public void set(long n) {
    times = n;
  }

  public void reset() {
    times = 0;
  }
}
