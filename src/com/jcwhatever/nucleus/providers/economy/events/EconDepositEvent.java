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


package com.jcwhatever.nucleus.providers.economy.events;

import com.jcwhatever.nucleus.providers.economy.IAccount;
import com.jcwhatever.nucleus.providers.economy.ICurrency;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when money is deposited into an account.
 */
public class EconDepositEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final IAccount _account;
	private final double _originalAmount;
    private final ICurrency _currency;
	private double _amount;
    private double _convertedAmount;

	/**
	 * Constructor.
	 *
	 * @param account   The account receiving the deposit.
	 * @param amount    The amount being deposited.
     * @param currency  The currency of the amount.
	 */
	public EconDepositEvent(IAccount account, double amount, ICurrency currency) {
		PreCon.notNull(account);
        PreCon.notNull(currency);

		_account = account;
		_originalAmount = amount;
        _currency = currency;
        _amount = amount;
        _convertedAmount = amount * currency.getConversionFactor();
	}

	/**
	 * Get the account that is receiving the deposit.
	 */
	public IAccount getAccount() {
		return _account;
	}

	/**
	 * Get the amount being deposited when the event was first called.
	 */
	public double getOriginalAmount() {
		return _originalAmount;
	}

	/**
	 * Get the amount being deposited.
	 */
	public double getAmount() {
		return _amount;
	}

    /**
     * Get the amount converted to the providers default currency.
     */
    public double getConvertedAmount() {
        return _convertedAmount;
    }

    /**
     * Get the currency of the amount.
     */
    public ICurrency getCurrency() {
        return _currency;
    }

	/**
	 * Set the amount being deposited.
	 *
	 * <p>The value of the amount should correspond to the currency of the event.</p>
     *
	 * @param amount The amount to deposit.
	 */
	public void setAmount(double amount) {
		PreCon.positiveNumber(amount);

		_amount = amount;
        _convertedAmount = amount * _currency.getConversionFactor();
	}

	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
