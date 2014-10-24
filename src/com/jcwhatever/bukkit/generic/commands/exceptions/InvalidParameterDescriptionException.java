package com.jcwhatever.bukkit.generic.commands.exceptions;

import com.jcwhatever.bukkit.generic.commands.CommandInfoContainer;
import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localized;


public class InvalidParameterDescriptionException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    private String _message;
    
    public InvalidParameterDescriptionException(CommandInfoContainer commandInfo, String parameterName) {
        _message = Lang.get("Invalid description for parameter '{0}' in command '{1}'", parameterName, commandInfo.getCommandName()); 
    }
    
    @Override
    @Localized
    public String getMessage() {
        return _message;
    }
    
}
