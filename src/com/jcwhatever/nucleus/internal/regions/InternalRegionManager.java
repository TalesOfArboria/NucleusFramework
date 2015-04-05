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


package com.jcwhatever.nucleus.internal.regions;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.regions.IGlobalRegionManager;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.regions.ReadOnlyRegion;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Global Region Manager.
 *
 * <p>Tracks instances of {@link IRegion} and provides methods to determine which regions
 * a player is in as well as track players to determine when they enter and leave
 * player watcher regions.</p>
 *
 * <p>Methods that return {@link IRegion} instances are returning
 * {@link com.jcwhatever.nucleus.regions.ReadOnlyRegion} instances. This is to prevent
 * inter-plugin conflicts caused by changes to a region that the region owning plugin is
 * unaware of.</p>
 *
 * <p>Methods that return a specific region type return the actual region instance.</p>
 */
public final class InternalRegionManager extends RegionTypeManager<IRegion> implements IGlobalRegionManager {

    public static final MetaKey<IRegion> REGION_HANDLE = new MetaKey<IRegion>(IRegion.class);

    // worlds that have regions
    private final ElementCounter<World> _listenerWorlds = new ElementCounter<>(RemovalPolicy.REMOVE);

    // store managers for individual region types.
    private final Map<Class<? extends IRegion>, RegionTypeManager<?>> _managers = new HashMap<>(15);

    // store regions by lookup name. lookup name is: PluginName:RegionName
    private final Multimap<String, IRegion> _regionNameMap =
            MultimapBuilder.hashKeys(35).hashSetValues(5).build();

    // watch region and players to detect when players enter/leave regions
    private final InternalPlayerWatcher _playerWatcher = new InternalPlayerWatcher(this);

    /**
     * Constructor
     *
     * @param plugin  The owning plugin.
     */
    public InternalRegionManager(Plugin plugin) {
        super(IRegion.class);

        if (Nucleus.getPlugin() != plugin) {
            throw new RuntimeException("InternalRegionManager should not be instantiated.");
        }
    }

    /**
     * Get the internal player watcher to notify it of changes
     * in the players position.
     */
    public InternalPlayerWatcher getPlayerWatcher() {
        return _playerWatcher;
    }

    @Override
    public <T extends IRegion> boolean hasRegion(Location location, Class<T> regionClass) {
        RegionTypeManager<T> manager = getManager(regionClass, false);
        return manager != null && manager.hasRegion(location);
    }

    @Override
    public <T extends IRegion> boolean hasRegion(World world, int x, int y, int z, Class<T> regionClass) {
        RegionTypeManager<T> manager = getManager(regionClass, false);
        return manager != null && manager.hasRegion(world, x, y, z);
    }

    @Nullable
    @Override
    public List<IRegion> getRegions(Plugin plugin, String name) {
        return new ArrayList<>(_regionNameMap.get(getLookupName(plugin, name)));
    }

    @Override
    public <T extends IRegion> List<T> getRegions(Location location, Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegions(location);
    }

