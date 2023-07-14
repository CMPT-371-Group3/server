package org.server;

import java.net.Socket;

public interface ClientInterface {
    String GetIpAddress();
    int GetPortNumber();
    String ListenForMessage();
    Socket GetSocket();
}