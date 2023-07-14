package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements ServerInterface{
    ArrayList<Client> Clients;
    int PortNumber;
    ServerSocket Server;
    public Server(int portNumber) {
        this.Clients = new ArrayList<>();
        this.PortNumber = portNumber;
        try {
            this.Server = new ServerSocket(portNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void AddClient(Client client) {
        this.Clients.add(client);
    }

    public Status DisconnectClient(String ipAddress) {
        try {
            for (Client client : this.Clients) if (client.GetIpAddress().equals(ipAddress))  return client.Disconnect();
            return Status.INVALID_ARGUMENT;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return Status.FAILURE;
        }
    }

    public ArrayList<Client> GetClients() {
        return this.Clients;
    }

    public ServerSocket GetServerSocket() {
        return null;
    }

    public Socket ListenServer() {
        try {
            return this.Server.accept();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public String ListenForMessage(){
        try {
            for(Client client: this.Clients) return client.ListenForMessage();
            throw new SocketException("There was no messages to return");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
