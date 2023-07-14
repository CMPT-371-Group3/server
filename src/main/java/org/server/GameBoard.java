package org.server;

import java.util.ArrayList;

public class GameBoard {
    private int boardX = 8;
    private int boardY = 8;

    private ArrayList<ArrayList<BoardCell>> board = new ArrayList<ArrayList<BoardCell>>();

    public GameBoard() {

        // add cells to the array list
        for (int i = 0; i < boardX; i++) {
            board.add(new ArrayList<BoardCell>());
            for (int j = 0; j < boardY; j++) {
                board.get(i).add(new BoardCell());
            }
        }
    }

    public boolean takeCell(int x, int y, Player player) {
        if (x < 0 || x >= boardX || y < 0 || y >= boardY) {
            return false;
        }

        board.get(x).get(y).setLockedBy(player);
        return true;
    }
}
