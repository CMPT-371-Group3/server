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

public class ClientHandler implements Runnable {
    private final Socket clientSocket; 
    private PrintWriter out;
    private BufferedReader in;
    private Scanner sc;
    private Client client;
    private boolean isReady = false;
    private Server server;
    
    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        try {
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.sc = new Scanner(System.in);
            this.server = server;
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
                System.out.println("Incoming: " + line);
                // parse line
                String[] tokens = line.split("/");
                switch (tokens[0]) {
                    case "JOIN":
                        server.broadcastMessages(this, "\n" + this.clientSocket.getInetAddress().getHostAddress() + ":" + this.clientSocket.getPort() + " has connected");
                        break;
                    case "LOCK": {
                        System.out.println("processing LOCK");
                        // tokens[1] in format x,y
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        System.out.println("coords: " + coords[0] + " " + coords[1]);
                        boolean returnval = server.lockCell(coords[0], coords[1]); 
                        System.out.println("has been locked? " + returnval);
                        //line = this.in.readLine();
                        break;
                    }
                    case "UNLOCK": {
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        server.unlockCell(coords[0], coords[1]);
                        break;
                    }
                    case "FILL": {
                        Integer[] coords = {Integer.parseInt(tokens[1].split(",")[0]), Integer.parseInt(tokens[1].split(",")[1])};
                        server.fillCell(coords[0], coords[1]);
                        break;
                    }
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
            server.broadcastMessages(this, clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has disconnected");
            System.out.println(clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " has disconnected");
        }

        
    }

    public boolean getIsReady() {
        return this.isReady;
    }
} 