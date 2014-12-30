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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Manage warp locations.
 */
public class WarpManager extends NamedInsensitiveDataManager<IWarp> {

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public WarpManager (IDataNode dataNode) {
        super(dataNode);
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
    public IWarp add(String name, Location location) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(location);

        if (contains(name))
            return null;

        IDataNode warpNode = getNode(name);
        if (warpNode == null)
            return null;

        IWarp warp = createWarp(name, location, warpNode);
        if (warp == null)
            return null;

        add(warp);

        return warp;
    }

    /**
     * Called to create a new warp instance.
     *
     * @param name      The name of the warp.
     * @param location  The warp location.
     * @param dataNode  The data node to store the warp.
     */
    protected IWarp createWarp(String name, Location location, IDataNode dataNode) {
        return new Warp(name, location, dataNode);
    }

    @Nullable
    @Override
    protected IWarp load(String name, IDataNode warpNode) {

        Location location = warpNode.getLocation("");
        if (location == null)
            return null;

        IWarp warp = createWarp(name, location, warpNode);
        if (warp == null)
            return null;

        return warp;
    }

    @Nullable
    @Override
    protected void save(IWarp item, IDataNode itemNode) {
        // do nothing
    }
}
