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

package com.jcwhatever.nucleus.utils.items.loremeta;

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.util.regex.Matcher;

/**
 * Data container for a single lore text meta item.
 */
public class LoreMetaItem implements INamed {

    private final String _name;
    private final String _value;

    private Number _numberValue;

    /**
     * Constructor.
     *
     * @param name   The name of the lore meta.
     * @param value  The meta value.
     */
    public LoreMetaItem(String name, String value) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(value);

        _name = name;
        _value = value;
    }

    @Override
    public String getName() {
        return _name;
    }

    /**
     * Get the value as a string.
     */
    public String getValue() {
        return _value;
    }

    /**
     * Get the value as a boolean.
     */
    public boolean booleanValue() {
        return TextUtils.parseBoolean(_value);
    }

    /**
     * Get the value as a byte.
     */
    public byte byteValue() {
        return numberValue().byteValue();
    }

    /**
     * Get the value as a short.
     */
    public short shortValue() {
        return numberValue().shortValue();
    }

    /**
     * Get the value as an integer.
     */
    public int intValue() {
        return numberValue().intValue();
    }

    /**
     * Get the value as a long.
     */
    public long longValue() {
        return numberValue().longValue();
    }

    /**
     * Get the value as a float.
     */
    public float floatValue() {
        return numberValue().floatValue();
    }

    /**
     * Get the value as a double.
     */
    public double doubleValue() {
        return numberValue().doubleValue();
    }

    /**
     * Get the value as a number.
     */
    public Number numberValue() {
        if (_numberValue != null)
            return _numberValue;

        Matcher matcher = TextUtils.PATTERN_DECIMAL_NUMBERS.matcher(_value);
        if (!matcher.find()) {
            return _numberValue = 0;
        }

        String number = matcher.group();
        return _numberValue = TextUtils.parseDouble(number, 0.0D);
    }
}
