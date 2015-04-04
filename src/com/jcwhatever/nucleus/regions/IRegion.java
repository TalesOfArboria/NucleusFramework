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

package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IMeta;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPlayerOwnable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.mixins.IPrioritizable;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

/**
 * Represents a persisted region.
 *
 * <p>For nearly all cases, the abstract class {@link Region} should be extended
 * or use one of the other abstract implementations such as {@link BuildableRegion},
 * {@link RestorableRegion} or {@link MultiSnapshotRegion}.</p>
 */
public interface IRegion extends IRegionSelection, INamedInsensitive,
        IPlayerOwnable, IPrioritizable, IPluginOwned, IMeta, IDisposable {

    /**
     * Get the owning plugin.
     */
    @Override
    Plugin getPlugin();

    /**
     * Get the sorting priority of the region.
     */
    @Override
    int getPriority();

    /**
     * Find locations in the region that are made of the specified {@link Material}.
     *
     * @param material  The material to find.
     */
    Collection<Location> find(Material material);

    /**
     * Refresh all chunks the region intersects with.
     */
    void refreshChunks();

    /**
     * Determine if the region contains a block of the specified {@link Material}.
     *
     * @param material  The material to find.
     */
    boolean contains(Material material);

    /**
     * Remove all specified entity types from the region.
     *
     * @param itemTypes  The entity types to remove.
     */
    void removeEntities (Class<?>... itemTypes);

    /**
     * Determine if the region watches players to see if they enter or leave.
     */
    boolean isEventListener();

    /**
     * Get the regions event listener.
     */
    IRegionEventListener getEventListener();

    /**
     * Get the regions priority when handling player entering or leaving region.
     *
     * @param priorityType  The priority type.
     */
    RegionEventPriority getEventPriority(PriorityType priorityType);

    /**
     * Add a transient event handler to the region.
     *
     * @param handler  The handler to add.
     */
    boolean addEventHandler(IRegionEventHandler handler);

    /**
     * Remove a transient event handler from the region.
     *
     * @param handler  The handler to add.
     */
    boolean removeEventHandler(IRegionEventHandler handler);

    /**
     * Get the region class.
     *
     * <p>The same as calling {@link #getClass} method except in cases where the actual
     * region is not represented by the implementer (wrappers), in which case the
     * encapsulated regions class is returned.</p>
     */
    Class<? extends IRegion> getRegionClass();
}
