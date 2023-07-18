package org.server;

public class BoardCell {
    private Client lockedBy = null;

    // if client has completed colouring
    private boolean isComplete = false;

    public Client getLockedBy() {
        return lockedBy;
    }

    public boolean setLockedBy(Client client) {
        // lock if not already locked
        if (lockedBy != null) {
            return false;
        }

        lockedBy = client;
        return true;
    }

    public boolean getIsColoured() {
        return isComplete;
    }

    public void setColoured() {
        isComplete = true;
    }
}
