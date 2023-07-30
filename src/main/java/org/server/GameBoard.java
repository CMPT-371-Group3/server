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
