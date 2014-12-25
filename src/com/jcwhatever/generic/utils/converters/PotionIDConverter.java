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


package com.jcwhatever.generic.utils.converters;

import org.bukkit.potion.Potion;
/**
 * Converts between a Potion and potion id.
 */
public class PotionIDConverter extends ValueConverter<Potion, Short> {

    PotionIDConverter() {}

    /**
     * Convert a number value representing the potion meta id into a new Potion instance.
     */
    @Override
    protected Potion onConvert(Object value) {
        if (value instanceof String) {
            try {
                value = Short.parseShort((String)value);
            }
            catch (NumberFormatException nfe) {
                return null;
            }
        }
        else if (value instanceof Byte) {
            Byte b = (Byte)value;
            value = b.shortValue();
        }
        else if (value instanceof Integer) {
            Integer i = (Integer)value;
            value = i.shortValue();
        }

        if (value instanceof Short) {
            short potionId = (Short) value;
            return Potion.fromDamage(potionId);
        }

        return null;
    }

    /**
     * Convert a Potion object to its meta id.
     */
    @Override
    protected Short onUnconvert(Object value) {
        if (value instanceof Potion) {
            Potion potion = (Potion)value;
            return potion.toDamageValue();
        }
        return 8192;
    }

}
