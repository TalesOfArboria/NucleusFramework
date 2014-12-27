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

package com.jcwhatever.nucleus.utils.items.serializer.metahandlers;

import com.jcwhatever.nucleus.utils.PreCon;

/**
 * An intermediate meta object used to store raw data that is parsed from
 * or will be serialized into an {@code ItemStack} String.
 */
public class ItemMetaObject {

    private String _name;
    private String _rawData;

    /**
     * Constructor.
     *
     * @param name     The meta name.
     * @param rawData  The raw data value.
     */
    public ItemMetaObject(String name, String rawData) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(rawData);

        _name = name;
        _rawData = rawData;
    }

    /**
     * Get the meta name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the raw meta value.
     */
    public String getRawData() {
        return _rawData;
    }
}
