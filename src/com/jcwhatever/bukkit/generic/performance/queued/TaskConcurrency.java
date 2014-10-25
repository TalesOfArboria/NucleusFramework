package com.jcwhatever.bukkit.generic.performance.queued;

/**
 * Specifies the preferred method of running a queue task.
 */
public enum TaskConcurrency {
    /**
     * The task should be run on the main thread.
     */
    MAIN_THREAD,

    /**
     * The task should be run on the current thread.
     */
    CURRENT_THREAD,

    /**
     * The task should be run on a new asynchronous thread.
     */
    ASYNC
}
