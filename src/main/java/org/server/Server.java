package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    private volatile ArrayList<ClientHandler> clients;
    private ServerSocket server;
    private final long MILLISECONDS_IN_A_MINUTE = 60000;
    private volatile GameBoard gameBoard;
    private HashMap<String, String> map;
    private boolean gameStarted = false;
    private int portNumber;

    public Server(int portNumber) {
        try {
            this.portNumber = portNumber;
            this.server = new ServerSocket(portNumber);
            // Create an ArrayList for Clients and save the Port Number then create a ServerSocket
            init();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void init() {
        this.gameBoard = new GameBoard(8, 8);
        this.clients = new ArrayList<>();
        startServer();
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
        System.out.println( "Server Started on IP " + this.server.getInetAddress().getHostAddress() + " and Port " + this.server.getLocalPort());
        listenForConnections();
        checkReady();
    }


    /*
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
    */

    private void listenForConnections() {
        new Thread(() -> {
            System.out.println("Listening for connections");
            try {
                while(!gameStarted && this.clients.size() < 4) {
                    Socket socket = this.server.accept();
                    ClientHandler newClient = null;
                    synchronized (clients) {
                        // overly complicated way to calculate what number hasnt been used lol
                        boolean[] playerNums = {false, false, false, false};
                        for (ClientHandler client : clients) {
                            // playerNums starts from 1
                            playerNums[client.getPlayerNumber() - 1] = true; 
                        }

                        for (int i = 0; i < playerNums.length; i++) {
                            if (!playerNums[i]) {
                                newClient = new ClientHandler(socket, this, i + 1);
                                break;
                            }
                        }

                        clients.add(newClient);
                    }
                    new Thread(newClient).start();
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }).start();
    }

    private void checkReady() { 
        new Thread(() -> {
            int clientsCount = 0;
            int clientsReady = 0;
            System.out.println("Waiting for clients to be ready");
            while(!gameStarted) {
                // sleep for a second before checking again
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                synchronized (clients) {
                    // if a new client has joined
                    if (this.clients.size() > clientsCount) { 
                        clientsCount = this.clients.size();
                        System.out.println("New client has joined. There are now " + this.clients.size() + " clients");
                    } 
                    // if a client has left
                    else if (this.clients.size() < clientsCount) { 
                        clientsCount = this.clients.size();
                        System.out.println("A client has left. There are now " + this.clients.size() + " clients");
                    }

                    // count ready clients
                    int tempClientsReady = 0;
                    for (ClientHandler client : clients) {
                        if (client.getIsReady()) {
                            tempClientsReady++;
                        }
                    }
                    
                    // change in the number of clients that are ready
                    if (tempClientsReady != clientsReady) { 
                        System.out.println(tempClientsReady + " clients are ready out of " + this.clients.size());
                        clientsReady = tempClientsReady;
                    }
                    
                    // all clients are ready
                    if (this.clients.size() >= 2) {
                        if (clientsReady == this.clients.size()) { 
                            startGame();
                        }
                    } 
                    
                }
            }
        }).start();
    }

    private void startGame() {
        synchronized (clients) {
            System.out.println(clients.size() + " clients have joined and are all ready. Starting game");
        }
        
        gameStarted = true;
        broadcastMessages(null, "START");
        onBoardChange();
    }

    private void checkRestart() {
        if (clients.size() == 0) {
            init();
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
        
        System.out.println("Broadcasting \n" + message);
            // send to all clients
        for (ClientHandler curr : clients) {
            
            curr.sendMessage(message);
        }
    }

    public boolean lockCell(int row, int col, ClientHandler c) {
        if (!gameStarted) { return false; }
        boolean result = gameBoard.lockCell(row, col, c);
        if (result) { broadcastMessages(null, "LOCK/" + row + "," + col + "/" + c.getPlayerNumber()); }
        System.out.println("Locking cell " + row + ", " + col + " " + result);
        onBoardChange();
        return result;
    }

    public boolean unlockCell(int row, int col, ClientHandler c) {
        if (!gameStarted) { return false; }
        boolean result = gameBoard.unlockCell(row, col, c);
        if (result) { broadcastMessages(null, "UNLOCK/" + row + "," + col + "/" + c.getPlayerNumber()); }
        System.out.println("cell has been unlocked");
        onBoardChange();
        return result;
    }

    public boolean fillCell(int row, int col, ClientHandler c) {
        if (!gameStarted) { return false; }
        boolean result = gameBoard.fillCell(row, col);
        if (result) { broadcastMessages(null, "FILL/" + row + "," + col + "/" + c.getPlayerNumber()); }
        onBoardChange();
        boolean isFinished = gameBoard.checkState(clients);
        if(isFinished) {
            gameBoard.getWinner(this, clients);
        }
        return result; 
    }

    private void onBoardChange() {
        broadcastMessages(null, "BOARD");
        broadcastMessages(null, gameBoard.toString());
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            System.out.println("removed a client");
            checkRestart();
        }
    }
}
