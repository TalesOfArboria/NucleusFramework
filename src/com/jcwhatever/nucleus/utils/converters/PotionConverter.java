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

package com.jcwhatever.nucleus.utils.converters;

import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.potion.Potion;

import javax.annotation.Nullable;

/**
 * Converts a {@link java.lang.String} representation of a potion ID or a {@link java.lang.Number}
 * to {@link org.bukkit.potion.Potion}.
 */
public class PotionConverter extends Converter<Potion> {

    protected PotionConverter() {}

    @Nullable
    @Override
    protected Potion onConvert(@Nullable Object value) {

        if (value instanceof String) {
            value = TextUtils.parseShort((String) value, Short.MIN_VALUE);
            if (value == new Short(Short.MIN_VALUE))
                return null;
        }
        else if (value instanceof Number) {
            value = ((Number)value).shortValue();
        }

        if (value instanceof Short) {
            return Potion.fromDamage((Short) value);
        }

        return null;
    }
}
