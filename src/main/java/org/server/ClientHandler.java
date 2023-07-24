package org.server;

import java.util.concurrent.Callable;

public class ClientHandler implements Callable<String> {
    private final Client Client;

    public ClientHandler(Client client) {
        this.Client = client;
    }
    @Override
    public String call() throws Exception {
        try {
            return this.Client.listenForMessage();
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            throw e;
        }
    }
}
