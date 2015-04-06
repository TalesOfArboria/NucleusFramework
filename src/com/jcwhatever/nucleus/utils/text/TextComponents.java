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

package com.jcwhatever.nucleus.utils.text;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormats;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A collection of {@link TextComponent} instances.
 */
public class TextComponents implements Iterable<TextComponent> {

    private final String _text;
    private final List<TextComponent> _components;
    private final Object _sync = new Object();
    private String _formatted;

    /**
     * Constructor.
     *
     * <p>Generates {@link TextComponent} collection from text.</p>
     *
     * @param text  The text.
     */
    public TextComponents(String text) {
        PreCon.notNull(text);

        _text = text;
        _components = getTextComponents(text);
    }

    /**
     * Constructor.
     *
     * @param textComponents  The text components to clone.
     */
    public TextComponents(TextComponents textComponents) {
        PreCon.notNull(textComponents);

        _text = textComponents._text;
        _components = new ArrayList<>(textComponents._components);
    }

    /**
     * Get the text used to generate the {@link TextComponent}'s in
     * the collection.
     */
    public String getText() {
        return _text;
    }

    /**
     * Get text generated from the components.
     */
    public String getFormatted() {
        if (_formatted == null) {
            synchronized (_sync) {

                if (_formatted != null)
                    return _formatted;

                StringBuilder buffer = new StringBuilder(_components.size() * 15);

                for (TextComponent component : this) {
                    buffer.append(component.getFormatted());
                }

                _formatted = buffer.toString();
            }
        }
        return _formatted;
    }

    /**
     * Get the number of {@link TextComponent}'s in the collection.
     */
    public int size() {
        return _components.size();
    }

    /**
     * Get the {@link TextComponent} at the specified index location.
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

    /**
     * Parse the given text into a list of {@link TextComponents}.
     *
     * @param text  The text to parse.
     */
    private static List<TextComponent> getTextComponents(String text) {
        PreCon.notNull(text);

        text = TextUtils.format(text);

        StringBuilder buffer = new StringBuilder(text.length());

        LinkedList<TextComponent> result = new LinkedList<>();

        for (int i = 0; i < text.length(); i++) {

            char ch = text.charAt(i);

            boolean hasFormat = false;
            int startIndex = i;
            while (ch == TextFormat.CHAR && i < text.length() - 1 &&
                    TextFormat.isFormatChar(text.charAt(i + 1))) {
                hasFormat = true;
                i += 1;
                ch = text.charAt(i);
            }

            if (hasFormat) {

                if (buffer.length() > 0) {

                    TextFormats formats = TextFormat.getFormatAt(startIndex - 1, text);
                    TextComponent textComponent = new TextComponent(buffer.toString(), formats);

                    buffer.setLength(0);

                    if (textComponent.getText().length() > 0)
                        result.addLast(textComponent);
                }
            }
            else {

                buffer.append(ch);
            }
        }

        if (buffer.length() > 0) {
            TextFormats formats = TextFormat.getFormatAt(text.length() - 1, text);
            TextComponent textComponent = new TextComponent(buffer.toString(), formats);
            result.addLast(textComponent);
        }

        return new ArrayList<>(result);
    }
}
