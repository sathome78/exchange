package me.exrates.model;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

@Getter@Setter
public class SynchronizersObject {

    private Object objectSync;
    private Semaphore semaphore;

    public static SynchronizersObject init() {
        SynchronizersObject object = new SynchronizersObject();
        object.objectSync = new Object();
        object.semaphore = new Semaphore(1);
        return object;
    }
}
