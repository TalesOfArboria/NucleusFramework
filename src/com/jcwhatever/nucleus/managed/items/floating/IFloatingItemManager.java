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

package com.jcwhatever.nucleus.managed.items.floating;

import com.jcwhatever.nucleus.Nucleus;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Manages floating items.
 *
 * @see Nucleus#getFloatingItems
 */
public interface IFloatingItemManager {

    /**
     * Add a new floating item.
     *
     * @param plugin     The items owning plugin.
     * @param name       The name of the item.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack}.
     *
     * @return  The created {@link IFloatingItem} or null if the name already exists.
     */
    @Nullable
    IFloatingItem add(Plugin plugin, String name, ItemStack itemStack);

    /**
     * Add a new floating item.
     *
     * @param plugin     The items owning plugin.
     * @param name       The name of the item.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack}.
     * @param location   Optional initial location to set.
     *
     * @return  The created {@link IFloatingItem} or null if the name already exists.
     */
    @Nullable
    IFloatingItem add(Plugin plugin, String name, ItemStack itemStack,
                      @Nullable Location location);

    /**
     * Determine if the manager contains an item.
     *
     * @param plugin  The items owning plugin.
     * @param name    The name of the item.
     */
    boolean contains(Plugin plugin, String name);

    /**
     * Get an item by name.
     *
     * @param plugin  The items owning plugin.
     * @param name    The name of the item.
     *
     * @return  The item or null if not found.
     */
    @Nullable
    IFloatingItem get(Plugin plugin, String name);

    /**
     * Get all items owned by the specified plugin.
     *
     * @param plugin  The owning plugin.
     */
    Collection<IFloatingItem> getAll(Plugin plugin);

    /**
     * Get all items owned by the specified plugin.
     *
     * @param plugin  The owning plugin.
     * @param output  The output collection to place results into.
     *
     * @return  The output collection.
     */
    <T extends Collection<IFloatingItem>> T getAll(Plugin plugin, T output);

    /**
     * Remove an item.
     *
     * @param plugin  The items owning plugin.
     * @param name    The name of the item.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean remove(Plugin plugin, String name);
}
