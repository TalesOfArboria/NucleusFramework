package com.jcwhatever.bukkit.generic.utils;

/**
 * Used to provide internal batch execution functionality
 * within a class.
 */
public class BatchTracker {

    private int _batchOperations = 0;

    /**
     * Begin a batch operation.
     */
    public void start() {
        _batchOperations ++;
    }

    /**
     * End a batch operation.
     */
    public void end() {
        _batchOperations --;
    }

    /**
     * Determine if there is at least 1 batch operation running.
     */
    public boolean isRunning() {
        return _batchOperations > 0;
    }

    /**
     * Get the number of batch operations currently
     * running.
     */
    public int getBatchCount() {
        return _batchOperations;
    }
}
