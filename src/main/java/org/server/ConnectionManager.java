package org.server;

import java.net.*;

public class ConnectionManager {
    private Server Server;
    private int RunningPortNumber;

    public ConnectionManager(int portNumber) {
        try {
            this.RunningPortNumber = portNumber;
            this.Server = new Server(portNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void Listen() {
        while(true) {
            Socket socket = this.Server.listenServer();
            String ipAddress = socket.getInetAddress().getHostAddress();
            int portNumber = socket.getPort();
            Client client = new Client(ipAddress, portNumber, socket);
            this.Server.addClient(client);
        }
    }
}
