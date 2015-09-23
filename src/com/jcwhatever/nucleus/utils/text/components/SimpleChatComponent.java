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

package com.jcwhatever.nucleus.utils.text.components;

import com.jcwhatever.nucleus.utils.PreCon;

import java.io.IOException;

/**
 * Simple implementation of {@link IChatComponent}.
 */
public class SimpleChatComponent implements IChatComponent {

    private final CharSequence _text;
    private IChatModifier _modifier;

    /**
     * Constructor.
     *
     * @param rawText  Component raw text.
     */
    public SimpleChatComponent(CharSequence rawText) {
        PreCon.notNull(rawText);

        _text = rawText;
        _modifier = new SimpleChatModifier();
    }

    /**
     * Constructor.
     *
     * @param rawText   Component raw text.
     * @param modifier  Text modifier.
     */
    public SimpleChatComponent(String rawText, IChatModifier modifier) {
        PreCon.notNull(rawText);
        PreCon.notNull(modifier);

        _text = rawText;
        _modifier = modifier;
    }

    @Override
    public String getText() {
        return _text.toString();
    }

    @Override
    public void getText(Appendable output) {
        PreCon.notNull(output);

        try {
            output.append(_text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFormatted() {
        return _modifier.getFormatted() + getText();
    }

    @Override
    public void getFormatted(Appendable output) {
        PreCon.notNull(output);

        _modifier.getFormatted(output);
        getText(output);
    }

    @Override
    public IChatModifier getModifier() {
        return _modifier;
    }

    @Override
    public void setModifier(IChatModifier modifier) {
        PreCon.notNull(modifier);

        _modifier = modifier;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
