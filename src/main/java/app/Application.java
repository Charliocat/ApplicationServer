package app;

import app.server.ServerManager;

public class Application {
    public static void main(String[] args) {
        ServerManager manager = new ServerManager(4000);
        manager.startServer();
    }
}
