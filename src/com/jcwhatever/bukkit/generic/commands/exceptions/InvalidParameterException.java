package com.jcwhatever.bukkit.generic.commands.exceptions;

import javax.annotation.Nullable;

import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * Thrown when a an optional parameter is used that isn't predefined by
 * the command.
 * 
 * @author JC The Pants
 *
 */
public class InvalidParameterException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String _parameterName;
    private String _message;
    
    /**
     * Constructor.
     * 
     * @param parameterName  The name of the invalid parameter
     */
    public InvalidParameterException(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);
                
        _parameterName = parameterName;
    }
    
    /**
     * Constructor.
     * 
     * @param parameterName  The name of the invalid parameter
     * @param message        The description of the problem
     */
    public InvalidParameterException(String parameterName, String message) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNullOrEmpty(message);
        
        _parameterName = parameterName;
        _message = message;
    }
    
    /**
     * Determine if a custom message is set.
     * @return
     */
    public boolean hasMessage() {
        return _message != null;        
    }
    
    /**
     * Get custom message describing exception
     */
    @Override
    @Nullable    
    public String getMessage() {
        return _message;
    }
    
    /**
     * Get the name of the invalid parameter.
     * @return
     */
    public String getParameterName() {
        return _parameterName;
    }
}
