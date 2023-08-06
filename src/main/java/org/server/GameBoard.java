package org.server;

import java.util.ArrayList;

public class GameBoard {
    private final int Rows;
    private final int Cols;

    private final ArrayList<ArrayList<BoardCell>> Board = new ArrayList<>();

    public GameBoard(int rows, int cols) {
        this.Rows = rows;
        this.Cols = cols;

        // add cells to the array list
        for (int i = 0; i < Rows; i++) {
            Board.add(new ArrayList<>());
            for (int j = 0; j < Cols; j++) {
                Board.get(i).add(new BoardCell());
            }
        }
    }

    public boolean lockCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;
        return Board.get(x).get(y).setLocked(c);
    }

    public boolean unlockCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;

        return Board.get(x).get(y).unlock(c);
    }

    public boolean fillCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;

        Board.get(x).get(y).setIsFilled(c);

        return true;
    }

    public boolean isLocked(int x, int y) {
        if (checkOutOfBounds(x, y)) return false;

        return Board.get(x).get(y).getLocked();
    }

    // Returns true if game has ended
    public boolean checkState(ArrayList<ClientHandler> clientHandlers) {
        int counter = 0;
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }
        for (ArrayList<BoardCell> boardCells : Board) {
            for (BoardCell boardCell : boardCells) {
                if (boardCell.getIsFilled()) {
                    counter++;
                    clients[clientHandlers.indexOf(boardCell.getLockedBy())]++;
                }
            }
        }

        if(counter == (Rows * Cols)) return true;

        for (int i = 1; i < clients.length; i++){
            if(clients[i] >= ((Rows * Cols)/2 + 1)) return true;
        }
        return false;
    }

    public void getWinner(Server server, ArrayList<ClientHandler> clientHandlers) {
        server.endGame();
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }
        for (ArrayList<BoardCell> boardCells : Board) {
            for (BoardCell boardCell : boardCells) {
                if (boardCell.getIsFilled()) {
                    clients[clientHandlers.indexOf(boardCell.getLockedBy())]++;
                }
            }
        }
        int max = 0;
        int maxIndex = -1;
        int maxIndexTie = -1;
        int maxIndexTieTwo = -1;
        int maxIndexTieThree = -1;
        boolean isTie = false;
        boolean isTieTwo = false;
        boolean isTieThree = false;
        for(int i = 0; i < clientHandlers.size(); i++) {
            if(clients[i] > max) {
                max = clients[i];
                maxIndex = i;
            } else if (!isTie && clients[i] == max) {
                isTie = true;
                maxIndexTie = i;
            } else if (!isTieTwo && clients[i] == max) {
                isTieTwo = true;
                maxIndexTieTwo = i;
            } else if (!isTieThree && clients[i] == max) {
                isTieThree = true;
                maxIndexTieThree = i;
            }

        }
        if(isTieThree)
            server.broadcastMessages("STOP/" + clientHandlers.get(maxIndex).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTie).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTieTwo).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTieThree).getPlayerNumber());
        else if (isTieTwo)
            server.broadcastMessages("STOP/" + clientHandlers.get(maxIndex).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTie).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTieTwo).getPlayerNumber());
        else if (isTie)
            server.broadcastMessages("STOP/" + clientHandlers.get(maxIndex).getPlayerNumber() + ","
                    + clientHandlers.get(maxIndexTie).getPlayerNumber());
        else
            server.broadcastMessages("STOP/" + clientHandlers.get(maxIndex).getPlayerNumber());
    }

    private boolean checkOutOfBounds(int x, int y) {
        return (x < 0 && x >= Rows && y < 0 && y >= Cols);
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
