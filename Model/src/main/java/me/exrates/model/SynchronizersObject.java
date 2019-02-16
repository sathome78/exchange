package me.exrates.model;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

@Getter@Setter
public class SynchronizersObject {

    private Object objectSync;
    private Semaphore semaphore;
    private CyclicBarrier cyclicBarrier;
    private ReentrantLock locker = new ReentrantLock();

    public static SynchronizersObject init() {
        SynchronizersObject object = new SynchronizersObject();
        object.objectSync = new Object();
        object.semaphore = new Semaphore(1);
        object.cyclicBarrier = new CyclicBarrier(1);
        object.locker = new ReentrantLock();
        return object;
    }
}
