package com.jcwhatever.bukkit.generic.commands.exceptions;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;

/**
 * Thrown when a command without the required command annotation is detected.
 */
public class MissingCommandAnnotationException extends RuntimeException {

    private String _message;

    public MissingCommandAnnotationException(Class<? extends AbstractCommand> commandClass) {
        _message = "Could not find expected type annotation for command class: " + commandClass.getName();
    }

    @Override
    public String getMessage() {
        return _message;
    }

}
