package com.jcwhatever.bukkit.generic.events.exceptions;

import com.jcwhatever.bukkit.generic.events.GenericsEventListener;

/**
 * Thrown when an event listener that is already registered with a {@code GenericsEventManager}
 * is registered again.
 */
public class ListenerAlreadyRegisteredException extends RuntimeException {

    private String _msg;

    public ListenerAlreadyRegisteredException(GenericsEventListener listener) {
        _msg = "Event listener is already registered: " + listener.getClass().getName();
    }

    @Override
    public String getMessage() {
        return _msg;
    }

}
