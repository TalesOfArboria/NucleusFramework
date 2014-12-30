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

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.mixins.IReadOnly;
import com.jcwhatever.nucleus.regions.selection.RegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * A region manager.
 */
public abstract class RegionManager<T extends IRegion> extends NamedInsensitiveDataManager<T> implements IPluginOwned {

    protected final Plugin _plugin;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Optional data node to load and store regions.
     */
    public RegionManager(Plugin plugin, @Nullable IDataNode dataNode) {
        super(dataNode);
        PreCon.notNull(plugin);

        _plugin = plugin;
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Add a new region.
     *
     * @param name       The node name of the region.
     * @param selection  The regions coordinates.
     *
     * @return  Null if the manager already has a region by the specified node name.
     */
    @Nullable
    public T add(String name, RegionSelection selection) {
        PreCon.validNodeName(name);
        PreCon.notNull(selection);
        PreCon.isValid(selection.isDefined(), "RegionSelection must be defined.");

        // make sure a region with that name does not already exist
        if (contains(name))
            return null;

        IDataNode regionNode = _dataNode != null ? getNode(name) : null;

        T instance = create(name, regionNode, selection);

        if (instance != null)
            add(instance);

        return instance;
    }

    protected abstract T create(String name, @Nullable IDataNode dataNode, RegionSelection selection);

    @Override
    protected void onRemove(T region) {
        super.onRemove(region);

        if (!(region instanceof IReadOnly) || !((IReadOnly) region).isReadOnly()) {
            region.dispose();
        }
    }
}
