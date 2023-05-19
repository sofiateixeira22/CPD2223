package com.cpd.shared.tmap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TMap<K, V> {

    private final Map<K, V> map;
    private final Lock lock;

    public TMap() {
        lock = new ReentrantLock();
        map = new HashMap<>();
    }

    public boolean contains(K key) {
        boolean ret;
        lock.lock();
        try {
            ret = map.containsKey(key);
        } finally {
            lock.unlock();
        }
        return ret;
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            map.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        V ret;
        lock.lock();
        try {
            ret = map.get(key);
        } finally {
            lock.unlock();
        }
        return ret;
    }
}
