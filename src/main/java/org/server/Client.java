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
    private String addressWithPort;
    private Scanner sc;
    private Boolean msgSent;
    private static final Object lock = new Object();
    public static void main(String args[]) {
        try {
            Client client = new Client("34.67.92.136", 6000);
            String address = client.getInput().readLine();
            client.setAddressWithPort(address);
            String line = null;
            Scanner sc = new Scanner(System.in);

            // Separate thread to read messages from server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while((serverMessage = client.getInput().readLine()) != null) {
                        String[] tokens = serverMessage.split("/");
                        switch (tokens[0]) {
                            case "EXIT":
                                return;
                            case "LOCK":
                            case "UNLOCK":
                            case "MESSAGE":
                                System.out.println("\nIncoming: MESSAGE");
                                String message = client.getInput().readLine();
                                System.out.print(message);
                                // Scanner scanner = new Scanner(System.in);
                                message = client.getMessage();
                                System.out.println("message: " + message + " length: " + message.length());
                                client.getOutput().println(message);
                                client.getOutput().flush();
                                System.out.println("" + client.getInput().readLine());
                                /*
                                synchronized(lock) {
                                    client.setMsgSent(true);
                                    lock.notifyAll();
                                }
                                */
                                break;
                                
                            default:
                                if (serverMessage.length() > 0)
                                System.out.println(client.getAddressWithPort() + ": " + serverMessage);
                        }
                    }   
                } catch(IOException e) {
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
                if (line.equalsIgnoreCase("EXIT")) { break; }
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
                // synchronized (lock) {
                //     try {
                //         lock.wait(); // Wait for notification from the other thread
                //     } catch (InterruptedException e) {
                //         e.printStackTrace();
                //     }
                // }
                //client.setMsgSent(false);           
            }
            System.out.println("Stopped listening");
            // if (line.equalsIgnoreCase("exit")) {
            //     client.getOutput().println("EXIT");
            //     client.getOutput().flush();                    
            // } 
            //client.disconnect();
            /*
            */
        } catch(IOException e) {
            e.printStackTrace();
        }
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
            this.addressWithPort = "";
            this.sc = new Scanner(System.in);
            this.msgSent = false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public String getMessage() {
        return sc.nextLine();
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

    public void setAddressWithPort(String address) {
        this.addressWithPort = address;
    }

    public String getAddressWithPort() {
        return this.addressWithPort;
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

    public void setMsgSent(Boolean b) {
        this.msgSent = b;
    }

    public Boolean getMsgSent() {
        return this.msgSent;
    }

    public class ClientThread implements Runnable {

        private Client client;

        public ClientThread(Client c) {
            this.client = c;
        }

        public void run() {
            try {
                String serverMessage;
                while((serverMessage = client.getInput().readLine()) != null) {
                    switch (serverMessage) {
                        case "EXIT":
                            return;
                        case "MESSAGE":
                            System.out.println("\nIncoming: MESSAGE");
                            String message = client.getInput().readLine();
                            System.out.print(message);
                            // Scanner scanner = new Scanner(System.in);
                            message = sc.nextLine();
                            System.out.println("message: " + message + " length: " + message.length());
                            client.getOutput().println(message);
                            client.getOutput().flush();
                            break;
                        default:
                    }
                    System.out.println("\nSelect an option:\nEXIT\nMESSAGE");
                    System.out.print("Selection: ");
                }   
            } catch(IOException e) {
                e.printStackTrace();
            }            
        }
    }
}
