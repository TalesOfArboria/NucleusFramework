package com.jcwhatever.bukkit.generic.warp;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.sun.istack.internal.Nullable;
import org.bukkit.Location;

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
