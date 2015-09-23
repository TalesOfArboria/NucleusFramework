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

import javax.annotation.Nullable;

/**
 * Interface for line settings.
 */
public interface IChatLineSettings {

    /**
     * Get the maximum line length.
     *
     * @return  The max line length or -1 to indicate no max len.
     */
    int getMaxLineLen();

    /**
     * Set the max line length.
     *
     * @param len  The maximum line length. -1 to indicate no max length.
     *
     * @return  Self for chaining.
     */
    IChatLineSettings setMaxLineLen(int len);

    /**
     * Get the String prepended before each line.
     */
    @Nullable
    IChatMessage getLinePrefix();

    /**
     * Set the text prepended before each line.
     *
     * @param text  The text to prepend.
     *
     * @return  Self for chaining.
     */
    IChatLineSettings setLinePrefix(@Nullable IChatMessage text);
}
