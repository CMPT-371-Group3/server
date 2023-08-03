package org.server;

import java.util.ArrayList;
import java.util.Arrays;

public class GameBoard {
    private int Rows;
    private int Cols;

    private ArrayList<ArrayList<BoardCell>> Board = new ArrayList<ArrayList<BoardCell>>();

    public GameBoard(int rows, int cols) {
        this.Rows = rows;
        this.Cols = cols;

        // add cells to the array list
        for (int i = 0; i < Rows; i++) {
            Board.add(new ArrayList<BoardCell>());
            for (int j = 0; j < Cols; j++) {
                Board.get(i).add(new BoardCell());
            }
        }
    }

    public boolean lockCell(int x, int y, ClientHandler c) {
        if (!checkBounds(x, y)) { return false; }
        return Board.get(x).get(y).setLocked(c);
    }

    public boolean unlockCell(int x, int y, ClientHandler c) {
        if (!checkBounds(x, y)) { return false; }

        return Board.get(x).get(y).unlock(c);
    }

    public boolean fillCell(int x, int y) {
        if (!checkBounds(x, y)) { return false; }

        Board.get(x).get(y).setIsFilled();

        return true;
    }

    public boolean isLocked(int x, int y) {
        if (!checkBounds(x, y)) { return false; }

        return Board.get(x).get(y).getLocked();
    }

    // Returns true if game has ended
    public boolean checkState(ArrayList<ClientHandler> clientHandlers) {
        int counter = 0;
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }
        for (int i = 0; i < Board.size(); i++) {
            for(int j = 0; j < Board.get(i).size(); j++) {
                if(Board.get(i).get(j).getIsFilled()) {
                    counter++;
                    clients[clientHandlers.indexOf(Board.get(i).get(j).getLockedBy())]++;
                }
            }
        }
        if(counter == (Rows * Cols)) {
            return true;
        }
        for (int i = 1; i < clients.length; i++){
            if(i >= ((Rows * Cols)/2 + 1)) {
                return true;
            }
        }
        return false;
    }

    public void getWinner(Server server, ArrayList<ClientHandler> clientHandlers) {
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }
        for (int i = 0; i < Board.size(); i++) {
            for(int j = 0; j < Board.get(i).size(); j++) {
                if(Board.get(i).get(j).getIsFilled()) {
                    clients[clientHandlers.indexOf(Board.get(i).get(j).getLockedBy())]++;
                }
            }
        }
        int max = 0;
        int maxIndex = -1;
        for(int i = 0; i < clientHandlers.size(); i++) {
            if(clients[i] > max) {
                max = clients[i];
                maxIndex = i;
            }
        }
        server.broadcastMessages(null, "STOP" + "\n" + "Client " + clientHandlers.get(maxIndex).toString() + " has won the game!");
    }

    private boolean checkBounds(int x, int y) {
        return (x >= 0 && x < Rows && y >= 0 && y < Cols);
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < Cols; i++) {
            for (int j = 0; j < Rows; j++) {
                str += Board.get(j).get(i).getIsFilled() ? "F" : isLocked(j, i) ? "L" : "0";
                str += " | ";
            }
            str += "\n";
            str+= "-------------------------------------\n";
        }
        return str;
    }
}
