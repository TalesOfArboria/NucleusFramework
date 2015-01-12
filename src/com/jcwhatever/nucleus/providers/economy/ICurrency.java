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

package com.jcwhatever.nucleus.providers.economy;

/**
 * A type that represents an economy currency.
 */
public interface ICurrency {

    /**
     * Specifies how a currency name is used.
     */
    public enum CurrencyNoun {
        SINGULAR,
        PLURAL
    }

    /**
     * Format an amount into a string using the economy settings.
     *
     * @param amount  The amount to format.
     */
    String format(double amount);

    /**
     * Get the currency name.
     *
     * @param noun  The type of noun to return.
     */
    String getName(CurrencyNoun noun);

    /**
     * Get the conversion factor from the
     * stored currency amount.
     */
    double getConversionFactor();

    /**
     * Convert an amount of the specified currency to
     * the current currency.
     *
     * @param amount    The amount to convert.
     * @param currency  The currency of the amount.
     */
    double convert(double amount, ICurrency currency);
}
