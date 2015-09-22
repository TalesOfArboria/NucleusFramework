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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatLine;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatLine;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * {@link SimpleChatMessage} implementation for {@link TextFormatterResult}.
 *
 * <p>Generates lines from a collection of {@link IChatComponent} and applies
 * line related text formatter settings.</p>
 */
public class FormatLines extends SimpleChatMessage {

    FormatLines(Collection<? extends IChatComponent> components, TextFormatterSettings settings) {
        super(load(components, settings));
    }

    private static List<IChatLine> load(Collection<? extends IChatComponent> components,
                                        TextFormatterSettings settings) {
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

        for (IChatComponent component : components) {

            if (component instanceof FormatNewLine) {
                output.add(current);
                current = new SimpleChatLine();
                continue;
            }

            current.append(component);
        }

        output.add(current);
    }

    private static void addPrefix(TextFormatterSettings settings, List<IChatLine> lines) {
        IChatMessage prefix = settings.getLinePrepend();
        if (prefix == null)
            return;

        List<IChatComponent> prefixComponents = prefix.getComponents();

        for (IChatLine line : lines) {
            line.prependAll(prefixComponents);
        }
    }

    private static void fixLines(TextFormatterSettings settings, List<IChatLine> lines) {
        int maxLen = settings.getMaxLineLen();
        if (maxLen <= 0)
            return;

        double prefixLen = 0;
        IChatMessage prefix = settings.getLinePrepend();
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

                    if (len < maxLen) {
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

                        lineIterator.remove();
                        lineIterator.add(new SimpleChatLine(lineComponents));
                        lineIterator.add(newLine);

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
