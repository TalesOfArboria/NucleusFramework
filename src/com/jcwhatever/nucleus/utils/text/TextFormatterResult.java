/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.nucleus.utils.text;

/**
 * Used by {@link TextFormatter} to return a result.
 */
public class TextFormatterResult implements CharSequence {

    private CharSequence _result;
    private boolean _parsedColor;
    private boolean _isParsed;

    TextFormatterResult() {
        _isParsed = true;
    }

    TextFormatterResult(String result) {
        _result = result;
    }

    /**
     * Determine if the result is parsed or if the
     * formatter determined if parsing was not needed
     * and returned the format template.
     *
     * @return  True if parsed, False if template is result.
     */
    public boolean isParsed() {
        return _isParsed;
    }

    /**
     * Determine if color was parsed. Does not always indicate the
     * presence of color since the parser may not have actually
     * parsed the template.
     *
     * <p>If the template is parsed and the settings indicate that
     * color should be ignored or removed, false is returned.</p>
     */
    public boolean parsedColor() {
        return _parsedColor;
    }

    TextFormatterResult setParsedColor(boolean hasColor) {
        _parsedColor = hasColor;

        return this;
    }

    TextFormatterResult setResult(CharSequence result) {
        _result = result;

        return this;
    }

    @Override
    public int length() {
        return _result.length();
    }

    @Override
    public char charAt(int index) {
        return _result.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return _result.subSequence(start, end);
    }

    @Override
    public String toString() {
        return _result.toString();
    }
}
