package app.server;

import app.server.protocol.ClientTask;
import app.server.report.api.Reporter;
import app.server.report.impl.ReporterImpl;
import app.utils.api.SynchronizedCounter;
import app.utils.impl.Counter;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.*;

public class Server implements Runnable {

    private int portNumber;
    private ServerSocket serverSocket;
    private ConcurrentLinkedQueue<String> loggingQueue;
    private Collection<String> uniquesSet = Collections.synchronizedCollection(new HashSet<>());
    private SynchronizedCounter uniqueCount = new Counter();
    private SynchronizedCounter repeatedCount = new Counter();
    private volatile boolean isStopped = false;
    private ExecutorService executorService;
    private Reporter reporter = new ReporterImpl(this);
    private BlockingQueue<Boolean> terminateQueue = new ArrayBlockingQueue<>(1);

    public Server(int portNumber, ConcurrentLinkedQueue<String> loggingQueue) {
        this.portNumber = portNumber;
        this.loggingQueue = loggingQueue;
    }

    public Collection<String> getUniquesSet() {
        return uniquesSet;
    }

    public int getUniqueCount() {
        return uniqueCount.getCount();
    }

    public void resetUnique() {
        uniqueCount.resetCount();
    }

    public int getRepeatedCount() {
        return repeatedCount.getCount();
    }

    public void resetRepeated() {
        this.repeatedCount.resetCount();
    }

    @Override
    public void run() {
        System.out.println("Server is running...");
        openServerSocket();

        /*Reporter uses a daemon to write standard output reports every 10s.
        * Reporter resets the repeated and unique count to obtain new data for the following iteration*/
        reporter.startReporting();

        /*
            This thread keeps reading read the terminate queue and if a "terminate" sequence is processed,
            it starts the shutting down process.
        */
        Thread running = new Thread(new Terminate(terminateQueue));
        running.setDaemon(true);
        running.start();

        //Only a pool of 5 threads is used to meet the requirements
        executorService = Executors.newFixedThreadPool(5);
        while (!isStopped) {
            try {
                executorService.submit(new ClientTask(
                        serverSocket.accept(), uniquesSet, uniqueCount, repeatedCount, loggingQueue,
                        terminateQueue));

            } catch (IOException e) {
                System.out.println("Exception caught when trying to listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }

        if (!isStopped)
            stop();
        //stop();
    }

    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + portNumber, e);
        }
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private synchronized void stop() {
        System.out.println("Shut down server...");
        shutdownExecutorService();
        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private final class Terminate implements Runnable {
        private BlockingQueue<Boolean> queue;

        private Terminate(BlockingQueue<Boolean> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (queue.isEmpty()){}
            stop();
        }
    }
}
