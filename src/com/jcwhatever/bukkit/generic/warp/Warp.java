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


package com.jcwhatever.bukkit.generic.warp;

import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import com.jcwhatever.bukkit.generic.mixins.INamedLocationDistance;
import com.jcwhatever.bukkit.generic.mixins.implemented.NamedLocationDistance;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;

/**
 * Represents a warp location.
 */
public class Warp implements INamedLocation {

    private final String _warpName;
    private final String _warpSearchName;
    private final IDataNode _dataNode;
    private Location _location;

    /**
     * Constructor.
     *
     * @param warpName  The name of the warp.
     * @param location  The warp location.
     * @param dataNode  The warp data node.
     */
    public Warp (String warpName, Location location, IDataNode dataNode) {
        _warpName = warpName;
        _warpSearchName = warpName.toLowerCase();
        _location = location;
        _dataNode = dataNode;
    }

    /**
     * Get the name of the warp.
     */
    @Override
    public String getName() {
        return _warpName;
    }

    /**
     * Get the name of the warp in lowercase.
     */
    @Override
    public String getSearchName() {
        return _warpSearchName;
    }

    /**
     * Get the warp location.
     */
    @Override
    public Location getLocation() {
        return _location;
    }

    /**
     * Get the distance from the warp to the specified location.
     *
     * @param location  The location to check.
     */
    @Override
    public INamedLocationDistance getDistance(Location location) {
        return new NamedLocationDistance(this, location);
    }

    /**
     * Set the warp location.
     *
     * @param location  The warp location.
     */
    public void setLocation(Location location) {
        PreCon.notNull(location);

        _location = location;
        _dataNode.set(_warpName, location);
        _dataNode.saveAsync(null);
    }
}