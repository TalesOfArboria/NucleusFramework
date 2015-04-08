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


package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.bankitems.IBankItem;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccount;
import com.jcwhatever.nucleus.providers.bankitems.InsufficientItemsException;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Provide scripts with API access to Item Bank.
 */
public class SAPI_ItemBank implements IDisposable {

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Deposit an {@link ItemStack} into the players account.
     *
     * @param player  The player.
     * @param item    The item to deposit.
     * @param qty     The quantity to deposit.
     */
    public void deposit(Object player, ItemStack item, int qty) {
        PreCon.notNull(player);
        PreCon.notNull(item);
        PreCon.greaterThanZero(qty);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IBankItemsAccount account = Nucleus.getProviders().getBankItems()
                .getAccount(p.getUniqueId());

        account.deposit(item, qty);
    }

    /**
     * Withdraw items from the players account.
     *
     * @param player  The player.
     * @param item    The item to withdraw.
     * @param qty     The quantity to withdraw.
     *
     * @return  True if successful, false if insufficient items.
     */
    public boolean withdraw(Object player, ItemStack item, int qty) {
        PreCon.notNull(player);
        PreCon.notNull(item);
        PreCon.greaterThanZero(qty);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IBankItemsAccount account = Nucleus.getProviders().getBankItems()
                .getAccount(p.getUniqueId());

        try {
            account.withdraw(item, qty);
        } catch (InsufficientItemsException e) {
            return false;
        }
        return true;
    }

    /**
     * Get all {@link ItemStack}'s in the players item bank account.
     *
     * @param player  The player.
     */
    public IBankItem[] getBankItems(Object player) {
        PreCon.notNull(player);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IBankItemsAccount account = Nucleus.getProviders().getBankItems()
                .getAccount(p.getUniqueId());

        List<IBankItem> items = account.getItems();
        if (items == null)
            return new IBankItem[0];

        IBankItem[] itemsArray = new IBankItem[items.size()];

        for (int i=0; i < itemsArray.length; i++) {
            itemsArray[i] = items.get(i);
        }

        return itemsArray;
    }

    /**
     * Determine if the player has the specified {@link ItemStack} in the
     * specified quantity available in account.
     *
     * @param player  The player.
     * @param item    The item to check.
     * @param qty     The quantity to check for.
     *
     * @return  True if available.
     */
    public boolean has(Object player, ItemStack item, int qty) {
        PreCon.notNull(player);
        PreCon.notNull(item);
        PreCon.greaterThanZero(qty);

        Player p = PlayerUtils.getPlayer(qty);
        PreCon.notNull(p);

        IBankItemsAccount account = Nucleus.getProviders().getBankItems()
                .getAccount(p.getUniqueId());

        IBankItem bankItem = account.getItem(item);
        return bankItem != null && bankItem.getAmount() >= qty;
    }

    /**
     * Get the total quantity available of the specified item
     * from the players item bank account.
     *
     * @param player  The player.
     * @param item    The item to check.
     */
    public int getBalance(Object player, ItemStack item) {
        PreCon.notNull(player);
        PreCon.notNull(item);

        Player p = PlayerUtils.getPlayer(player);
        PreCon.notNull(p);

        IBankItemsAccount account = Nucleus.getProviders().getBankItems()
                .getAccount(p.getUniqueId());

        IBankItem bankItem = account.getItem(item);
        if (bankItem == null)
            return 0;

        return bankItem.getAmount();
    }
}

