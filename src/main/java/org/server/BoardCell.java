package org.server;

public class BoardCell {
    private Player lockedBy = Player.NONE;

    // if player has completed colouring
    private boolean isComplete = false;

    public Player getLockedBy() {
        return lockedBy;
    }

    public boolean setLockedBy(Player player) {
        // lock if not already locked
        if (lockedBy != Player.NONE) {
            return false;
        }

        lockedBy = player;
        return true;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setComplete() {
        isComplete = true;
    }
}
