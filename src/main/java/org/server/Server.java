package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Server {
    private ArrayList<Client> Clients;
    private ServerSocket Server;
    private final long MILLISECONDS_IN_A_MINUTE = 60000;

    public Server(int portNumber) {
        // Create an ArrayList for Clients and save the Port Number then create a ServerSocket
        try {
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
    public Status disconnectClient(String ipAddress, int portNumber){
        try {
            for (Client client : this.Clients)
                if (client.getIpAddress().equals(ipAddress) && client.getPortNumber() == portNumber)
                    return client.disconnect();
            return Status.INVALID_ARGUMENT;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return Status.FAILURE;
        }
    }

    public void listenServer() {
        try {
            long startTime = System.currentTimeMillis();
            while(true) {
                // If we pass 1 minute, then timeout the thread and let the Server handle it
                if((System.currentTimeMillis() - startTime) >= this.MILLISECONDS_IN_A_MINUTE) {
                    this.timeoutConnections();
                    break;
                } else {
                    // Listen for connections and if there is, then send it to then accept the connection
                    Socket socket = this.Server.accept();
                    Client client = new Client(socket.getInetAddress().getHostAddress(), socket.getPort(), socket);
                    this.addClient(client);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String listenForMessage(){
        try {
            // Listen for messages and then return it if there was any other let the calling function know
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            ArrayList<FutureTask<String>> taskArrayList = new ArrayList<>();
            for(Client client: this.Clients) {
                ClientHandler clientHandler = new ClientHandler(client);
                FutureTask<String> futureTask = new FutureTask<>(clientHandler);
                executorService.submit(futureTask);
                taskArrayList.add(futureTask);
            }

            while (true)
                try {
                    for (FutureTask<String> stringFutureTask : taskArrayList) {
                        if (stringFutureTask.isDone()) {
                            handleMessage(stringFutureTask.get());
                            taskArrayList.remove(stringFutureTask);
                            // Figure out how to listen for another message from Client
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    private void handleMessage(String message) {
        // Do code
        MessageHandler messageHandler = new MessageHandler(message);
        Thread t = new Thread(messageHandler);
        t.start();
    }

    public void timeoutConnections() {
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
            this.listenForMessage();
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
            this.listenForMessage();
        }
    }
}