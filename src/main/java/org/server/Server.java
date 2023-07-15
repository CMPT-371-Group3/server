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

    public void addClient(Client client) {
        this.Clients.add(client);
    }

    public Status disconnectClient(String ipAddress) {
        try {
            for (Client client : this.Clients)
                if (client.getIpAddress().equals(ipAddress))
                    return client.Disconnect();
            return Status.INVALID_ARGUMENT;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return Status.FAILURE;
        }
    }

    public ArrayList<Client> getClients() {
        return this.Clients;
    }

    public ServerSocket getServerSocket() {
        return null;
    }

    public Socket listenServer() {
        try {
            return this.Server.accept();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public String listenForMessage(){
        try {
            for(Client client: this.Clients) return client.listenForMessage();
            throw new SocketException("There was no messages to return");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}
