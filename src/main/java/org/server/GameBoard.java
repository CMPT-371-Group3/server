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

    /**
     * This method locks a cell for a client
     * @param x
     * the x coordinate of the cell
     * @param y
     * the y coordinate of the cell
     * @param c
     * the client that is locking the cell
     * @return True if the cell was successfully locked, otherwise false
     */
    public boolean lockCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;
        return Board.get(x).get(y).setLocked(c);
    }

    /**
     * This method unlocks a cell for a client
     * @param x
     * the x coordinate of the cell
     * @param y
     * the y coordinate of the cell
     * @param c
     * the client that is unlocking the cell
     * @return 
     * True if the cell was successfully unlocked, otherwise false
     */
    public boolean unlockCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;

        return Board.get(x).get(y).unlock(c);
    }

    /**
     * This method fills a cell for a client
     * @param x
     * the x coordinate of the cell
     * @param y
     * the y coordinate of the cell
     * @param c
     * the client that is filling the cell
     * @return 
     * True if the cell was successfully filled, otherwise false
     */
    public boolean fillCell(int x, int y, ClientHandler c) {
        if (checkOutOfBounds(x, y)) return false;

        Board.get(x).get(y).setIsFilled(c);

        return true;
    }

    /**
     * This method checks if a cell is filled
     * @param x
     * the x coordinate of the cell
     * @param y
     * the y coordinate of the cell
     * @return 
     * True if the cell is filled, otherwise false
     */
    public boolean isLocked(int x, int y) {
        if (checkOutOfBounds(x, y)) return false;

        return Board.get(x).get(y).getLocked();
    }

    /**
     * This method checks the state of the game, and returns true if the game has reach an endgame condition
     * @param clientHandlers
     * The list of clients
     * @return 
     * True if the game is ended, otherwise false
     * 
     */
    public boolean checkEnded(ArrayList<ClientHandler> clientHandlers) {
        // Setup a counter and an array for all the clients
        int counter = 0;
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }

        // Check each cell, if it is filled, increment the counter and also check which client it is an increment that
        // clients index in the array (This is used to see if a client has already won a game, in which case we can end
        // it)
        for (ArrayList<BoardCell> boardCells : Board) {
            for (BoardCell boardCell : boardCells) {
                if (boardCell.getIsFilled()) {
                    counter++;
                    clients[clientHandlers.indexOf(boardCell.getLockedBy())]++;
                }
            }
        }

        // Return true if the counter is equal to the total number of cells
        if(counter == (Rows * Cols)) return true;

        // Return true if there is a client that has won the game
        for (int i = 1; i < clients.length; i++){
            if(clients[i] >= ((Rows * Cols)/2 + 1)) return true;
        }

        // Otherwise return false
        return false;
    }

    /**
     * This method gets the winner or winners and broadcasts it to all the clients
     * @param server
     * @param clientHandlers
     */
    public void getWinner(Server server, ArrayList<ClientHandler> clientHandlers) {
        // First declare an array that we use to check each client and setup an index for each client
        int clients[] = new int[4];
        for (int i = 0; i < clientHandlers.size(); i++) {
            clients[i] = 0;
        }

        // Run through the board and if the board cell is filled, get which client locked it and increment that
        // index in the array
        for (ArrayList<BoardCell> boardCells : Board) {
            for (BoardCell boardCell : boardCells) {
                if (boardCell.getIsFilled()) {
                    clients[clientHandlers.indexOf(boardCell.getLockedBy())]++;
                }
            }
        }

        // Declare variables to use for checking the winner
        int max = 0;
        int maxIndex = -1;
        int maxIndexTie = -1;
        int maxIndexTieTwo = -1;
        int maxIndexTieThree = -1;
        boolean isTie = false;
        boolean isTieTwo = false;
        boolean isTieThree = false;

        // Run through each client and compare their score with the maximum, we have 4 cases because we can have 1
        // winner or a 2, 3 or 4 way tie. For each type of tie we activate it by setting a boolean to true and
        // saving the client's index
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

        // Depending on what type of tie we have (or a single winner) broadcast a corresponding message
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

    /**
     * This method checks in the input coordinates are out of bounds
     * @param x
     * @param y
     * @return True if it is out of bounds; False if it is not
     */
    private boolean checkOutOfBounds(int x, int y) {
        return (x < 0 && x >= Rows && y < 0 && y >= Cols);
    }

    /**
     * This method returns the board as a string.
     * Used for debugging purposes.
     * @return
     */
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
