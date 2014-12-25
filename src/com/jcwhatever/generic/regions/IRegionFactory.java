/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.generic.regions;

import com.jcwhatever.generic.regions.selection.RegionSelection;
import com.jcwhatever.generic.storage.IDataNode;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Represents a factory that can create region instances.
 */
public interface IRegionFactory<T extends IRegion> {

    /**
     * Create a new region without a region selection or
     * the region selection is loaded from the provided
     * data node.
     *
     * @param plugin      The owning plugin.
     * @param regionName  The region node name.
     * @param regionNode  The regions data node to load from.
     */
    T create(Plugin plugin, String regionName, @Nullable IDataNode regionNode);

    /**
     * Create a new region defined by a region selection.
     *
     * <p>The provided selection should already have valid
     * coordinates defined.</p>
     *
     * <p>If a data node is provided, the new selection is
     * saved to it.</p>
     *
     * @param plugin      The owning plugin.
     * @param regionName  The region node name.
     * @param regionNode  The regions data node to load from and or save to.
     * @param selection   The region selection.
     */
    T create(Plugin plugin, String regionName, @Nullable IDataNode regionNode, RegionSelection selection);
}
