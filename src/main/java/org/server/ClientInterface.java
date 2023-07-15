package org.server;

import java.net.Socket;

public interface ClientInterface {
    String getIpAddress();
    int getPortNumber();
    String listenForMessage();
    Socket GetSocket();
}