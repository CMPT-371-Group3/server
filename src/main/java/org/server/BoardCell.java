package org.server;

import java.util.concurrent.Semaphore;

public class BoardCell {
    //private Client lockedBy = null;
    private boolean locked = false;

    // Semaphore to lock the cell
    private Semaphore sema = new Semaphore(1);

    // if client has completed colouring
    private boolean isFilled = false;
    
    private ClientHandler lockedBy = null;

    /**
     * Get the client that locked the cell
     * @return
     */
    public ClientHandler getLockedBy() {
        return lockedBy;
    }

    /**
     * Set the client that locked the cell
     * @param c
     */
    public void setLockedBy(ClientHandler c) {
       this.lockedBy = c; 
    }
    
    /**
     * This method locks a cell for a client
     * @param c
     * the client that is locking the cell
     * @return True if the cell was successfully locked, otherwise false
     */
    public boolean setLocked(ClientHandler c) {
        // try to acquire the semaphore
        if (!sema.tryAcquire()) {
            return false;
        }
        // set the client that locked the cell if the semaphore was acquired
        setLockedBy(c);
        locked = true;
        return true;
    }

    /**
     * Check if the cell is locked
     * @return
     */
    public boolean getLocked() {
        return locked;
    }

    /**
     * This method unlocks a cell for a client
     * @param c
     * the client that is unlocking the cell
     * @return 
     * True if the cell was successfully unlocked, otherwise false
     */
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

    /**
     * Check if the cell is filled
     * @return
     * True if the cell is filled, otherwise false
     */
    public boolean getIsFilled() {
        return isFilled;
    }

    /**
     * Set the cell to filled
     * @param c
     * the client that is filling the cell
     */
    public void setIsFilled(ClientHandler c) {
        // Check to make sure the cell is locked and that the client filling the cell is the one
        // that locked it
        if(locked && lockedBy == c)
            isFilled = true;
    }
}
