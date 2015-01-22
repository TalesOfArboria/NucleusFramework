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

package com.jcwhatever.nucleus.providers.bankitems;

import com.jcwhatever.nucleus.mixins.IPaginator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * An {@link IBankItem} represents all items of the same type, meta, durability, etc.
 * in a players account. The amount of the item can be "infinite" (or up to the technical limit).
 *
 * <p>The {@link IBankItem} may also represent a sub item of the root {@link IBankItem}
 * that is used to represent an {@link ItemStack} in an amount up to its maximum stack size.</p>
 *
 * <p>The {@link IPaginator} implementation as well as the {@link Iterable} implementation
 * returns {@link IBankItem}'s that are sub items of their root {@link IBankItem} instance.
 * Each sub item can have no more than the maximum stack size of the item type. When a sub item
 * is modified, it also modifies the root item.</p>
 *
 * <p>Note that the {@link IBankItem} is not publicly modifiable. Any iterators or collections
 * produced by the item should honor this. Use {@link java.lang.UnsupportedOperationException}
 * where appropriate. Modifications should take place through {@link IBankItemsAccount}.</p>
 */
public interface IBankItem extends IPaginator<IBankItem>, Iterable<IBankItem> {

    /**
     * A unique identifier for the account {@code IBankItem}. The id is only
     * unique to the root item. All sub items share the same ID as their root.
     */
    UUID getId();

    /**
     * Determine if the {@code IBankItem} is the root item.
     */
    boolean isRootItem();

    /**
     * Get the root item for the current {@code IBankItem}. If the
     * current items is the the master item, the current item is
     * returned.
     */
    IBankItem getRootItem();

    /**
     * Get the item type.
     */
    Material getType();

    /**
     * Get the quantity held.
     */
    int getAmount();

    /**
     * Get the quantity held by the root {@code IBankItem}.
     */
    int getRootAmount();

    /**
     * Get the maximum stack size of the item.
     */
    int getMaxStackSize();

    /**
     * Get the total number of {@code ItemStack}'s that can be produced
     * from the amount of items where each {@code ItemStack} is filled with
     * the maximum amount of items that can be added.
     */
    int getTotalStacks();

    /**
     * Get sub items of the current item where each sub item represents an
     * {@code ItemStack} up to the maximum stack size.
     */
    List<IBankItem> getItems();

    /**
     * Create a new {@code ItemStack} that is a duplicate of the {@code ItemStack}
     * represented by the {@code IBankItem}.
     *
     * @param amount  The amount.
     */
    ItemStack toItemStack(int amount);
}
