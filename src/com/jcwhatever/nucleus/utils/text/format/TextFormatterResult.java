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
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Used by {@link TextFormatter} to return a result.
 */
public class TextFormatterResult implements CharSequence, IChatMessage {

    private final List<IChatComponent> _components;
    private final boolean _isParsed;

    private boolean _parsedColor;
    private String _text;
    private FormatLines _result;

    TextFormatterResult() {
        _components = new ArrayList<>(5);
        _isParsed = true;
    }

    TextFormatterResult(IChatComponent component) {
        _components = new ArrayList<>(1);
        _components.add(component);
        _isParsed = false;
    }

    /**
     * Determine if the result is parsed or if the formatter determined if
     * parsing was not needed and returned the format template.
     *
     * @return  True if parsed, False if template is result.
     */
    public boolean isParsed() {
        return _isParsed;
    }

    /**
     * Determine if color was parsed.
     *
     * <p>Does not always indicate the presence of color since the parser may not
     * have actually parsed the template.</p>
     *
     * <p>If the template is parsed and the settings indicate that color should
     * be ignored or removed, false is returned.</p>
     */
    public boolean parsedColor() {
        return _parsedColor;
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
        _result.append(component);
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
        _result = new FormatLines(_components, settings);
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
