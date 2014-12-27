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


package com.jcwhatever.nucleus.warp;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Manage warp locations.
 */
public class WarpManager {

    private final Map<String, IWarp> _warpMap = new HashMap<String, IWarp>(10);
    private final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public WarpManager (IDataNode dataNode) {
        _dataNode = dataNode;

        loadSettings();
    }

    /**
     * Get a warp by name.
     *
     * @param name  The name of the warp.
     */
    @Nullable
    public IWarp getWarp(String name) {
        return _warpMap.get(name.toLowerCase());
    }

    /**
     * Get all warps.
     */
    public List<IWarp> getWarps() {
        return CollectionUtils.unmodifiableList(_warpMap.values());
    }

    /**
     * Add a warp location.
     *
     * @param name      The name of the warp.
     * @param location  The warp location.
     *
     * @return  Null if there is already a warp with the specified name or
     * warp creation is cancelled/failed.
     */
    @Nullable
    public IWarp addWarp(String name, Location location) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(location);

        if (_warpMap.containsKey(name.toLowerCase()))
            return null;

        IDataNode warpNode = getWarpNode(name, _dataNode);
        if (warpNode == null)
            return null;

        IWarp warp = createWarp(name, location, warpNode);
        if (warp == null)
            return null;

        warpNode.saveAsync(null);

        _warpMap.put(warp.getSearchName(), warp);

        return warp;
    }

    /**
     * Delete a warp by name.
     *
     * @param name  The name of the warp.
     */
    public boolean deleteWarp(String name) {
        PreCon.notNullOrEmpty(name);

        IWarp warp = getWarp(name);
        if (warp == null)
            return false;

        _warpMap.remove(warp.getSearchName());

        IDataNode warpNode = getWarpNode(warp.getName(), _dataNode);
        warpNode.remove();
        warpNode.saveAsync(null);

        return true;
    }

    /**
     * Called to create a new warp instance.
     *
     * @param name      The name of the warp.
     * @param location  The warp location.
     * @param dataNode  The data node to store the warp.
     */
    protected IWarp createWarp(String name, Location location, IDataNode dataNode) {
        return new Warp(name, location, _dataNode);
    }

    /**
     * Called to get a new instance of a data node for a warp.
     *
     * @param name        The warp name.
     * @param parentNode  The parent node.
     */
    protected IDataNode getWarpNode(String name, IDataNode parentNode) {
        return parentNode.getNode(name);
    }

    /**
     * Called after settings are loaded.
     */
    protected void onLoad() {}

    // initial settings load
    private void loadSettings() {
        Set<String> warpNames = _dataNode.getSubNodeNames();

        for (String warpName : warpNames) {
            Location location = _dataNode.getLocation(warpName);
            if (location == null)
                continue;

            IDataNode warpNode = getWarpNode(warpName, _dataNode);
            if (warpNode == null)
                continue;

            IWarp warp = createWarp(warpName, location, warpNode);
            if (warp == null)
                continue;

            _warpMap.put(warp.getSearchName(), warp);
        }

        onLoad();
    }
}
