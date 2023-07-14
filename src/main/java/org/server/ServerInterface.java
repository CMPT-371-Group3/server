package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<Client> GetClients();
    ServerSocket GetServerSocket();
    void AddClient(Client client);
    Status DisconnectClient(String ipAddress);
    String ListenForMessage();
    Socket ListenServer();

}
