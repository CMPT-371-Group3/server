package org.server;

public class Main {
    public static void main(String[] args) {
        // Set Server Port Number
        int portNumber = 5000;
        // Create a Server and Listen for Connections
        Server server = new Server(portNumber);
        server.listenServer();
    }
}