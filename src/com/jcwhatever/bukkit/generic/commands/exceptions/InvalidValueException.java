package com.jcwhatever.bukkit.generic.commands.exceptions;

import javax.annotation.Nullable;

import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * Thrown when an argument value is not valid.
 * 
 * @author JC The Pants
 *
 */
public class InvalidValueException extends Exception {

	private static final long serialVersionUID = 1L;
	private final String _parameterName;
	private final String _parameterDescription;
	private final String _message;
	
	
	
	public InvalidValueException(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);
                
        _parameterName = parameterName;
        _parameterDescription = null;
        _message = Lang.get("Invalid value provided for parameter '{0}'.", parameterName);
    }
	
	/**
	 * Constructor.
	 */
	public InvalidValueException(String parameterName, @Localized String parameterDescription) {
	    PreCon.notNullOrEmpty(parameterName);
	    PreCon.notNull(parameterDescription);
	    	    
	    _parameterName = parameterName;
	    _parameterDescription = parameterDescription;
	    _message = Lang.get("Invalid value provided for parameter '{0}'.", parameterName);
    }
	
	/**
     * Get the name of the invalid arguments parameter.
     * 
     * @return
     */
    @Nullable
    public String getParameterName() {
        return _parameterName;
    }
    
	/**
	 * Get the message that was set, if any.
	 * If the message is set, it should override auto generated messages.
	 */
	@Override
	@Nullable
	@Localized
	public String getMessage() {
	    return _message;	    
	}
	
	@Nullable
	@Localized	
	public String getParameterDescription() {
	    return _parameterDescription;
	}
}
