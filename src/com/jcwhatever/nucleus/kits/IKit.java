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

package com.jcwhatever.nucleus.kits;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.items.ItemStackComparer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * A kit of items that can be given (or taken) from a player.
 */
public interface IKit extends INamedInsensitive, IPluginOwned {

    /**
     * Get the kit helmet, if any.
     */
    @Nullable
    ItemStack getHelmet();

    /**
     * Get the kit chest plate, if any.
     */
    @Nullable
    ItemStack getChestplate();

    /**
     * Get the kit leggings, if any.
     */
    @Nullable
    ItemStack getLeggings();

    /**
     * Gets the kit boots, if any.
     */
    @Nullable
    ItemStack getBoots();

    /**
     * Gets a new array of non-armor items in the kit.
     */
    ItemStack[] getItems();

    /**
     * Gets the kit armor items as an a new array.
     */
    ItemStack[] getArmor();

    /**
     * Give the kit to the specified player
     *
     * @param p  The player to give a copy of the kit to.
     */
    void give(final Player p);

    /**
     * Take items from the kit away from the specified player.
     *
     * @param p    The player to take from.
     * @param qty  The number of items to take. (kit * qty)
     *
     * @return  True if the items were taken.
     */
    boolean take(Player p, int qty);

    /**
     * Take items from the kit away from the specified player.
     *
     * <p>Does not take items if the player does not have all required items.</p>
     *
     * @param p        The player to take from.
     * @param comparer The {@code ItemStackComparer} used to compare items.
     * @param qty      The number of items to take. (kit * qty)
     *
     * @return  True if the player had all the items.
     */
    boolean take(Player p, ItemStackComparer comparer, int qty);
}
