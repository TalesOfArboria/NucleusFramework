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

package com.jcwhatever.nucleus.managed.scoreboards;

import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;

import javax.annotation.Nullable;

/**
 * Interface for a scoreboard used as a sidebar hud
 */
public interface IHudObjective extends IObjective {

    /**
     * Get the number of text lines in the HUD.
     */
    int size();

    /**
     * Set a line of text on the HUD.
     *
     * @param lineIndex  The 0 based index of the line.
     * @param text       The text to display.
     * @param args       Optional format arguments.
     *
     * @return  Self for chaining.
     */
    IHudObjective set(int lineIndex, CharSequence text, Object... args);

    /**
     * Set a line of dynamic text on the HUD.
     *
     * @param lineIndex  The 0 based index of the line.
     * @param text       The dynamic text to display.
     *
     * @return  Self for chaining.
     */
    IHudObjective set(int lineIndex, IDynamicText text);

    /**
     * Get the text at the specified line index.
     *
     * @param lineIndex  The 0 based index of the line.
     *
     * @return  The text or null if there is no line at the specified index.
     */
    @Nullable
    IDynamicText get(int lineIndex);

    /**
     * Clear a line of text without removing the line.
     *
     * @param lineIndex  The 0 based index of the line.
     *
     * @return  Self for chaining.
     */
    IHudObjective clear(int lineIndex);

    /**
     * Remove a line from the HUD.
     *
     * @param lineIndex  The 0 based index of the line.
     *
     * @return  Self for chaining.
     */
    IHudObjective remove(int lineIndex);

    /**
     * Clear all text from the HUD.
     *
     * @return  Self for chaining.
     */
    IHudObjective clearAll();

    /**
     * Clear all lines from the HUD.
     *
     * @return  Self for chaining.
     */
    IHudObjective removeAll();
}
