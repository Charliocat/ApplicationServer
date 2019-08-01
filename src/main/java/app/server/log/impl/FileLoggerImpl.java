package app.server.log.impl;

import app.server.log.api.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FileLoggerImpl implements Logger {

    private File logFile;
    private ConcurrentLinkedQueue<String> writingQueue;

    public FileLoggerImpl(ConcurrentLinkedQueue<String> writingQueue, File logFile) {
        this.writingQueue = writingQueue;
        this.logFile = logFile;
    }

    @Override
    public void run() {
        if (writingQueue == null)
            throw new RuntimeException();

        //All the data that comes into the logging queue is printed to the file concurrently.
        try (PrintWriter writer = new PrintWriter(logFile)) {
            while (true) {
                String data = writingQueue.poll();
                if (data != null) {
                    writer.println(data);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
