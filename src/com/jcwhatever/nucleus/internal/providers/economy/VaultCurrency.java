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

import net.milkbowl.vault.economy.Economy;

/**
 * Vault economy provider currency.
 */
public class VaultCurrency implements ICurrency {

    private final Economy _economy;

    public VaultCurrency(Economy economy) {
        _economy = economy;
    }

    @Override
    public String format(double amount) {
        return _economy.format(amount);
    }

    @Override
    public String getName(CurrencyNoun noun) {
        PreCon.notNull(noun);

        switch (noun) {
            case SINGULAR:
                //noinspection ConstantConditions
                return _economy.currencyNameSingular();

            case PLURAL:
                //noinspection ConstantConditions
                return _economy.currencyNamePlural();

            default:
                throw new AssertionError();
        }
    }

    @Override
    public double getConversionFactor() {
        return 1.0D;
    }

    @Override
    public double convert(double amount, ICurrency currency) {
        PreCon.notNull(currency);

        return amount / currency.getConversionFactor();
    }
}
