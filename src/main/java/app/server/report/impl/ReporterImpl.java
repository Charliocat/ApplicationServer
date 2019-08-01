package app.server.report.impl;

import app.server.Server;
import app.server.report.api.Reporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReporterImpl implements Reporter {
    private Server server;

    public ReporterImpl(Server server) {
        this.server = server;
    }

    private void run() {
        String report = "Received " + server.getUniqueCount() + " unique numbers, " + server.getRepeatedCount() + " duplicated. " +
                "Unique Total: " + server.getUniquesSet().size();
        System.out.println(report);

        //Reset values to count new values for next 10 seconds
        server.resetUnique();
        server.resetRepeated();
    }

    public void startReporting(){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, runnable -> {
                    Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                    thread.setDaemon(true);
                    return thread;
                }
        );
        scheduledExecutorService.scheduleAtFixedRate(this::run, 10, 10, TimeUnit.SECONDS);
    }
}
