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

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A kit that can be modified.
 */
public interface IModifiableKit extends IKit {

    /**
     * Set the kits helmet item.
     *
     * @param helmet  The helmet.
     */
    void setHelmet(@Nullable ItemStack helmet);

    /**
     * Set the kits chest plate item.
     *
     * @param chestplate  The chestplate.
     */
    void setChestplate(@Nullable ItemStack chestplate);

    /**
     * Set the kits legging item.
     *
     * @param leggings  The leggings.
     */
    void setLeggings(@Nullable ItemStack leggings);

    /**
     * Set the kits boots item.
     *
     * @param boots  The boots.
     */
    void setBoots(@Nullable ItemStack boots);

    /**
     * Add an array of non-armor slot items to the kit.
     *
     * @param items  The items to add.
     */
    public void addItems(ItemStack... items);

    /**
     * Add a collection of non-armor slot items to the kit.
     *
     * @param items  The items to add.
     */
    public void addItems(Collection<ItemStack> items);

    /**
     * Remove 1 or more non-armor slot items from the kit.
     *
     * @param items  The items to remove.
     *
     * @return  True if any of the items were removed.
     */
    public boolean removeItems(ItemStack... items);

    /**
     * Remove 1 or more non-armor slot items from the kit.
     *
     * @param items  The items to remove.
     *
     * @return  True if any of the items were removed.
     */
    public boolean removeItems(Collection<ItemStack> items);

    /**
     * Add an array of armor or non-armor slot items to the kit.
     *
     * @param items  The items to add.
     */
    public void addAnyItems(ItemStack... items);

    /**
     * Add a collection of armor or non-armor slot items to the kit.
     *
     * @param items  The items to add.
     */
    public void addAnyItems(Collection<ItemStack> items);

    /**
     * Remove 1 or more items from the kit, armor or non-armor.
     *
     * @param items  The items to remove.
     *
     * @return  True if any of the items were removed.
     */
    public boolean removeAnyItems(ItemStack... items);

    /**
     * Remove 1 or more non-armor slot items from the kit.
     *
     * @param items  The items to remove.
     *
     * @return  True if any of the items were removed.
     */
    public boolean removeAnyItems(Collection<ItemStack> items);

    /**
     * Save the modified kit.
     */
    public boolean save();
}
