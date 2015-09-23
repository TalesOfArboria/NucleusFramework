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

package com.jcwhatever.nucleus.internal.providers.economy;

import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.text.DecimalFormat;

/**
 * Nucleus economy provider currency.
 */
public final class NucleusCurrency implements ICurrency {

    private final String _singular;
    private final String _plural;
    private final String _template;
    private final double _factor;
    private final DecimalFormat _formatter;

    public NucleusCurrency(String singular, String plural, double factor,
                           String formatTemplate, DecimalFormat formatter) {
        PreCon.notNull(singular);
        PreCon.notNull(plural);
        PreCon.notNull(formatTemplate);
        PreCon.notNull(formatter);

        _singular = singular;
        _plural = plural;
        _factor = factor;
        _template = formatTemplate;
        _formatter = formatter;
    }

    @Override
    public String format(double amount) {

        String formattedDecimal = _formatter.format(amount);
        return TextUtils.format(_template,
                formattedDecimal, (amount > 1 ? _plural : _singular)).toString();
    }

    @Override
    public String getName(CurrencyNoun noun) {
        PreCon.notNull(noun);

        switch (noun) {
            case SINGULAR:
                return _singular;

            case PLURAL:
                return _plural;

            default:
                throw new AssertionError();
        }
    }

    @Override
    public double getConversionFactor() {
        return _factor;
    }

    @Override
    public double convert(double amount, ICurrency currency) {
        double base = amount / currency.getConversionFactor();
        return base * _factor;
    }
}
