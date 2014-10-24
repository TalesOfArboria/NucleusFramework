package com.jcwhatever.bukkit.generic.events;

/**
 * The basis for all Generics events.
 */
public abstract class AbstractGenericsEvent {

    private boolean _isCancelled;

    /**
     * Determine if the event can be cancelled.
     */
    public abstract boolean isCancellable();

    /**
     * Determine if the event is cancelled.
     *
     * <p>Always returns false if the event is not cancellable.</p>
     */
    public boolean isCancelled() {
        return isCancellable() && _isCancelled;
    }

    /**
     * Set the cancelled state of the event.
     *
     * @param isCancelled  True to cancel the event.
     */
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }
}
