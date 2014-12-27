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


package com.jcwhatever.nucleus.itembank;


import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Exception to throw when a specific amount of items is accessed from a players item bank account
 * but there are not enough items to meet the request.
 */
public class InsufficientItemsException extends Exception {

    private UUID _playerId;
    private int _balance;
    private ItemStack _item;

    /**
     * Constructor.
     *
     * @param playerId  The id of the player with insufficient items.
     * @param item      The item stack that represents the item the player has an insufficient amount of.
     * @param balance   The number of items the player actually has.
     */
    public InsufficientItemsException(UUID playerId, ItemStack item, int balance) {
        PreCon.notNull(playerId);
        PreCon.notNull(item);
        PreCon.positiveNumber(balance);

        _playerId = playerId;
        _balance = balance;
        _item = item;
    }

    /**
     * Get the id of the player with insufficient items.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the number of items the player has.
     */
    public int getBalance() {
        return _balance;
    }

    /**
     * An item stack that represents the item the player has an insufficient amount of.
     */
    public ItemStack getItem() {
        return _item;
    }
}
