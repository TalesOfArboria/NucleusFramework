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

import java.util.Collection;
import java.util.List;

/**
 * Interface adapter for Minecraft chat messages.
 */
public interface IChatMessage extends CharSequence {

    /**
     * Get the total number of lines in the message.
     */
    int totalLines();

    /**
     * Get the total number of components in the message.
     */
    int totalComponents();

    /**
     * Get the total number of characters in the message.
     */
    int charLen();

    /**
     * Get the message raw text.
     */
    String getText();

    /**
     * Append the message raw text to the specified output.
     *
     * @param output  The output.
     */
    void getText(Appendable output);

    /**
     * Get the formatted text.
     */
    String getFormatted();

    /**
     * Append the formatted text to the specified output.
     *
     * @param output  The output.
     */
    void getFormatted(Appendable output);

    /**
     * Append a text component to the last line.
     *
     * @param component  The component.
     */
    void append(IChatComponent component);

    /**
     * Append a text line.
     *
     * @param line  The text line.
     */
    void append(IChatLine line);

    /**
     * Get chat message components.
     */
    List<IChatComponent> getComponents();

    /**
     * Add chat message components into specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<IChatComponent>> T getComponents(T output);

    /**
     * Get chat message lines.
     */
    List<IChatLine> getLines();

    /**
     * Add chat message lines into specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<IChatLine>> T getLines(T output);

    /**
     * Get the message as a formatted string.
     */
    String toString();
}
