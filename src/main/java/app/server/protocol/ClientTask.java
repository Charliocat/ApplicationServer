package app.server.protocol;

import app.utils.api.SynchronizedCounter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientTask implements Runnable {
    private Socket clientSocket;
    private Collection<String> uniquesSet;
    private SynchronizedCounter uniqueCount;
    private SynchronizedCounter repeatedCount;
    private ConcurrentLinkedQueue<String> loggerQueue;
    private BlockingQueue terminateQueue;

    public ClientTask(Socket socket, Collection<String> uniquesSet, SynchronizedCounter uniqueCount,
                      SynchronizedCounter repeatedCount, ConcurrentLinkedQueue<String> loggerQueue,
                      BlockingQueue terminateQueue
    ) {
        this.clientSocket = socket;
        this.uniquesSet = uniquesSet;
        this.uniqueCount = uniqueCount;
        this.repeatedCount = repeatedCount;
        this.loggerQueue = loggerQueue;
        this.terminateQueue = terminateQueue;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("terminate")) {
                    throw new TerminateException();
                }

                if (!validateData(inputLine)) {
                    break;
                }

                if (uniquesSet.add(inputLine)) {
                    uniqueCount.increment();
                    loggerQueue.offer(inputLine);
                } else {
                    repeatedCount.increment();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TerminateException e) {
            try {
                /*When the "terminal" signal is received, the terminal queue is filled to notify the terminal thread to
                * start the shutdown process */
                terminateQueue.put(Boolean.TRUE);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateData(String input) {
        return input.matches("\\d{9}");
    }
}
