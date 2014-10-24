package com.jcwhatever.bukkit.generic.events;

/**
 * Defines the order an event handler
 * executed.
 */
public enum GenericsEventPriority {

    /**
     * The last handler to be called.
     */
    LAST    (5),

    /**
     * Low priority. Called second to last.
     */
    LOW     (4),

    /**
     * Normal priority.
     */
    NORMAL  (3),

    /**
     * High priority. Called second.
     */
    HIGH    (2),

    /**
     * Highest priority. Called first.
     */
    FIRST   (1),

    /**
     * Watcher. Only watches to see if the event
     * is called but does not effect the outcome
     * of the event. Is always called even if the
     * event is cancelled.
     */
    WATCHER (0);

    private final int _order;

    GenericsEventPriority(int order) {
        _order = order;
    }

    /**
     * Get a sort order index number.
     */
    public int getSortOrder() {
        return _order;
    }
}
