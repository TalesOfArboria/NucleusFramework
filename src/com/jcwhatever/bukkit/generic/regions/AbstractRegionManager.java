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

package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.mixins.IReadOnly;
import com.jcwhatever.bukkit.generic.regions.data.RegionSelection;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Abstract implementation of a region manager.
 */
public abstract class AbstractRegionManager<T extends IRegion> {

    protected final Plugin _plugin;
    protected final IDataNode _dataNode;
    protected final IRegionFactory<T> _factory;

    protected Map<String, T> _regionMap;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Optional data node to load and store regions.
     * @param factory   The factory used to create new instances.
     */
    protected AbstractRegionManager(Plugin plugin, @Nullable IDataNode dataNode, IRegionFactory<T> factory) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);
        PreCon.notNull(factory);

        _plugin = plugin;
        _dataNode = dataNode;
        _factory = factory;

        loadSettings();
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the factory used to create new regions.
     */
    public IRegionFactory<T> getRegionFactory() {
        return _factory;
    }

    /**
     * Get a region.
     *
     * @param regionName  The name of the region.
     *
     * @return  Null if not found.
     */
    @Nullable
    public T getRegion(String regionName) {
        PreCon.notNullOrEmpty(regionName);

        if (_regionMap == null)
            return null;

        return _regionMap.get(regionName.toLowerCase());
    }

    /**
     * Get all regions.
     */
    public List<T> getRegions() {
        if (_regionMap == null)
            return new ArrayList<>(0);

        return new ArrayList<>(_regionMap.values());
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
    public T addRegion(String name, RegionSelection selection) {
        PreCon.validNodeName(name);
        PreCon.notNull(selection);
        PreCon.isValid(selection.isDefined(), "RegionSelection must be defined.");

        if (_regionMap == null) {
            _regionMap = new HashMap<>(10);
        }
        // make sure a region with that name does not already exist
        else if (_regionMap.containsKey(name.toLowerCase())) {
            return null;
        }

        IDataNode regionNode = _dataNode != null ? _dataNode.getNode(name) : null;

        T instance = _factory.create(_plugin, name, regionNode, selection);

        _regionMap.put(instance.getSearchName(), instance);

        return instance;
    }

    /**
     * Remove a region.
     *
     * @param name  The name of the region.
     *
     * @return  False if the region was not found and/or removed.
     */
    public boolean removeRegion(String name) {
        PreCon.notNullOrEmpty(name);

        if (_regionMap == null)
            return false;

        T region = _regionMap.remove(name.toLowerCase());
        if (region == null)
            return false;

        if (!(region instanceof IReadOnly) || !((IReadOnly) region).isReadOnly()) {
            region.dispose();
        }

        if (_dataNode != null) {
            _dataNode.getNode(region.getName()).remove();
            _dataNode.saveAsync(null);
        }

        return true;
    }

    /**
     * Called to load settings from the data node, if any.
     */
    protected void loadSettings() {
        if (_dataNode == null)
            return;

        _regionMap.clear();

        Set<String> nodeNames = _dataNode.getSubNodeNames();

        _regionMap = new HashMap<>(nodeNames.size() + 10);

        for (String nodeName : nodeNames) {
            IDataNode regionNode = _dataNode.getNode(nodeName);

            T instance = _factory.create(_plugin, nodeName, regionNode);

            _regionMap.put(instance.getSearchName(), instance);
        }
    }
}
