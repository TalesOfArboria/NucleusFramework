/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.utils.text;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.Iterator;
import java.util.List;

/**
 * A collection of {@link TextComponent} instances.
 */
public class TextComponents implements Iterable<TextComponent> {

    private final String _text;
    private final List<TextComponent> _components;
    private String _formatted;

    /**
     * Constructor.
     *
     * <p>Generates {@code TextComponent} collection from text.</p>
     *
     * @param text  The text.
     */
    public TextComponents(String text) {
        PreCon.notNull(text);

        _text = text;
        _components = TextUtils.getTextComponents(text);
    }

    /**
     * Constructor.
     *
     * @param text        The original text used to generate the components.
     * @param components  The components generated from the text.
     */
    public TextComponents(String text, List<TextComponent> components) {
        PreCon.notNull(text);
        PreCon.notNull(components);

        _text = text;
        _components = components;
    }

    /**
     * Get the text used to generate the
     * {@code TextComponent}'s in the collection.
     */
    public String getText() {
        return _text;
    }

    /**
     * Get text generated from the components.
     */
    public String getFormatted() {
        if (_formatted == null) {
            StringBuilder buffer = new StringBuilder(_components.size() * 15);

            for (TextComponent component : this) {
                buffer.append(component.getFormatted());
            }

            _formatted = buffer.toString();
        }
        return _formatted;
    }

    /**
     * Get the number of {@code TextComponent}'s in
     * the collection.
     */
    public int size() {
        return _components.size();
    }

    /**
     * Get the {@code TextComponent} at the specified
     * index location.
     *
     * @param index  The index.
     */
    public TextComponent get(int index) {
        return _components.get(index);
    }

    @Override
    public Iterator<TextComponent> iterator() {
        return new Iterator<TextComponent>() {

            int index =0;

            @Override
            public boolean hasNext() {
                return index <= _components.size();
            }

            @Override
            public TextComponent next() {
                TextComponent n = _components.get(index);
                index ++;
                return n;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
