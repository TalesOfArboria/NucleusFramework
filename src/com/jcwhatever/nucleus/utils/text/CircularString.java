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
     * Returns a reference to the array of un-rotated characters being
     * used by the {@code CircularString}.
     */
    public char[] getChars() {
        return _string;
    }

    /**
     * Reset the rotation back to 0.
     */
    public void reset() {
        if (_rotation == 0)
            return;

        _rotation = 0;
        _hasResult = false;
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

        _rotation = getCorrectIndex(_rotation + amount);

        _hasResult = false;
    }

    /**
     * Rotate the string to the right by the specified amount.
     *
     * @param amount  The amount to rotate the string.
     */
    public void rotateRight(int amount) {

        _rotation = getCorrectIndex(_rotation - amount);

        _hasResult = false;
    }

    /**
     * Set the character at the specified position. The position
     * is relative to the current rotation.
     *
     * <p>The index can be a negative or out of bounds number.</p>
     *
     * @param index  The index.
     * @param ch     The replacement character.
     */
    public void setChar(int index, char ch) {
        _string[getCorrectIndex(_rotation + index)] = ch;
    }

    @Override
    public int length() {
        return _string.length;
    }

    /**
     * Set the character at the specified index relative
     * to the current rotation.
     *
     * <p>The index can be a negative or out of bounds number.</p>
     *
     * @param index  The index.
     */
    @Override
    public char charAt(int index) {
        return _string[getCorrectIndex(_rotation + index)];
    }

    /**
     * Get a new {@code CircularString} whose characters are
     * a sub-sequence of characters from the current.
     *
     * @param start  The start index of the sequence relative to the current rotation.
     * @param end    The end index of the sequence relative to the current rotation.
     */
    @Override
    public CircularString subSequence(int start, int end) {
        PreCon.positiveNumber(start, "start");
        PreCon.positiveNumber(end, "end");
        PreCon.isValid(end >= start, "end argument must be greater than or equal to start argument.");
        PreCon.lessThan(start, _string.length, "start");
        PreCon.lessThan(end, _string.length, "end");

        CircularString result = new CircularString();
        result._string = new char[end - start];

        updateResult();

        System.arraycopy(_result, start % _string.length, result._string, 0, (end - start));
        return result;
    }

    @Override
    public String toString() {

        updateResult();

        return new String(_result);
    }

    private int getCorrectIndex(int index) {

        return index < 0
                ? _string.length - ((-index) % _string.length)
                : index % _string.length;
    }

    private void updateResult() {

        if (_hasResult)
            return;

        if (_result == null || _result.length != _string.length)
            _result = new char[_string.length];

        for (int i=0; i < _string.length; i++) {
            _result[i] = _string[(_rotation + i) % _string.length];
        }

        _hasResult = true;
    }
}
