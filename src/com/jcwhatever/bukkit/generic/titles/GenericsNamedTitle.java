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

package com.jcwhatever.bukkit.generic.titles;

import javax.annotation.Nullable;

/**
 * GenericsLib implementation of {@link INamedTitle}
 */
public class GenericsNamedTitle extends GenericsTitle implements INamedTitle {

    private final String _name;
    private final String _searchName;

    /**
     * Constructor.
     *
     * <p>Uses default times.</p>
     *
     * @param name      The name of the title.
     * @param title     The title text components.
     * @param subTitle  The sub title text components.
     */
    public GenericsNamedTitle(String name, String title,
                              @Nullable String subTitle) {

        this(name, title, subTitle, -1, -1, -1);
    }

    /**
     * Constructor.
     *
     * @param name         The name of the title.
     * @param title        The title text components.
     * @param subTitle     The sub title text components.
     * @param fadeInTime   The time spent fading in.
     * @param stayTime     The time spent being displayed.
     * @param fadeOutTime  The time spent fading out.
     */
    public GenericsNamedTitle(String name, String title,
                              @Nullable String subTitle,
                              int fadeInTime, int stayTime, int fadeOutTime) {

        super(title, subTitle, fadeInTime, stayTime, fadeOutTime);

        _name = name;
        _searchName = name.toLowerCase();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }
}
