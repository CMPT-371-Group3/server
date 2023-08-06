package org.server;


public class App {
    /**
     * Start the application
     * @param args
     */
    public static void main( String[] args ) {
        // Set Server Port Number
        int portNumber = 6000;
        // Create a Server and Listen for Connections
        Server server = new Server(portNumber);
        server.startServer();
    }
}
