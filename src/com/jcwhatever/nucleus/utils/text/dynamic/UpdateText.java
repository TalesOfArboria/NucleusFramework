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

import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import javax.annotation.Nullable;

/**
 * Text that can be updated.
 */
public class UpdateText implements IDynamicText {

    private IChatMessage _text;
    private int _refreshRate = 3; // 3 ticks
    private final Object _sync = new Object();

    /**
     * Constructor.
     */
    public UpdateText() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param text  The initial text to display.
     */
    public UpdateText(@Nullable String text) {
        _text = text == null ? null : TextUtils.format(text);
    }

    /**
     * Get the text being displayed.
     */
    public String getText() {
        synchronized (_sync) {
            return _text.toString();
        }
    }

    /**
     * Set the text.
     *
     * @param text  The text.
     */
    public void setText(@Nullable String text) {
        synchronized (_sync) {
            _refreshRate = 1;
            _text = text == null ? null : TextUtils.format(text);
        }
    }

    @Override
    public IChatMessage nextText() {

        synchronized (_sync) {
            _refreshRate = 5;
            return _text;
        }
    }

    @Override
    public int getRefreshRate() {
        return _refreshRate;
    }
}

