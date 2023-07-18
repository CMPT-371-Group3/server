package org.server;

import java.util.ArrayList;

public class GameBoard {
    private int Rows;
    private int Cols;

    private ArrayList<ArrayList<BoardCell>> Board = new ArrayList<ArrayList<BoardCell>>();

    public GameBoard(int rows, int cols) {
        this.Rows = rows;
        this.Cols = cols;

        // add cells to the array list
        for (int i = 0; i < rows; i++) {
            Board.add(new ArrayList<BoardCell>());
            for (int j = 0; j < cols; j++) {
                Board.get(i).add(new BoardCell());
            }
        }
    }

    public boolean lockCell(int x, int y, Client client) {
        // out of bounds
        if (x < 0 || x >= x || y < 0 || y >= y) {
            return false;
        }

        return Board.get(x).get(y).setLockedBy(client);
    }
}
