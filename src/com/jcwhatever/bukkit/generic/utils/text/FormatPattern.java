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

package com.jcwhatever.bukkit.generic.utils.text;

import java.util.regex.Pattern;

/**
 * Used to hold custom text formatting information
 */
public class FormatPattern {
    private final String _key;
    private final Pattern _pattern;

    public FormatPattern (String regex) {
        _key = regex;
        _pattern = Pattern.compile(regex);
    }

    public String getReplaceKey() {
        return _key;
    }

    public Pattern getPattern() {
        return _pattern;
    }

    public FormatEntry getEntry(String replaceValue) {
        return new FormatEntry(replaceValue);
    }

    public class FormatEntry {

        private final String _replaceValue;

        FormatEntry(String replaceValue) {
            _replaceValue = replaceValue;
        }

        public Pattern getPattern() {
            return _pattern;
        }

        public String getReplaceValue() {
            return _replaceValue;
        }
    }
}

