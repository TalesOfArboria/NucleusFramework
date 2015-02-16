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
 * A {@link CharSequence} thin wrapper for a char[].
 */
public class CharArraySequence implements CharSequence {

    private char[] _chars;

    /**
     * Constructor.
     *
     * @param characters  The character array to wrap.
     */
    public CharArraySequence(char[] characters) {
        _chars = characters;
    }

    @Override
    public int length() {
        return _chars.length;
    }

    @Override
    public char charAt(int index) {
        return _chars[index];
    }

    @Override
    public CharArraySequence subSequence(int start, int end) {

        char[] sub = new char[end - start];

        System.arraycopy(_chars, start, sub, start, end - start);

        return new CharArraySequence(sub);
    }

    @Override
    public String toString() {
        return new String(_chars);
    }
}
