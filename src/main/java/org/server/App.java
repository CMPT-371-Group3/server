package org.server;


public class App {
    public static void main( String[] args ) {
        System.out.println( "Server Started" );
        // Set Server Port Number
        int portNumber = 6000;
        // Create a Server and Listen for Connections
        Server server = new Server(portNumber);
        server.listenServer();
    }
}
