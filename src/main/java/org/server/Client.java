package org.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String ipAddress;
    private int portNumber;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;

    public static void main(String args[]) {
        Client client = new Client("localhost", 6000);
        String line = null;
        Scanner sc = new Scanner(System.in);
        System.out.print(client.getIpAddress() + ":" + client.getPortNumber() + ": ");
        line =  sc.nextLine();
        while (!line.equalsIgnoreCase("exit")) {
            client.getOutput().println(line);
            client.getOutput().flush();
            String response = null;
            try {
                response = client.getInput().readLine();            
            } catch(IOException e) {
                e.printStackTrace();
            }
            if (response != null) {
                System.out.println("Server: " + response);
            }
            line = "";
            System.out.print(client.getIpAddress() + ":" + client.getPortNumber() + ": " + line);
            line = sc.nextLine();
        }
        client.disconnect();
    }

    public Client(String ipAddress, int portNumber) {
        try {
            // Store any information needed then create the Socket, and the I/O
            this.ipAddress = ipAddress;
            this.portNumber = portNumber;
            this.socket = new Socket(ipAddress, portNumber);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            this.output = new PrintWriter(outputStream, true);
            this.input = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public String listenForMessage() {
        try {
            // Listen for inputs and then pass them up
            return this.input.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return  null;
        }
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public int getPortNumber() {
        return this.portNumber;
    }

    public Status disconnect() {
        try {
            // Close the connection
            this.input.close();
            this.output.close();
            this.socket.close();
            return Status.SUCCESS;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Status.FAILURE;
        }
    }

    public void sendMessage(String payload) {
        try {
            // Write to the client
            this.output.println(payload);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public PrintWriter getOutput() {
        return this.output;
    }

    public BufferedReader getInput() {
        return this.input;
    }

    // @Override
    // public void run() {
    //     try {
    //         // listen for messages and pass them to the server
    //         while (true) {
    //             String message = this.Input.readLine();
    //             this.Server.handleClientMessage(message, this);
    //         }
    //     } catch (Exception e) {
    //         System.out.println("Error: " + e.getMessage());
    //     }
    // }
}
