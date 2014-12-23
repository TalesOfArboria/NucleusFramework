/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.commands.parameters;

import com.jcwhatever.bukkit.generic.mixins.INamed;
import com.jcwhatever.bukkit.generic.utils.ArrayUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import javax.annotation.Nullable;

/**
 * Represents a command parameter.
 */
public class CommandParameter implements INamed {

    private final String _parameterName;
    private final String _defaultValue;

    /**
     * Constructor.
     *
     * @param parameterName  The parameter name.
     * @param defaultValue   The default value.
     */
    public CommandParameter(String parameterName, @Nullable String defaultValue) {
        PreCon.notNullOrEmpty(parameterName);

        _parameterName = parameterName;
        _defaultValue = defaultValue;
    }

    /**
     * Constructor.
     *
     * @param rawParameter  The raw parameter info to parse.
     */
    public CommandParameter(String rawParameter) {

        ParseResult result = new ParseResult();

        parseRawParameter(result, rawParameter);

        _parameterName = result.parameterName;
        _defaultValue = result.defaultValue;
    }

    /**
     * Get the parameter name.
     */
    @Override
    public String getName() {
        return _parameterName;
    }

    /**
     * Determine if the parameter has a default value.
     */
    public boolean hasDefaultValue() {
        return _defaultValue != null;
    }

    /**
     * Get the default value.
     *
     * @return  Null if there is no default value.
     */
    @Nullable
    public String getDefaultValue() {
        return _defaultValue;
    }

    @Override
    public int hashCode() {
        return _parameterName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommandParameter &&
                ((CommandParameter) obj)._parameterName.equals(_parameterName);
    }

    protected void parseRawParameter(ParseResult result, String rawParameter) {
        String[] paramComp = TextUtils.PATTERN_EQUALS.split(rawParameter, -1);
        result.parameterName = paramComp[0];

        result.defaultValue = ArrayUtils.get(paramComp, 1, null);

        if (result.defaultValue != null)
            result.defaultValue = result.defaultValue.trim();
    }

    protected static class ParseResult {
        public String parameterName;
        public String defaultValue;
    }
}

