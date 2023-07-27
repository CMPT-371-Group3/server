package org.server;

import java.util.concurrent.Semaphore;

public class BoardCell {
    //private Client lockedBy = null;
    private boolean locked = false;

    private Semaphore sema = new Semaphore(1);

    // if client has completed colouring
    private boolean isFilled = false;

    /*
    public Client getLockedBy() {
        return lockedBy;
    }

    public boolean setLockedBy(Client client) {
        // lock if not already locked
        if (lockedBy != null) {
            return false;
        }

        if (!sema.tryAcquire()) {
            return false;
        }

        lockedBy = client;
        return true;
    }
    */

    public boolean setLocked() {
        if (locked) {
            return false;
        }

        if (!sema.tryAcquire()) {
            return false;
        }

        locked = true;
        return true;
    }

    public boolean getLocked() {
        return locked;
    }

    public void unlock() {
        locked = false;
        sema.release();
    }

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled() {
        isFilled = true;
    }
}
