package com.jcwhatever.bukkit.generic.commands.exceptions;

import javax.annotation.Nullable;

import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * Thrown when two parameters with the same name are detected.
 * 
 * <p>Exception can be thrown if there is an error in a command implementation
 * or if the command sender uses an optional argument twice or with then name of
 * a required parameter.</p>
 * 
 * @author JC The Pants
 *
 */
public class DuplicateParameterException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String _parameterName;
    public String _message;
    
    /**
     * Constructor.
     * 
     * @param parameterName  The name of the duplicate parameter
     */
    public DuplicateParameterException(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);
        
        _parameterName = parameterName;
    }
    /**
     * Constructor.
     * 
     * @param parameterName  The name of the duplicate parameter
     * @param message        The message to display to the command sender
     */
    public DuplicateParameterException(String parameterName, String message) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNullOrEmpty(message);
        
        _parameterName = parameterName;
    }
    
    /**
     * Determine if a message is set
     * @return
     */
    public boolean hasMessage() {
        return _message != null;
    }
    
    /**
     * Get the custom message describing the exception.
     */
    @Nullable
    public String getMessage() {
        return _message;
    }
    
    /**
     * Get the name of the duplicate parameter.
     * @return
     */
    public String getParameterName() {
        return _parameterName;
    }

}
