package com.cpd.server;

import java.rmi.registry.Registry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Shared {
  private static Shared instance;
  private static Users users;
  private static Registry registry;
//  private long times;
//  private final Lock lock;

  private Shared() {
//    times = 0;
//    lock = new ReentrantLock();
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

  public Registry getRegistry() {
    return registry;
  }

  public void setRegistry(Registry registry) {
    Shared.registry = registry;
  }

//  public void add() {
//    lock.lock();
//    try {
//      times++;
//    } finally {
//      lock.unlock();
//    }
//  }
//
//  public void addUnsafe() {
//    times++;
//  }
//
//  public long get() {
//    return times;
//  }
//
//  public void set(long n) {
//    times = n;
//  }
//
//  public void reset() {
//    times = 0;
//  }
}
