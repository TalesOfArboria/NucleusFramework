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

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

/**
 * A command parameter description.
 */
public class ParameterDescription {

    private final String _parameterName;
    private final String _description;

    /**
     * Constructor.
     *
     * @param parameterName  The parameter name.
     * @param description    The parameter description.
     */
    public ParameterDescription(String parameterName, String description) {
        PreCon.notNullOrEmpty(parameterName);
        PreCon.notNull(description);

        _parameterName = parameterName;
        _description = description;
    }

    /**
     * Constructor.
     *
     * @param rawDescription  The raw description to parse.
     */
    public ParameterDescription(String rawDescription) {

        String[] descComp = TextUtils.PATTERN_EQUALS.split(rawDescription, -1);

        _parameterName = descComp[0].trim();

        if (descComp.length < 2) {
            throw new RuntimeException("Invalid description for parameter '" + _parameterName + '\'');
        }

        // re-add equal characters that may have been in the description.
        _description = TextUtils.concat(1, descComp, "=");
    }

    /**
     * Get the parameter name.
     */
    public String getParameterName() {
        return _parameterName;
    }

    /**
     * Get the parameter description.
     */
    public String getDescription() {
        return _description;
    }
}
