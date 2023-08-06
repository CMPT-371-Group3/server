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

    public void startServer() {
        System.out.println( "Server Started on IP " + this.server.getInetAddress().getHostAddress() + " and Port " + this.server.getLocalPort());
        listenForConnections();
        checkReady();
    }

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
    
    public void broadcastMessages(ClientHandler sender, String message) {
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

    public synchronized boolean fillCell(int row, int col, ClientHandler c) {
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
