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

package com.jcwhatever.nucleus.utils.text.format;

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Text parser
 */
public class TextParser {

    String _text;
    private int _index = -1;

    /**
     * Constructor.
     *
     * @param text  The text to be parsed.
     */
    public TextParser(CharSequence text) {
        PreCon.notNull(text);

        _text = text.toString();
    }

    /**
     * Determine if parsing is finished.
     */
    public boolean isFinished() {
        return _text.length() == 0 || _index >= _text.length();
    }

    /**
     * Get the current character.
     */
    public char current() {
        if (!isInRange(_index))
            return 0;

        return _text.charAt(_index);
    }

    /**
     * Get the next character after incrementing the cursor.
     */
    public char next() {
        _index++;
        if (!isInRange(_index))
            return 0;

        return _text.charAt(_index);
    }

    /**
     * Skip the specified number of characters and
     * return the character at the new cursor position.
     *
     * @param skip  The number of characters to skip.
     */
    public char skip(int skip) {
        _index += skip;
        if (!isInRange(_index))
            return 0;

        return _text.charAt(_index);
    }

    /**
     * Peek the character at the specified relative
     * position without moving the cursor.
     *
     * @param amount  The number of characters ahead/behind to peek.
     */
    public char peek(int amount) {
        int index = _index + amount;
        if (!isInRange(index))
            return 0;

        return _text.charAt(index);
    }

    private boolean isInRange(int index) {
        if (index >= _text.length())
            return false;

        if (index < 0 || _text.length() == 0)
            return false;

        return true;
    }
}
