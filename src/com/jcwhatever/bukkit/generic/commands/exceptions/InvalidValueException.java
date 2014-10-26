/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.commands.exceptions;

import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

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
