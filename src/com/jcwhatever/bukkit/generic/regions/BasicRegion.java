package com.jcwhatever.bukkit.generic.regions;

import org.bukkit.plugin.Plugin;

import com.jcwhatever.bukkit.generic.storage.IDataNode;

/**
 * A basic implementation of a region.
 */
public class BasicRegion extends Region {

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
    public BasicRegion(Plugin plugin, String name) {
        super(plugin, name);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param name      The name of the region.
     * @param settings  The regions data storage node.
     */
    public BasicRegion(Plugin plugin, String name, IDataNode settings) {
        super(plugin, name, settings);
    }
	
}
