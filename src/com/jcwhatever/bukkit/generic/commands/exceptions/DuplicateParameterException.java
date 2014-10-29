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

import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Thrown when two parameters with the same name are detected.
 *
 * <p>Exception can be thrown if there is an error in a command implementation
 * or if the command sender uses an optional argument twice or with then name of
 * a required parameter.</p>
 */
public class DuplicateParameterException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String _parameterName;
    public String _message;

    /**
     * Constructor.
     *
     * @param parameterName  The name of the duplicate parameter.
     */
    public DuplicateParameterException(String parameterName) {
        PreCon.notNullOrEmpty(parameterName);

        _parameterName = parameterName;
    }
    /**
     * Constructor.
     *
     * @param parameterName  The name of the duplicate parameter.
     * @param message        The message to display to the command sender.
     */
    public DuplicateParameterException(String parameterName, String message) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNullOrEmpty(message);

        _parameterName = parameterName;
    }

    /**
     * Get the custom message describing the exception.
     */
    @Override
    @Nullable
    public String getMessage() {
        return _message;
    }

    /**
     * Get the name of the duplicate parameter.
     */
    public String getParameterName() {
        return _parameterName;
    }

}
