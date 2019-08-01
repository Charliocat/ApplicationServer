package app.utils.impl;

import app.utils.api.SynchronizedCounter;

public class Counter implements SynchronizedCounter {
    private int count = 0;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void resetCount() {
        count = 0;
    }

    @Override
    public synchronized void increment(){
        count += 1;
    }
}
