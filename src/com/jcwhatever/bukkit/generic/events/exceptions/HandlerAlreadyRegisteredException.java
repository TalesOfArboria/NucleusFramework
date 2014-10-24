package com.jcwhatever.bukkit.generic.events.exceptions;

import com.jcwhatever.bukkit.generic.events.EventHandler;

/**
 * Thrown when an event handler that is already registered with a {@code GenericsEventManager}
 * is registered again.
 */
public class HandlerAlreadyRegisteredException extends RuntimeException {

    private String _msg;

    public HandlerAlreadyRegisteredException(EventHandler handler) {
        _msg = "Event handler is already registered: " + handler.getClass().getName();
    }

    @Override
    public String getMessage() {
        return _msg;
    }

}
