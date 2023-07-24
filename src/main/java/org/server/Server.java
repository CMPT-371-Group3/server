package org.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private ArrayList<ClientHandler> clients;
    private ServerSocket server;
    private final long MILLISECONDS_IN_A_MINUTE = 60000;
    private GameBoard gameBoard;
    private HashMap<String, String> map;

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
                    ClientHandler newClient = new ClientHandler(socket);
                    this.clients.add(newClient);
                    new Thread(newClient).start();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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


    
    public void handleClientMessage(String message, Client client) {
        // handle based on client token
        // if client token is draw x y, then apply those changes to the GameBoard
        // after handling, broadcast changes to all clients
    }
    
    public void broadcastMessages(ClientHandler sender, String message) {
        for (ClientHandler curr : clients) {
            if (curr.getClientSocket() != sender.getClientSocket()) {
                // System.out.println("Broadcasting " + message);
                curr.sendMessage(curr.getClientSocket().getInetAddress().getHostAddress() + ":" + curr.getClientSocket().getPort() + ": " + message);
            } else {
                curr.sendMessage("");
            }
        }
    }
    public class ClientHandler implements Runnable {
        private final Socket clientSocket; 
        private PrintWriter out;
        private BufferedReader in;
        private Scanner sc;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                this.out = new PrintWriter(clientSocket.getOutputStream(), true);
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.sc = new Scanner(System.in);
            } catch (IOException e) {
                e.printStackTrace();
                this.out = null;
                this.in = null;
                this.sc = null;
            }
        }
        
        public Socket getClientSocket() {
            return this.clientSocket;
        }
        

        public void sendMessage(String message) {
            System.out.println(this.getClientSocket().getPort() + " " + message);
            // this.out.println("BROADCAST");
            this.out.println(message);
        }

        public void run() {
            try {
                System.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has connected");
                this.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                String line = this.in.readLine();
                System.out.print(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " selected option: " + line);                
                while (line != null && !line.equalsIgnoreCase("exit")) {
                    switch (line) {
                        case "JOIN":
                            broadcastMessages(this, "\n" + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + " has connected");
                            break;
                        case "MESSAGE":
                            System.out.println("Incoming: " + line);
                            this.out.println("MESSAGE");
                            this.out.println("Message: ");
                            line = this.in.readLine();
                            // System.out.println("Message: " + ("\n" + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + ": " + line));
                            broadcastMessages(this, line);
                            break;
                        case "EXIT":
                            System.out.println("EXIT");
                            this.out.println("EXIT");
                            break;
                        default:
                    }
                    System.out.print(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " selected option: ");
                    line = in.readLine();
                    System.out.println(line);
                    System.out.println();
                }
            } catch(IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }                
                broadcastMessages(this, clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has disconnected");
                System.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has disconnected");
            }
        }
    }

}
