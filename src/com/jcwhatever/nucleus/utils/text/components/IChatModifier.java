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

import com.jcwhatever.nucleus.utils.text.TextColor;

import javax.annotation.Nullable;

/**
 * Interface adapter for Minecraft's chat modifier.
 */
public interface IChatModifier {

    /**
     * Determine if there are any modifications.
     */
    boolean isModified();

    /**
     * Determine if the text is bold.
     */
    boolean isBold();

    /**
     * Set the modifier bold flag.
     *
     * @param isBold  True for bold text, otherwise false.
     */
    void setBold(boolean isBold);

    /**
     * Determine if the text is Italic.
     */
    boolean isItalic();

    /**
     * Set the modifier italic flag.
     *
     * @param isItalic  True for italic text, otherwise false.
     */
    void setItalic(boolean isItalic);

    /**
     * Determine if the text is strike through.
     */
    boolean isStrikeThrough();

    /**
     * Set the modifier strike through flag.
     *
     * @param isStrikeThrough  True to strike text, otherwise false.
     */
    void setStrikeThrough(boolean isStrikeThrough);

    /**
     * Determine if the text is underlined.
     */
    boolean isUnderlined();

    /**
     * Set the modifier underline flag.
     *
     * @param isUnderlined  True to underline, otherwise false.
     */
    void setUnderline(boolean isUnderlined);

    /**
     * Determine if the text is random (Magic).
     */
    boolean isMagic();

    /**
     * Set the modifier random flag.
     *
     * @param isRandom  True for random characters, otherwise false.
     */
    void setMagic(boolean isRandom);

    /**
     * Determine if the text is reset.
     *
     * <p>Reset modifiers should return false or null for all other modifier parameters.
     * (except click and hover).</p>
     */
    boolean isReset();

    /**
     * Set the reset flag.
     *
     * <p>Causes all other modifier parameters (except click and hover) to be false or null.
     * Changing other parameters causes reset flag to become false.</p>
     */
    void reset();

    /**
     * Get the text color.
     */
    @Nullable
    TextColor getColor();

    /**
     * Set the text color.
     *
     * @param color  The text color.
     */
    void setColor(@Nullable TextColor color);

    /**
     * Get the clickable data.
     */
    @Nullable
    IChatClickable getClickable();

    /**
     * Set the clickable data.
     */
    void setClickable(@Nullable IChatClickable clickable);

    /**
     * Get the hoverable data.
     */
    @Nullable
    IChatHoverable getHoverable();

    /**
     * Set the hoverable data.
     */
    void setHoverable(@Nullable IChatHoverable hoverable);

    /**
     * Get the modifier format text.
     */
    String getFormatted();

    /**
     * Append the modifiers format text to the specified output.
     *
     * @param output  The output.
     */
    void getFormatted(Appendable output);
}
