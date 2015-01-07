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

package com.jcwhatever.nucleus.utils.text.dynamic;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextColor;

import org.bukkit.Bukkit;

/**
 * {@code IDynamicText} implementation that displays a status bar
 * that shows percentage.
 */
public class StatusBarText implements IDynamicText {

    private volatile int _width;
    private volatile double _percent = 1.0D;
    private volatile String _currentText;

    private volatile char _fullChar = '\u2587';
    private volatile char _partialChar = '\u2587';
    private volatile char _emptyChar = '\u2587';

    private volatile TextColor _fullColor = TextColor.GREEN;
    private volatile TextColor _partialColor = TextColor.GRAY;
    private volatile TextColor _emptyColor = TextColor.GRAY;

    private final Object _sync = new Object();

    protected volatile boolean _isUpdateRequired = true;

    /**
     * Constructor.
     *
     * <p>Width is 10 characters.</p>
     */
    public StatusBarText() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param charWidth  The character width of the bar.
     */
    public StatusBarText(int charWidth) {
        _width = charWidth;
    }

    /**
     * Get the character width of the bar.
     */
    public int getCharWidth() {
        return _width;
    }

    /**
     * Set the character width of the bar.
     *
     * @param width  The character width.
     */
    public void setCharWidth(int width) {

        if (_width == width)
            return;

        synchronized (_sync) {

            if (_width == width)
                return;

            _width = width;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character used to indicate
     * a percentage.
     */
    public char getFullChar() {
        return _fullChar;
    }

    /**
     * Set the character used to indicate
     * a percentage.
     *
     * @param ch  The character.
     */
    public void setFullChar(char ch) {

        if (_fullChar == ch)
            return;

        synchronized (_sync) {

            if (_fullChar == ch)
                return;

            _fullChar = ch;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character used to indicate
     * a partial percentage.
     */
    public char getPartialChar() {
        return _partialChar;
    }

    /**
     * Set the character used to indicate
     * a partial percentage.
     *
     * @param ch  The character.
     */
    public void setPartialChar(char ch) {

        if (_partialChar == ch)
            return;

        synchronized (_sync) {

            if (_partialChar == ch)
                return;

            _partialChar = ch;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character used to indicate
     * no percentage.
     */
    public char getEmptyChar() {
        return _emptyChar;
    }

    /**
     * Set the character used to indicate
     * no percentage.
     *
     * @param ch  The character.
     */
    public void setEmptyChar(char ch) {

        if (_emptyChar == ch)
            return;

        synchronized (_sync) {

            if (_emptyChar == ch)
                return;

            _emptyChar = ch;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character color used to indicate
     * a percentage.
     */
    public TextColor getFullColor() {
        return _fullColor;
    }

    /**
     * Set the character color used to indicate
     * a percentage.
     *
     * @param color  The color.
     */
    public void setFullColor(TextColor color) {
        PreCon.notNull(color);

        if (_fullColor == color)
            return;

        synchronized (_sync) {

            if (_fullColor == color)
                return;

            _fullColor = color;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character color used to indicate
     * a partial percentage.
     */
    public TextColor getPartialColor() {
        return _partialColor;
    }

    /**
     * Set the character color used to indicate
     * a partial percentage.
     *
     * @param color  The color.
     */
    public void setPartialColor(TextColor color) {
        PreCon.notNull(color);

        if (_partialColor == color)
            return;

        synchronized (_sync) {

            if (_partialColor == color)
                return;

            _partialColor = color;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the character color used to indicate
     * no percentage.
     *
     * @return  The color.
     */
    public TextColor getEmptyColor() {
        return _emptyColor;
    }

    /**
     * Set the character color used to indicate
     * no percentage.
     *
     * @param color  The color.
     */
    public void setEmptyColor(TextColor color) {
        PreCon.notNull(color);

        if (_emptyColor == color)
            return;

        synchronized (_sync) {

            if (_emptyColor == color)
                return;

            _emptyColor = color;
            _isUpdateRequired = true;
        }
    }

    /**
     * Get the current percentage displayed by
     * the bar.
     *
     * @return  The percent is a number between 0.0 and 1.0
     * where 1.0 = 100%.
     */
    public double getPercent() {
        return _percent;
    }

    /**
     * Set the current percentage displayed by
     * the bar.
     *
     * @param percent  The percent. The percent is a number between 0.0
     *                 and 1.0 where 1.0 = 100%
     */
    public void setPercent(double percent) {

        if (Double.compare(percent, _percent) == 0)
            return;

        synchronized (_sync) {

            if (Double.compare(percent, _percent) == 0)
                return;

            _percent = percent;
            _isUpdateRequired = true;
        }
    }

    @Override
    public String nextText() {
        if (!_isUpdateRequired && Bukkit.isPrimaryThread())
            return _currentText;

        synchronized (_sync) {

            if (!_isUpdateRequired)
                return _currentText;

            _isUpdateRequired = false;

            double dblWidth = Math.min(_width, _width * _percent);
            double round = Math.round(dblWidth);

            boolean hasPartial = dblWidth < round;

            int fullWidth = (int) Math.max(Math.floor(dblWidth), 0);

            StringBuilder buffer = new StringBuilder(_width + 6);

            for (int i = 0; i < _width; i++) {
                if (i < fullWidth) {
                    buffer.append(_fullColor.getFormatCode());
                    buffer.append(_fullChar);
                } else if (i == fullWidth && hasPartial) {
                    buffer.append(_partialColor.getFormatCode());
                    buffer.append(_partialChar);
                } else {
                    buffer.append(_emptyColor.getFormatCode());
                    buffer.append(_emptyChar);
                }
            }

            return _currentText = buffer.toString();
        }
    }

    @Override
    public int getRefreshRate() {
        return _isUpdateRequired ? 1 : 0;
    }

    @Override
    public String toString() {
        return nextText();
    }
}
