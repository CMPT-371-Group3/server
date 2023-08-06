/**
 * This file is a Command Line Client Application that we used to test the Server
 * The game can be played using this, however the GUI is far better to play with anyway.
 */

package org.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private String addressWithPort;
    private Scanner sc;
    private static final Object lock = new Object();

    public static void main(String args[]) {
        try {
            Client client = new Client("0.0.0.0", 6000);
            String address = client.getInput().readLine();
            client.setAddressWithPort(address);
            String line = null;
            Scanner sc = new Scanner(System.in);

            // Separate thread to read messages from server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = client.getInput().readLine()) != null) {
                        String[] tokens = serverMessage.split("/");
                        switch (tokens[0]) {
                            case "EXIT":
                                client.disconnect();
                                break;
                            case "MESSAGE":
                                System.out.println("\nIncoming: MESSAGE");
                                String message = client.getInput().readLine();
                                System.out.print(message);
                                message = client.getMessage();
                                System.out.println("message: " + message + " length: " + message.length());
                                client.getOutput().println(message);
                                client.getOutput().flush();
                                System.out.println("" + client.getInput().readLine());
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                                break;
                            case "LOCK":

                            default:
                                if (serverMessage.length() > 0)
                                    System.out.println(client.getAddressWithPort() + ": " + serverMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            client.getOutput().println("JOIN");
            System.out.println(client.getAddressWithPort() + ": ");

            while (true) {
                System.out.println("Select an option:\nEXIT\nLOCK/row,col\nUNLOCK/row,col\nFILL/row,col");
                System.out.print("Selection: ");
                line = sc.nextLine();
                System.out.println("line: " + line);
                if (line.equalsIgnoreCase("EXIT")) {
                    client.getOutput().println("EXIT");
                    break;
                }
                String[] tokens = line.split("/");
                switch (tokens[0]) {
                    case "LOCK":
                    case "UNLOCK":
                    case "FILL":
                    case "READY":
                        System.out.println("Sending: " + line);
                        client.getOutput().println(line);
                        client.getOutput().flush();
                        break;
                    default:
                        System.out.println("Invalid selection");
                }
            }
            System.out.println("Stopped listening");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String ipAddress, int portNumber) {
        try {
            // Store any information needed then create the Socket, and the I/O
            this.socket = new Socket(ipAddress, portNumber);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            this.output = new PrintWriter(outputStream, true);
            this.input = new BufferedReader(new InputStreamReader(inputStream));
            this.addressWithPort = "";
            this.sc = new Scanner(System.in);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getMessage() {
        return sc.nextLine();
    }

    public void disconnect() {
        try {
            // Close the connection
            this.input.close();
            this.output.close();
            this.socket.close();
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

    public void setAddressWithPort(String address) {
        this.addressWithPort = address;
    }

    public String getAddressWithPort() {
        return this.addressWithPort;
    }
}