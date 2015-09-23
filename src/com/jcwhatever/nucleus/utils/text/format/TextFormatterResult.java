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
import com.jcwhatever.nucleus.utils.text.components.IChatLine;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Used by {@link TextFormatter} to return a result.
 */
public class TextFormatterResult implements ITextFormatterResult {

    private final List<IChatComponent> _components;
    private final boolean _isParsed;

    private boolean _parsedColor;
    private String _text;
    private SimpleChatMessage _result;

    TextFormatterResult() {
        _components = new ArrayList<>(5);
        _isParsed = true;
    }

    TextFormatterResult(IChatComponent component) {
        _components = new ArrayList<>(1);
        _components.add(component);
        _isParsed = false;
    }

    TextFormatterResult(Collection<? extends IChatComponent> components) {
        _components = new ArrayList<>(components);
        _isParsed = false;
    }

    @Override
    public boolean isParsed() {
        return _isParsed;
    }

    @Override
    public boolean isColorParsed() {
        return _parsedColor;
    }

    @Override
    public void rebuild(TextFormatterSettings settings) {
        _result = new SimpleChatMessage(_components, settings);
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    @Override
    public String toString() {

        if (_text == null) {
            StringBuilder buffer = new StringBuilder(_components.size() * 50);
            for (IChatComponent component : _components) {
                buffer.append(component.toString());
            }
            _text = buffer.toString();
        }

        return _text;
    }

    TextFormatterResult setParsedColor(boolean hasColor) {
        _parsedColor = hasColor;

        return this;
    }

    @Override
    public int totalLines() {
        return _result.totalLines();
    }

    @Override
    public int totalComponents() {
        return _result.totalComponents();
    }

    @Override
    public int charLen() {
        return _result.charLen();
    }

    @Override
    public String getText() {
        return _result.getText();
    }

    @Override
    public void getText(Appendable output) {
        _result.getText(output);
    }

    @Override
    public String getFormatted() {
        return _result.getFormatted();
    }

    @Override
    public void getFormatted(Appendable output) {
        _result.getFormatted(output);
    }

    @Override
    public void append(IChatComponent component) {
        if (_result == null) {
            _components.add(component);
        }
        else {
            _result.append(component);
        }
    }

    @Override
    public void append(IChatLine line) {
        _result.append(line);
    }

    @Override
    public List<IChatComponent> getComponents() {
        return _result.getComponents();
    }

    @Override
    public <T extends Collection<IChatComponent>> T getComponents(T output) {
        return _result.getComponents(output);
    }

    @Override
    public List<IChatLine> getLines() {
        return _result.getLines();
    }

    @Override
    public <T extends Collection<IChatLine>> T getLines(T output) {
        return _result.getLines(output);
    }

    TextFormatterResult finishResult(TextFormatterSettings settings) {
        rebuild(settings);
        return this;
    }

    void appendAll(TextFormatterResult result) {
        _components.addAll(result._components);
        _parsedColor = _parsedColor || result._parsedColor;
    }

    void appendAll(Collection<IChatComponent> components) {
        for (IChatComponent component : components) {
            _components.add(component);
        }
    }
}
