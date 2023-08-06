package org.server;

import java.util.concurrent.Semaphore;

public class BoardCell {
    //private Client lockedBy = null;
    private boolean locked = false;

    private Semaphore sema = new Semaphore(1);

    // if client has completed colouring
    private boolean isFilled = false;
    
    private ClientHandler lockedBy = null;

    public ClientHandler getLockedBy() {
        return lockedBy;
    }

    public void setLockedBy(ClientHandler c) {
       this.lockedBy = c; 
    }
    
    public boolean setLocked(ClientHandler c) {

        if (!sema.tryAcquire()) {
            return false;
        }
        setLockedBy(c);
        locked = true;
        return true;
    }

    public boolean getLocked() {
        return locked;
    }

    public boolean unlock(ClientHandler c) {
        System.out.println(c + " trying to unlock");
        if (c == lockedBy) {
            setLockedBy(null);
            locked = false;
            sema.release();
            return true;
        }
        return false;
    }

    public boolean getIsFilled() {
        return isFilled;
    }

    public void setIsFilled(ClientHandler c) {
        if(locked && lockedBy == c)
            isFilled = true;
    }
}
