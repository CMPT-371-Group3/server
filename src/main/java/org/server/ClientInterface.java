package org.server;

import java.net.Socket;

public interface ClientInterface {
    String getIpAddress();
    int getPortNumber();
    Status disconnect();
    String listenForMessage();
    Socket getSocket();
    void sendMessage(String payload);
}