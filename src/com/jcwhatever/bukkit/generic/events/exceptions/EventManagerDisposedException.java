package com.jcwhatever.bukkit.generic.events.exceptions;

/**
 * Thrown when attempting to use a {@code GenericsEventManager} that is disposed.
 */
public class EventManagerDisposedException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Cannot use an event manager after it is disposed.";
    }
}
