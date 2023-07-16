package org.server;

import java.net.Socket;

public class ServerRunnable implements Runnable {
    private final Server Server;
    private final long StartTime;
    private final long MILLISECONDS_IN_A_MINUTE = 60000;

    public ServerRunnable(Server server) {
        this.StartTime = System.currentTimeMillis();
        this.Server = server;
    }

    @Override
    public void run() {
        while(true) {
            // If we pass 1 minute, then timeout the thread and let the Server handle it
            if(System.currentTimeMillis() - this.StartTime >= this.MILLISECONDS_IN_A_MINUTE) {
                this.Server.handleThread();
                break;
            } else {
                // Listen for connections and if there is, then send it to the Server
                Socket socket = this.Server.listenServer();
                String ipAddress = socket.getInetAddress().getHostAddress();
                int portNumber = socket.getPort();
                Client client = new Client(ipAddress, portNumber, socket);
                this.Server.addClient(client);
            }
        }
    }
}
