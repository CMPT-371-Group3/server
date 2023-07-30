package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private volatile ArrayList<ClientHandler> clients;
    private ServerSocket server;
    private final long MILLISECONDS_IN_A_MINUTE = 60000;
    private GameBoard gameBoard;
    private HashMap<String, String> map;
    private boolean gameStarted = false;

    public Server(int portNumber) {
        // Create an ArrayList for Clients and save the Port Number then create a ServerSocket
        try {
            this.clients = new ArrayList<>();
            this.server = new ServerSocket(portNumber);
            this.gameBoard = new GameBoard(8, 8);
            map = new HashMap<String, String>();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /* 
    public void addClient(ClientHandler client) {
        // Add a client to the ArrayList if we are not full, otherwise disconnect them
        if(this.clients.size() < 4) {
            this.clients.add(client);
            client.sendMessage(Tokens.JOIN.name() + "; Welcome to the server;");
        } else {
            client.sendMessage(Tokens.EXIT.name() + "; The server is full;");
            client.disconnect();
        }
    }

    Disconnect a specific client
    public Status disconnectClient(String ipAddress, int portNumber){
        try {
            for (Client client : this.clients)
                if (client.getIpAddress().equals(ipAddress) && client.getPortNumber() == portNumber)
                    return client.disconnect();
            return Status.INVALID_ARGUMENT;
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
            return Status.FAILURE;
        }
    }
    */

    public void startServer() {
        listenForConnections();
        checkReady();
    }


    public void listenServer() {
        try {
            long startTime = System.currentTimeMillis();
            while(true) {
                // If we pass 1 minute, then timeout the thread and let the Server handle it
                if((System.currentTimeMillis() - startTime) >= this.MILLISECONDS_IN_A_MINUTE) {
                    // this.timeoutConnections();
                    // break;
                } else {
                    // Listen for connections and if there is, then send it to then accept the connection
                    // System.out.println("server " + this.Server.toString());
                    Socket socket = this.server.accept();
                    // Client client = new Client(socket.getInetAddress().getHostAddress(), socket.getPort(), socket, this);
                    // this.addClient(client);
                    ClientHandler newClient = new ClientHandler(socket, this);
                    this.clients.add(newClient);
                    new Thread(newClient).start();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listenForConnections() {
        new Thread(() -> {
            try {
                while(!gameStarted) {
                    Socket socket = this.server.accept();
                    ClientHandler newClient = new ClientHandler(socket, this);
                    this.clients.add(newClient);
                    new Thread(newClient).start();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }).start();
    }

    private void checkReady() { 
        while(!gameStarted) {
            if (this.clients.size() >= 2) {
                boolean allReady = true;
                for (ClientHandler client : clients) {
                    if (!client.getIsReady()) {
                        allReady = false;
                        break;
                    }
                }
                if (allReady) {
                    // start game
                    this.gameStarted = true;
                }
            }
        }
    }
    

    /*
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
        if(this.clients.size() == 1) {
            this.clients.get(0).sendMessage(Tokens.STOP.name() + "; Insufficient players have joined the match;");
            Status status = this.clients.get(0).disconnect();
            switch (status) {
                case FAILURE -> System.out.println("Failure in disconnecting");
                case SUCCESS -> System.out.println("Success in disconnecting");
                default -> System.out.println("Unknown Status");
            }
        } else if (this.clients.size() < 5) {
            this.clients.forEach(client -> client.sendMessage(Tokens.START.name() + "; Beginning Game;"));
            this.startGame();
        } else {
            for(int i = 0; i < 4; i++) {
                this.clients.get(0).sendMessage(Tokens.START.name() + "; Beginning Game");
            }
            for(int i = 4; i < this.clients.size(); i++) {
                this.clients.get(i).sendMessage(Tokens.STOP.name() + "; Too Many Players. Disconnecting;");
                Status status = this.clients.get(i).disconnect();
                switch (status) {
                    case FAILURE -> System.out.println("Failure in disconnecting");
                    case SUCCESS -> System.out.println("Success in disconnecting");
                    default -> System.out.println("Unknown Status");
                }
            }
            this.startGame();
        }
    }

    public void startGame() {
        try {
            // start all client threads
            for (Client client : this.clients) {
                // client.start();
            }
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    */


    
    public void handleClientMessage(String message, ClientHandler client) {
        // handle based on client token
        // if client token is draw x y, then apply those changes to the GameBoard
        // after handling, broadcast changes to all clients
    }
    
    public void broadcastMessages(ClientHandler sender, String message) {
        // for (ClientHandler curr : clients) {
        //     if (curr.getClientSocket() != sender.getClientSocket()) {
        //         // System.out.println("Broadcasting " + message);
        //         curr.sendMessage(curr.getClientSocket().getInetAddress().getHostAddress() + ":" + curr.getClientSocket().getPort() + ": " + message);
        //     } else {
        //         curr.sendMessage("");
        //     }
        // }

        // send to all clients
        for (ClientHandler curr : clients) {
            System.out.println("Broadcasting \n" + message);
            curr.sendMessage(message);
        }
    }

    public boolean lockCell(int row, int col, ClientHandler c) {
        boolean result = gameBoard.lockCell(row, col, c);
        System.out.println("cell has been locked");
        broadcastMessages(null, gameBoard.toString());
        return result;
    }

    public boolean unlockCell(int row, int col, ClientHandler c) {
        boolean result = gameBoard.unlockCell(row, col, c);
        System.out.println("cell has been unlocked");
        broadcastMessages(null, gameBoard.toString());
        return result;
    }

    public boolean fillCell(int row, int col) {
        boolean result = gameBoard.fillCell(row, col);
        broadcastMessages(null, gameBoard.toString());
        return result;
    }
}
