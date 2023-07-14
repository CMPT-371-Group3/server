package org.server;

import java.io.*;
import java.net.Socket;

public class Client implements ClientInterface{
    private String IpAddress;
    private int PortNumber;
    private Socket Socket;
    private InputStream InputStream;
    private OutputStream OutputStream;
    private PrintWriter Output;
    private BufferedReader Input;
    public Client(String ipAddress, int portNumber, Socket socket) {
        try {
            this.IpAddress = ipAddress;
            this.PortNumber = portNumber;
            this.Socket = socket;
            this.InputStream = socket.getInputStream();
            this.OutputStream = socket.getOutputStream();
            this.Output = new PrintWriter(this.OutputStream, true);
            this.Input = new BufferedReader(new InputStreamReader(this.InputStream));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public String ListenForMessage() {
        try {
            return this.Input.readLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return  null;
        }
    }

    public String GetIpAddress() {
        return this.IpAddress;
    }

    public int GetPortNumber() {
        return this.PortNumber;
    }

    public java.net.Socket GetSocket() {
        return this.Socket;
    }

    public Status Disconnect() {
        try {
            this.Input.close();
            this.Output.close();
            this.Socket.close();
            return Status.SUCCESS;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return Status.FAILURE;
        }
    }
}
