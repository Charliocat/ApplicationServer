package app.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MultiClient {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: java MultiClient <host name> <port number>");
            System.exit(1);
        }

        Runnable task = () -> {
            System.out.println("Task: " + Thread.currentThread().getName());
            String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            long initTime = System.currentTimeMillis();
            try (Socket echoSocket = new Socket(hostName, portNumber);
                 PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);)
            {
                while (System.currentTimeMillis() < (initTime + TimeUnit.SECONDS.toMillis(Integer.parseInt(args[2])))) {
                    int number = new Random().nextInt(999999999);
                    String userInput = String.format("%09d", number);
                    out.println(userInput);
                }
                // }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + hostName);
                System.exit(1);
            } finally {
                System.out.println("End task: " + Thread.currentThread().getName());
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(10);
        IntStream.range(0, 5).forEach(i -> service.execute(task));
        service.shutdown();
    }
}
