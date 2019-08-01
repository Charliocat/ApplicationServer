package app.server;

import app.server.log.api.Logger;
import app.server.log.impl.FileLoggerImpl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerManager {

    private final String LOG_FILE = "logs/numbers.log";

    private ConcurrentLinkedQueue<String> loggingQueue = new ConcurrentLinkedQueue<>();
    private int portNumber;

    public ServerManager(int portNumber) {
        this.portNumber = portNumber;
    }

    public void startServer() {
        //logger is a thread that reads from loggingQueue and writes to the app.server.log file
        try {
            Logger logger = new FileLoggerImpl(loggingQueue, createLogFile());

            //Use of daemon thread so it doesn't effect when the app.server shutdowns
            initDaemonThread(logger);
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        //app.server process the incoming data and writes it to the loggingQueue
        Server server = new Server(portNumber, loggingQueue);
        new Thread(server).start();
    }

    private File createLogFile() throws IOException {
        File file = new File(LOG_FILE);
        if (file.exists())
            file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    private void initDaemonThread(Runnable task){
        Thread log = new Thread(task);
        log.setDaemon(true);
        log.start();
    }

}
