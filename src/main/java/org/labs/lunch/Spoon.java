package org.labs.lunch;

import java.util.concurrent.locks.ReentrantLock;

public class Spoon {
    private final int id;
    private final ReentrantLock lock;

    public Spoon(int id) {
        this.id = id;
        lock = new ReentrantLock(true);
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
