package org.server;

import java.io.*;
import java.net.Socket;

public class Client extends Thread {
    private String IpAddress;
    private int PortNumber;
    private Socket Socket;
    private PrintWriter Output;
    private BufferedReader Input;
    private Server Server;

    public Client(String ipAddress, int portNumber, Socket socket, Server server) {
        try {
            // Store any information needed then create the Socket, and the I/O
            this.IpAddress = ipAddress;
            this.PortNumber = portNumber;
            this.Socket = socket;
            this.Server = server;
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            this.Output = new PrintWriter(outputStream, true);
            this.Input = new BufferedReader(new InputStreamReader(inputStream));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public String listenForMessage() {
        try {
            // Listen for inputs and then pass them up
            return this.Input.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return  null;
        }
    }

    public String getIpAddress() {
        return this.IpAddress;
    }

    public int getPortNumber() {
        return this.PortNumber;
    }

    public Status disconnect() {
        try {
            // Close the connection
            this.Input.close();
            this.Output.close();
            this.Socket.close();
            return Status.SUCCESS;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Status.FAILURE;
        }
    }

    public void sendMessage(String payload) {
        try {
            // Write to the client
            this.Output.println(payload);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // listen for messages and pass them to the server
            while (true) {
                String message = this.Input.readLine();
                this.Server.handleClientMessage(message, this);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
