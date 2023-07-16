package org.server;

public class Main {
    public static void main(String[] args) {
        // Set Server Port Number
        int portNumber = 5000;

        // Create a ConnectionManager and Listen for Connections
        ConnectionManager connectionManager = new ConnectionManager(portNumber);
        connectionManager.listen();
    }
}