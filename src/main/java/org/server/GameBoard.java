package org.server;

import java.util.ArrayList;

public class GameBoard {
    private int rows;
    private int cols;

    private ArrayList<ArrayList<BoardCell>> board = new ArrayList<ArrayList<BoardCell>>();

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        // add cells to the array list
        for (int i = 0; i < ; i++) {
            board.add(new ArrayList<BoardCell>());
            for (int j = 0; j < boardY; j++) {
                board.get(i).add(new BoardCell());
            }
        }
    }

    public boolean lockCell(int x, int y, Client client) {
        // out of bounds
        if (x < 0 || x >= boardX || y < 0 || y >= boardY) {
            return false;
        }

        return board.get(x).get(y).setLockedBy(player);
    }

    public boolean lockCell(int x, int y, Client client) {
        // out of bounds
        if (x < 0 || x >= boardX || y < 0 || y >= boardY) {
            return false;
        }

        return board.get(x).get(y).setLockedBy(player);
    }
}
