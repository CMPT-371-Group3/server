package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public interface ServerInterface {
    ArrayList<Client> GetClients();
    ServerSocket GetServerSocket();
    public void AddClient(Client client);
    public Status DisconnectClient(String ipAddress);
    public String ListenForMessage();
    public Socket ListenServer();

}
