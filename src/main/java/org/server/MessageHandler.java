package org.server;

public class MessageHandler implements Runnable {
    private final String Message;

    public MessageHandler (String message) {
        this.Message = message;
    }

    @Override
    public void run() {
        // Do something
        System.out.println(this.Message);
    }
}
