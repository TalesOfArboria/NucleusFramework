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

import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.ColoredCircularString;
import com.jcwhatever.nucleus.utils.text.TextUtils;

/**
 * An {@link IDynamicText} implementation that displays marquee
 * text which can scroll text from left to right or right to left.
 */
public class MarqueeText implements IDynamicText {

    private final ColoredCircularString _marquee;

    private volatile int _charWidth;
    private volatile IDynamicText _text;
    private volatile MarqueeDirection _direction = MarqueeDirection.LEFT;
    private volatile int _refreshRate = 3; // 3 ticks
    private volatile long _nextUpdate;
    private volatile String _currentText;
    private final Object _sync = new Object();

    public enum MarqueeDirection {
        LEFT,
        RIGHT
    }

    /**
     * Constructor.
     *
     * @param charWidth  The character width of the marquee.
     * @param text       The text to display.
     */
    public MarqueeText(int charWidth, String text) {
        PreCon.notNull(text);

        _text = new DynamicTextBuilder().append(text).build();
        _marquee = new ColoredCircularString();
        _charWidth = charWidth;
    }

    /**
     * Constructor.
     *
     * @param charWidth    The character width of the marquee.
     * @param dynamicText  The text to display.
     */
    public MarqueeText(int charWidth, IDynamicText dynamicText) {
        PreCon.notNull(dynamicText);

        _marquee = new ColoredCircularString();
        _charWidth = charWidth;
        _text = dynamicText;
    }

    /**
     * Get the marquee character width.
     */
    public int getWidth() {
        return _charWidth;
    }

    /**
     * Set the marquee character width.
     *
     * @param width  The width. Must be greater than zero.
     */
    public void setWidth(int width) {
        PreCon.greaterThanZero(width);

        _charWidth = width;
    }

    /**
     * Get the text being displayed in the marquee.
     */
    public IDynamicText getText() {
        return _text;
    }

    /**
     * Get the text currently being displayed by the marquee.
     */
    public String getCurrentText() {
        return _currentText;
    }

    /**
     * Set the marquee text.
     *
     * @param text  The text.
     */
    public void setText(String text) {
        PreCon.notNull(text);

        _text = new DynamicTextBuilder().append(text).build();
    }

    /**
     * Set the marquee text.
     *
     * @param text  The text.
     */
    public void setText(IDynamicText text) {
        PreCon.notNull(text);

        _text = text;
    }

    /**
     * Get the marquee's scroll direction.
     */
    public MarqueeDirection getDirection() {
        return _direction;
    }

    /**
     * Set the marquee's scroll direction.
     *
     * @param direction  The direction.
     */
    public void setDirection(MarqueeDirection direction) {
        PreCon.notNull(direction);

        _direction = direction;
    }

    @Override
    public String nextText() {

        // ensure refresh rate (and scroll rate) is maintained even if refreshed
        // at a faster rate than requested.
        if (_nextUpdate != 0 && _nextUpdate > System.currentTimeMillis()) {
            return _currentText;
        }

        synchronized (_sync) {

            String text = getMarqueeText();

            _marquee.setString(text);

            if (_direction == MarqueeDirection.LEFT) {
                _marquee.rotateLeft(1);
            } else {
                _marquee.rotateRight(1);
            }

            _currentText = _marquee.subSequence(0, _charWidth).toString();
            _nextUpdate = System.currentTimeMillis() + _refreshRate;

            return _currentText;
        }
    }

    @Override
    public int getRefreshRate() {
        return _refreshRate;
    }

    /**
     * Set the refresh rate. The refresh rate also represents
     * the scroll speed of the marquee.
     *
     * @param rate       The rate.
     * @param timeScale  The rate time scale.
     */
    public void setRefreshRate(int rate, TimeScale timeScale) {
        _refreshRate = rate * timeScale.getTimeFactor() / 50;
    }

    // get the current text modified for the marquee
    private String getMarqueeText() {
        return _direction == MarqueeDirection.LEFT
                ? TextUtils.padLeft(_text.nextText(), _charWidth)
                : TextUtils.padRight(_text.nextText(), _charWidth);
    }
}
