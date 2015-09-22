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
 * Interface for a component text line.
 */
public interface IChatLine {

    /**
     * Get the total number of components in the text line.
     */
    int totalComponents();

    /**
     * Append a chat component to the line.
     *
     * @param component  The chat component.
     */
    void append(IChatComponent component);

    /**
     * Append a collection of chat components to the line.
     *
     * @param components  The chat components.
     */
    void appendAll(Collection<? extends IChatComponent> components);

    /**
     * Prepend a chat component to the line.
     *
     * @param component  The chat component.
     */
    void prepend(IChatComponent component);

    /**
     * Prepend a collection of chat components to the line.
     *
     * @param components  The chat components.
     */
    void prependAll(Collection<? extends IChatComponent> components);

    /**
     * Get the raw text of the line.
     */
    String getText();

    /**
     * Append the raw text of the line to the specified output.
     *
     * @param output  The output.
     */
    void getText(Appendable output);

    /**
     * Get the formatted text of the line.
     */
    String getFormatted();

    /**
     * Append the formatted text to the specified output.
     *
     * @param output  The output.
     */
    void getFormatted(Appendable output);

    /**
     * Get the components in the line.
     */
    List<IChatComponent> getComponents();

    /**
     * Add the line components to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<IChatComponent>> T getComponents(T output);

    /**
     * Convert the text line to a formatted string.
     */
    String toString();
}
