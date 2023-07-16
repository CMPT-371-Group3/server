package org.server;

public class ConnectionManager implements ConnectionManagerInterface{
    private Server Server;
    private int RunningPortNumber;

    public ConnectionManager(int portNumber) {
        try {
            // Save the port number and then create a Server
            this.RunningPortNumber = portNumber;
            this.Server = new Server(portNumber);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listen() {
        // We create a new runnable and then put it on it's own thread
        ServerRunnable serverRunnable = new ServerRunnable(this.Server);
        Thread t = new Thread(serverRunnable);
        t.start();
    }
}
