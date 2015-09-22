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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Simple implementation of {@link IChatLine}.
 */
public class SimpleChatLine implements IChatLine {

    private final Deque<IChatComponent> _components;

    /**
     * Constructor.
     *
     * <p>Creates empty line.</p>
     */
    public SimpleChatLine() {
        _components = new ArrayDeque<>(10);
    }

    /**
     * Constructor.
     *
     * @param components  The line components.
     */
    public SimpleChatLine(Collection<? extends IChatComponent> components) {
        PreCon.notNull(components);

        _components = new ArrayDeque<>(components);
    }

    @Override
    public int totalComponents() {
        return _components.size();
    }

    @Override
    public void append(IChatComponent component) {
        PreCon.notNull(component);

        _components.add(component);
    }

    @Override
    public void appendAll(Collection<? extends IChatComponent> components) {
        PreCon.notNull(components);

        _components.addAll(components);
    }

    @Override
    public void prepend(IChatComponent component) {
        PreCon.notNull(component);

        _components.addFirst(component);
    }

    @Override
    public void prependAll(Collection<? extends IChatComponent> components) {
        PreCon.notNull(components);

        List<IChatComponent> reversed = new ArrayList<>(components);
        Collections.reverse(reversed);

        for (IChatComponent component : reversed) {
            _components.addFirst(component);
        }
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder(_components.size() * 30);
        getText(sb);
        return sb.toString();
    }

    @Override
    public void getText(Appendable output) {
        PreCon.notNull(output);

        for (IChatComponent component : _components) {
            component.getText(output);
        }
        try {
            output.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFormatted() {
        StringBuilder sb = new StringBuilder(_components.size() * 40);
        getFormatted(sb);
        return sb.toString();
    }

    @Override
    public void getFormatted(Appendable output) {
        PreCon.notNull(output);

        for (IChatComponent component : _components) {
            component.getFormatted(output);
        }
        try {
            output.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<IChatComponent> getComponents() {
        return new ArrayList<>(_components);
    }

    @Override
    public <T extends Collection<IChatComponent>> T getComponents(T output) {
        PreCon.notNull(output);

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(output.size() + _components.size());

        output.addAll(_components);
        return output;
    }
}
