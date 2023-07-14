package org.server;

import java.net.Socket;

public interface ClientInterface {

    String GetIpAddress();

    int GetPortNumber();
    public String ListenForMessage();
    Socket GetSocket();
}