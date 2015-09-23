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
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

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

    public SimpleChatMessage(Collection<? extends IChatComponent> components,
                             IChatLineSettings settings) {
        PreCon.notNull(components);
        PreCon.notNull(settings);

        _lines = load(components, settings);
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

        for (int i=0; i < _lines.size(); i++) {
            IChatLine line = _lines.get(i);
            line.getFormatted(output);
            if (i < _lines.size() - 1) {
                try {
                    output.append('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
    public int length() {
        return charLen();
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
        return getFormatted();
    }

    private static List<IChatLine> load(Collection<? extends IChatComponent> components,
                                        IChatLineSettings settings) {
        PreCon.notNull(components);
        PreCon.notNull(settings);

        List<IChatLine> lines = new ArrayList<>(5);
        generateLines(components, lines);
        fixLines(settings, lines);
        addPrefix(settings, lines);

        return lines;
    }

    private static void generateLines(Collection<? extends IChatComponent> components, List<IChatLine> output) {

        IChatLine current = new SimpleChatLine();
        TextColor color = null;

        for (IChatComponent component : components) {

            if (component instanceof NewLineComponent) {
                output.add(current);
                current = new SimpleChatLine();
                continue;
            }

            boolean hasColor = component.getModifier().getColor() != null;
            // continue previous color if no new color has been set
            if (color != null && !hasColor && !component.getText().isEmpty()) {
                component.getModifier().setColor(color);
            }

            current.append(component);

            if (hasColor)
                color = component.getModifier().getColor();
        }

        output.add(current);
    }

    private static void addPrefix(IChatLineSettings settings, List<IChatLine> lines) {
        IChatMessage prefix = settings.getLinePrefix();
        if (prefix == null)
            return;

        List<IChatComponent> prefixComponents = prefix.getComponents();

        for (IChatLine line : lines) {
            line.prependAll(prefixComponents);
        }
    }

    private static void fixLines(IChatLineSettings settings, List<IChatLine> lines) {
        int maxLen = settings.getMaxLineLen();
        if (maxLen <= 0)
            return;

        double prefixLen = 0;
        IChatMessage prefix = settings.getLinePrefix();
        if (prefix != null) {
            prefixLen = getCharWidth(prefix.getText());
        }

        List<IChatComponent> lineComponents = new ArrayList<>(5);
        StringBuilder buffer = new StringBuilder(20);

        ListIterator<IChatLine> lineIterator = lines.listIterator();
        while (lineIterator.hasNext()) {

            IChatLine line = lineIterator.next();
            line.getComponents(lineComponents);

            double len = prefixLen;

            ListIterator<IChatComponent> iterator = lineComponents.listIterator();
            while (iterator.hasNext()) {
                IChatComponent component = iterator.next();
                component.getText(buffer);

                String lineText = buffer.toString();
                buffer.setLength(0);
                String[] words = TextUtils.PATTERN_SPACE.split(lineText);

                // iterate words and measure character length to determine if the line
                // needs to be broken into a new line.
                for (int i=0; i < words.length; i++) {
                    len += getCharWidth(words[i]);
                    len += 1; // add space as part of count

                    if (len < maxLen || buffer.length() == 0) {
                        buffer.append(words[i]);
                        if (i < words.length - 1) {
                            buffer.append(' ');
                        }
                    }
                    else {

                        // replace component with shortened version.
                        iterator.remove();
                        iterator.add(new SimpleChatComponent(buffer.toString(), component.getModifier()));

                        // create new line.
                        SimpleChatLine newLine = new SimpleChatLine();

                        newLine.append(new SimpleChatComponent(TextUtils.concat(i, words.length, words, " "),
                                component.getModifier()));

                        // transfer excess components to new line.
                        while (iterator.hasNext()) {
                            IChatComponent transferred = iterator.next();
                            iterator.remove();
                            newLine.append(transferred);
                        }

                        lineIterator.remove(); // remove old line
                        lineIterator.add(new SimpleChatLine(lineComponents)); // add modified line
                        lineIterator.add(newLine); // add new line
                        lineIterator.previous();
                        break;
                    }
                }

                buffer.setLength(0);
            }

            lineComponents.clear();
        }
    }

    private static double getCharWidth(String text) {

        double width = 0;

        for (int i=0; i < text.length(); i++) {
            char ch = text.charAt(i);

            switch (ch) {
                case '.':
                case ',':
                case ':':
                case 'i':
                case 'l':
                    width += 0.5D;
                    break;
                case 'm':
                case '-':
                    width += 1.5D;
                    break;
                default:
                    width += Character.isUpperCase(ch) ? 1.5D : 1D;
            }
        }
        return width;
    }
}
