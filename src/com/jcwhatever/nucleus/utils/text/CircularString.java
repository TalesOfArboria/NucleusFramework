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

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * A {@code CharSequence} implementation that allows the characters
 * to be rotated left or right.
 *
 * <p>Allows changing the string without changing the rotation.</p>
 */
public class CircularString implements CharSequence {

    private char[] _string;
    private char[] _result;
    private boolean _hasResult;
    private int _rotation;

    public CircularString() {
        _string = new char[0];
    }

    /**
     * Constructor.
     *
     * @param characters  The initial string characters.
     */
    public CircularString(char[] characters) {
        PreCon.notNull(characters);

        _string = characters;
    }

    /**
     * Constructor.
     *
     * @param string  The initial string.
     */
    public CircularString(String string) {
        PreCon.notNull(string);

        _string = string.toCharArray();
    }

    /**
     * Set the string. The rotation is not changed.
     *
     * @param string  The string to set.
     */
    public void setString(String string) {
        PreCon.notNull(string);

        _string = string.toCharArray();
        _hasResult = false;
    }

    /**
     * Reset the rotation back to 0.
     */
    public void reset() {
        _rotation = 0;
    }

    /**
     * Get the current rotation index.
     */
    public int getRotation() {
        return _rotation;
    }

    /**
     * Rotate the string to the left by the specified amount.
     *
     * @param amount  The amount to rotate the string.
     */
    public void rotateLeft(int amount) {

        _rotation += amount;

        correctRotation();
    }

    /**
     * Rotate the string to the right by the specified amount.
     *
     * @param amount  The amount to rotate the string.
     */
    public void rotateRight(int amount) {

        _rotation -= amount;

        correctRotation();
    }

    @Override
    public int length() {
        return _string.length;
    }

    @Override
    public char charAt(int index) {
        PreCon.positiveNumber(index, "index");
        PreCon.lessThan(index, _string.length, "index");

        return _string[(_rotation + index) % _string.length];
    }

    @Override
    public CircularString subSequence(int start, int end) {
        PreCon.positiveNumber(start, "start");
        PreCon.positiveNumber(end, "end");
        PreCon.isValid(end >= start, "end argument must be greater than or equal to start argument.");
        PreCon.lessThan(start, _string.length, "start");
        PreCon.lessThan(end, _string.length, "end");

        CircularString result = new CircularString();
        result._string = new char[end - start];

        System.arraycopy(_string, start, result._string, 0, end - start);
        return result;
    }

    @Override
    public String toString() {

        if (_hasResult)
            return new String(_result);

        if (_result == null || _result.length != _string.length)
            _result = new char[_string.length];

        for (int i=0; i < _string.length; i++) {
            _result[i] = _string[(_rotation + i) % _string.length];
        }

        _hasResult = true;

        return new String(_result);
    }

    private void correctRotation() {
        if (_rotation < 0) {
            _rotation = ((-_rotation) % _string.length) + _string.length;
            _rotation = _string.length - _rotation;
        }
        else {
            _rotation = _rotation % _string.length;
        }

        _hasResult = false;
    }
}
