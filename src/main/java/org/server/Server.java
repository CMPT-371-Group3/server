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
        // Create an ArrayList for Clients and save the Port Number then create a ServerSocket
        try {
            this.PortNumber = portNumber;
            this.Clients = new ArrayList<>();
            this.Server = new ServerSocket(portNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addClient(Client client) {
        // Add a client to the ArrayList if we are not full, otherwise disconnect them
        if(this.Clients.size() < 4) {
            this.Clients.add(client);
            client.sendMessage(Tokens.JOIN.name() + "; Welcome to the server;");
        } else {
            client.sendMessage(Tokens.EXIT.name() + "; The server is full;");
            client.disconnect();
        }
    }

    // Disconnect a specific client
    public Status disconnectClient(String ipAddress) {
        try {
            for (Client client : this.Clients)
                if (client.getIpAddress().equals(ipAddress))
                    return client.disconnect();
            return Status.INVALID_ARGUMENT;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return Status.FAILURE;
        }
    }

    public ArrayList<Client> getClients() {
        // Return clients
        return this.Clients;
    }

    public ServerSocket getServerSocket() {
        // Return serverSocket
        return null;
    }

    public Socket listenServer() {
        try {
            // Accept Connections
            return this.Server.accept();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public String listenForMessage(){
        try {
            // Listen for messages and then return it if there was any other let the calling function know
            for(Client client: this.Clients) return client.listenForMessage();
            throw new SocketException("There was no messages to return");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public void handleThread() {
        // The thread has been timed out, if there is not enough players, let them know,
        // otherwise start the game, and if there is more than 4, then kick out the other players
        if(this.Clients.size() == 1) {
            this.Clients.get(0).sendMessage(Tokens.STOP.name() + "; Insufficient players have joined the match;");
            Status status = this.Clients.get(0).disconnect();
            switch (status) {
                case FAILURE -> System.out.println("Failure in disconnecting");
                case SUCCESS -> System.out.println("Success in disconnecting");
                default -> System.out.println("Unknown Status");
            }
        } else if (this.Clients.size() < 5) {
            this.Clients.forEach(client -> client.sendMessage(Tokens.START.name() + "; Beginning Game;"));
        } else {
            for(int i = 0; i < 4; i++) {
                this.Clients.get(0).sendMessage(Tokens.START.name() + "; Beginning Game");
            }
            for(int i = 4; i < this.Clients.size(); i++) {
                this.Clients.get(i).sendMessage(Tokens.STOP.name() + "; Too Many Players. Disconnecting;");
                Status status = this.Clients.get(i).disconnect();
                switch (status) {
                    case FAILURE -> System.out.println("Failure in disconnecting");
                    case SUCCESS -> System.out.println("Success in disconnecting");
                    default -> System.out.println("Unknown Status");
                }
            }
        }
    }
}
