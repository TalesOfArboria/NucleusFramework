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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple implementation of {@link IChatMessage}.
 */
public class SimpleChatMessage implements IChatMessage {

    private static IChatComponent NEW_LINE = new SimpleChatComponent("\n");

    private final List<IChatLine> _lines;
    private int _charLen = -1;

    public SimpleChatMessage() {
        _lines = new ArrayList<>(5);
    }

    public SimpleChatMessage(Collection<? extends IChatLine> lines) {
        PreCon.notNull(lines);

        _lines = new ArrayList<>(lines);
    }

    @Override
    public int totalComponents() {
        int total = 0;
        for (IChatLine line : _lines) {
            total += line.totalComponents();
        }
        return total;
    }

    @Override
    public int totalLines() {
        return 0;
    }

    @Override
    public int charLen() {
        if (_charLen == -1) {
            _charLen = toString().length();
        }
        return _charLen;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder(_lines.size() * 60);
        getText(sb);
        return sb.toString();
    }

    @Override
    public void getText(Appendable output) {
        PreCon.notNull(output);

        for (IChatLine line : _lines) {
            line.getText(output);
        }
    }

    @Override
    public String getFormatted() {
        StringBuilder sb = new StringBuilder(_lines.size() * 80);
        getText(sb);
        return sb.toString();
    }

    @Override
    public void getFormatted(Appendable output) {
        PreCon.notNull(output);

        for (IChatLine line : _lines) {
            line.getFormatted(output);
        }
    }

    @Override
    public void append(IChatComponent component) {
        PreCon.notNull(component);

        IChatLine current;

        if (_lines.isEmpty()) {
            current = new SimpleChatLine();
            _lines.add(current);
        } else {
            current = _lines.get(_lines.size() - 1);
        }

        current.append(component);
    }

    @Override
    public void append(IChatLine line) {
        PreCon.notNull(line);

        _lines.add(line);
    }

    @Override
    public List<IChatComponent> getComponents() {
        return getComponents(new ArrayList<IChatComponent>(_lines.size() * 5));
    }

    @Override
    public <T extends Collection<IChatComponent>> T getComponents(T output) {
        PreCon.notNull(output);

        for (int i=0; i < _lines.size(); i++) {
            IChatLine line = _lines.get(i);
            line.getComponents(output);
            if (i < _lines.size() - 1) {
                output.add(NEW_LINE);
            }
        }
        return output;
    }

    @Override
    public List<IChatLine> getLines() {
        return new ArrayList<>(_lines);
    }

    @Override
    public <T extends Collection<IChatLine>> T getLines(T output) {
        PreCon.notNull(output);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(output.size() + _lines.size());

        output.addAll(_lines);
        return output;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