    @Override
    public <T extends IRegion> List<T> getRegions(World world, int x, int y, int z, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegions(world, x, y, z);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(Location location, Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(location);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z,
                                                          Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(world, x, y, z);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(Location location, PriorityType priorityType,
                                                          Class<T> regionClass) {
        PreCon.notNull(location);
        PreCon.notNull(priorityType);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(location, priorityType);
    }

    @Override
    public <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z,
                                                          PriorityType priorityType, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(priorityType);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getListenerRegions(world, x, y, z, priorityType);
    }

    @Override
    public <T extends IRegion> List<T> getRegionsInChunk(Chunk chunk, Class<T> regionClass) {
        PreCon.notNull(chunk);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegionsInChunk(chunk);
    }

    @Override
    public <T extends IRegion> List<T> getRegionsInChunk(World world, int x, int z, Class<T> regionClass) {
        PreCon.notNull(world);
        PreCon.notNull(regionClass);

        RegionTypeManager<T> manager = getManager(regionClass, false);
        if (manager == null)
            return CollectionUtils.unmodifiableList();

        return manager.getRegionsInChunk(world, x, z);
    }

    @Override
    public List<IRegion> getPlayerRegions(Player player) {
        PreCon.notNull(player);

        synchronized(_playerWatcher) {
            Set<IRegion> regions = _playerWatcher.getCurrentRegions(player.getUniqueId());
            if (regions == null)
                return new ArrayList<>(0);

            return new ArrayList<>(regions);
        }
    }

    @Override
    public void forgetPlayer(Player p, IRegion region) {
        PreCon.notNull(p);
        PreCon.notNull(region);

        synchronized(_playerWatcher) {

            Set<IRegion> regions = _playerWatcher.getCurrentRegions(p.getUniqueId());
            if (regions == null)
                return;

            regions.remove(new ReadOnlyRegion(region));
        }
    }

    @Override
    public void register(IRegion region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded()) {
            NucMsg.debug("Failed to register region '{0}' with RegionManager because " +
                    "it's coords are undefined. Region Type: {1}", region.getName(), region.getClass().getName());
            return;
        }

        if (region instanceof ReadOnlyRegion) {
            region = region.getMeta().get(REGION_HANDLE);

            if (region == null) {
                throw new RuntimeException("ReadOnlyRegions handle has no meta reference to itself.");
            }
        }

        @SuppressWarnings("unchecked")
        RegionTypeManager<IRegion> manager = (RegionTypeManager<IRegion>)getManager(region.getClass(), true);
        assert manager != null;
        manager.register(region);

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);
        super.register(readOnlyRegion);

        _regionNameMap.put(getLookupName(region.getPlugin(), region), readOnlyRegion);
    }

    @Override
    public void unregister(IRegion region) {
        PreCon.notNull(region);

        if (!region.isDefined() || !region.isWorldLoaded())
            return;

        @SuppressWarnings("unchecked")
        RegionTypeManager<IRegion> manager = (RegionTypeManager<IRegion>)getManager(region.getClass(), false);
        if (manager != null) {
            manager.unregister(region);
        }

        ReadOnlyRegion readOnlyRegion = new ReadOnlyRegion(region);
        super.unregister(readOnlyRegion);

        _regionNameMap.remove(getLookupName(region.getPlugin(), region), readOnlyRegion);
    }

    @Override
    protected void onRegister(IRegion region, boolean isFormerListener) {
        if (region.isEventListener()) {
            //noinspection ConstantConditions
            _listenerWorlds.add(region.getWorld());
        }
        else if (isFormerListener){
            //noinspection ConstantConditions
            _listenerWorlds.subtract(region.getWorld());
        }
    }

    @Override
    protected void onUnregister(IRegion region) {
        if (region.isEventListener()) {
            //noinspection ConstantConditions
            _listenerWorlds.subtract(region.getWorld());
        }
    }

    /**
     * Get direct reference to region event listener world counter.
     *
     * <p>Contains the worlds that contain event listening regions and
     * the number of event listening regions in each of those worlds.</p>
     *
     */
    ElementCounter<World> getListenerWorlds() {
        return _listenerWorlds;
    }

    /*
     * Remove a region from a region map.
     */
    private <T extends Set<IRegion>> boolean removeFromMap(Map<String, T> map, String key, ReadOnlyRegion region) {
        Set<IRegion> regions = map.get(key);
        return regions != null && regions.remove(region);
    }

    /*
     * Get region manager for a specific region type.
     */
    @Nullable
    private <T extends IRegion> RegionTypeManager<T> getManager(Class<T> regionClass, boolean create) {

        @SuppressWarnings("unchecked")
        RegionTypeManager<T> manager = (RegionTypeManager<T>)_managers.get(regionClass);

        if (manager == null && create) {
            manager = new RegionTypeManager<>(regionClass);
            _managers.put(regionClass, manager);
        }

        return manager;
    }

    /*
     * Get a regions lookup name.
     */
    private String getLookupName(Plugin plugin, IRegion region) {
        return plugin.getName() + ':' + region.getSearchName();
    }

    /*
     * Get a regions lookup name.
     */
    private String getLookupName(Plugin plugin, String name) {
        return plugin.getName() + ':' + name.toLowerCase();
    }
}
