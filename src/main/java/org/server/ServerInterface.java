package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<Client> getClients();
    ServerSocket getServerSocket();
    void addClient(Client client);
    Status disconnectClient(String ipAddress);
    String listenForMessage();
    Socket listenServer();
    void handleThread();
}
