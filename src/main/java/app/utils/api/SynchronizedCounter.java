package app.utils.api;

public interface SynchronizedCounter {
    int getCount();

    void resetCount();

    void increment();
}
