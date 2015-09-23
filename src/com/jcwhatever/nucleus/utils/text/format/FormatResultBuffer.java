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

import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatModifier;
import com.jcwhatever.nucleus.utils.text.components.NewLineComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatModifier;

import java.util.ArrayList;
import java.util.List;

/*
 * 
 */
public class FormatResultBuffer implements IFormatterAppendable {

    private final StringBuilder _buffer;
    private IChatModifier _modifier = new SimpleChatModifier();
    private boolean _isModified;

    final List<IChatComponent> results = new ArrayList<>(20);
    int lineLen = 0;

    FormatResultBuffer() {
        _buffer = new StringBuilder(50);
    }

    FormatResultBuffer(int len) {
        _buffer = new StringBuilder(len);
    }

    void newLine() {
        IChatModifier currModifier = _modifier;
        reset(_modifier);
        results.add(new NewLineComponent());
        lineLen = 0;
        reset(new SimpleChatModifier(currModifier)); // reset using same modifier
    }

    void incrementCharCount(int amount) {
        lineLen += amount;
    }

    void reset() {
        reset(new SimpleChatModifier());
    }

    void reset(IChatModifier modifier) {
        if (isModified()) {
            results.add(new SimpleChatComponent(_buffer.toString(), this._modifier));
        }
        _buffer.setLength(0);
        this._modifier = modifier;
    }

    void hardReset() {
        reset();
        results.clear();
        lineLen = 0;
    }

    boolean isModified() {
        return _isModified || _modifier.isModified();
    }

    @Override
    public void append(Object object) {

        if (object instanceof IChatMessage) {
            reset();
            ((IChatMessage) object).getComponents(results);
            return;
        }
        else if (object instanceof IChatComponent) {
            reset();
            results.add((IChatComponent) object);
            return;
        }

        _isModified = true;
        _buffer.append(object);
    }

    @Override
    public IChatModifier getModifier() {
        return _modifier;
    }

    public void setModifier(IChatModifier modifier) {
        this._modifier = modifier;
    }
}
