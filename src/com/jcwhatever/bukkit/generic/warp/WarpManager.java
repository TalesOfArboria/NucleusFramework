/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manage warp locations.
 */
public class WarpManager {

    private Map<String, Warp> _warpMap = new HashMap<String, Warp>(10);
    private IDataNode _settings;

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public WarpManager (IDataNode dataNode) {
        _settings = dataNode;

        loadSettings();
    }

    /**
     * Get a warp by name.
     *
     * @param name  The name of the warp.
     */
    @Nullable
    public Warp getWarp(String name) {
        return _warpMap.get(name.toLowerCase());
    }

    /**
     * Get all warps.
     */
    public List<Warp> getWarps() {
        return new ArrayList<Warp>(_warpMap.values());
    }

    /**
     * Set a warp location.
     *
     * @param name      The name of the warp.
     * @param location  The warp location.
     */
    public boolean setWarp(String name, Location location) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(location);

        Warp warp = getWarp(name);

        if (warp != null) {
            warp.setLocation(location);
        }
        else {
            warp = new Warp(name, location, _settings);
            _settings.set(name, location);
            _settings.saveAsync(null);
            _warpMap.put(warp.getSearchName(), warp);
        }
        return true;
    }

    /**
     * Delete a warp by name.
     *
     * @param name  The name of the warp.
     */
    public boolean deleteWarp(String name) {
        PreCon.notNullOrEmpty(name);

        Warp warp = getWarp(name);
        if (warp == null)
            return false;

        _warpMap.remove(warp.getSearchName());
        _settings.set(warp.getName(), null);
        _settings.saveAsync(null);
        return true;
    }

    // initial settings load
    private void loadSettings() {
        Set<String> warpNames = _settings.getSubNodeNames();
        if (warpNames == null)
            return;

        for (String warpName : warpNames) {
            Location location = _settings.getLocation(warpName);
            Warp warp = new Warp(warpName, location, _settings);
            _warpMap.put(warp.getSearchName(), warp);
        }
    }

}
