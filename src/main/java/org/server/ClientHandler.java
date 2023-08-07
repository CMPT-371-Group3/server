package org.server;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientHandler implements Runnable {
    private final Socket clientSocket; 
    private PrintWriter out;
    private BufferedReader in;
    private boolean isReady = false;
    private Server server;
    private int playerNumber; // 1-4
    
    public ClientHandler(Socket socket, Server server, int playerNumber) {
        this.clientSocket = socket;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.server = server;
            this.playerNumber = playerNumber;
            sendMessage("PLAYER_NUMBER/" + playerNumber);
            
        } catch (IOException e) { 
            e.printStackTrace();
            this.out = null;
            this.in = null;
        }
    }
    
    /**
     * Sends a message to the client
     * @param message
     * the message to send
     */
    public void sendMessage(String message) {
        this.out.println(message);
    }

    /**
     * Disconnects the client from the server
     */
    public void disconnect() {
        try {
            // Close the input, output and the socket connection
            this.in.close();
            this.out.close();
            this.clientSocket.close();
            System.out.println("Disconnected player " + playerNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * This method runs when the thread is started.
     * It listens for messages from the client and handles them by
     * calling the appropriate methods in the server.
     * It also handles the client disconnecting.
     */
    public void run() {
        try {
            this.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
            String line = this.in.readLine(); // read the first line
            while (line != null && !line.equalsIgnoreCase("exit")) {
                // parse line
                String[] tokens = line.split("/"); // split by "/"
                switch (tokens[0]) {
                    case "JOIN":
                        server.broadcastMessages("\n" + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + " has connected");
                        break;
                    case "LOCK": {
                        // tokens[1] in format x,y
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        server.lockCell(coords[0], coords[1], this);
                        break;
                    }
                    case "UNLOCK": {
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        server.unlockCell(coords[0], coords[1], this);
                        break;
                    }
                    case "FILL": {
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        server.fillCell(coords[0], coords[1], this);
                        break;
                    }
                    case "EXIT":
                        this.server.broadcastMessages("Player: " + playerNumber + " has disconnected!");
                        this.out.println("EXIT");
                        this.disconnect();
                        server.removeClient(this);
                        break;
                    case "READY":
                        this.isReady = true;
                        break;
                    default:
                }
                line = in.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally { // if the client disconnects, remove it from the server
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
            server.broadcastMessages(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has disconnected");
            server.removeClient(this);
        }        
    }

    /**
     * Get the ready status of the client
     * @return
     * the ready status of the client
     */
    public boolean getIsReady() {
        return this.isReady;
    }

    /**
     * Get the player number of the client
     * @return
     * the player number of the client
     */
    public int getPlayerNumber() {
        return this.playerNumber;
    }
} 