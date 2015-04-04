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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.regions.RegionOwnerChangedEvent;
import com.jcwhatever.nucleus.internal.regions.InternalRegionManager;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority;
import com.jcwhatever.nucleus.regions.options.RegionEventPriority.PriorityType;
import com.jcwhatever.nucleus.regions.selection.RegionSelection;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.IChunkCoords;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Abstract implementation of a region.
 *
 * <p>The region is registered with NucleusFramework's {@link IGlobalRegionManager} as
 * soon as it is defined (P1 and P2 coordinates set) via the regions settings or by
 * invoking the {@link #setCoords} method.</p>
 *
 * <p>The regions protected methods {@link #onPlayerEnter} and {@link #onPlayerLeave}
 * are only invoked by the global region manager if the implementing type invokes
 * {@link #setEventListener(boolean)} with a 'true' argument.</p>
 */
public abstract class Region extends RegionSelection implements IRegion {

    private static final Map<Region, Void> _instances = new WeakHashMap<>(100);
    private static BukkitListener _bukkitListener;

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private final IDataNode _dataNode;
    private final IRegionEventListener _eventListener;
    private final MetaStore _meta = new MetaStore();

    private RegionEventPriority _enterPriority = RegionEventPriority.DEFAULT;
    private RegionEventPriority _leavePriority = RegionEventPriority.DEFAULT;

    private boolean _isEventListener;
    private boolean _isDisposed;
    private UUID _ownerId;
    private List<IRegionEventHandler> _eventHandlers = new ArrayList<>(10);

    /**
     * Constructor
     */
    public Region(Plugin plugin, String name, @Nullable IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _meta.set(InternalRegionManager.REGION_HANDLE, this);
        _eventListener = new RegionListener(this);

        _name = name;
        _plugin = plugin;
        _searchName = name.toLowerCase();
        _dataNode = dataNode;

        RegionPriorityInfo info = this.getClass().getAnnotation(RegionPriorityInfo.class);
        if (info != null) {
            _enterPriority = info.enter();
            _leavePriority = info.leave();
        }

        if (dataNode != null) {
            loadSettings(dataNode);
        }

        _instances.put(this, null);

        if (_bukkitListener == null) {
            _bukkitListener = new BukkitListener();
            Bukkit.getPluginManager().registerEvents(_bukkitListener, Nucleus.getPlugin());
        }
    }

    @Override
    public final String getName() {
        return _name;
    }

    @Override
    public final String getSearchName() {
        return _searchName;
    }

    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the regions data node.
     */
    @Nullable
    public IDataNode getDataNode() {
        return _dataNode;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public RegionEventPriority getEventPriority(PriorityType priorityType) {
        PreCon.notNull(priorityType);

        switch (priorityType) {
            case ENTER:
                return _enterPriority;
            case LEAVE:
                return _leavePriority;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public final boolean isEventListener() {
        return _isEventListener || !_eventHandlers.isEmpty();
    }

    @Override
    public final IRegionEventListener getEventListener() {
        return _eventListener;
    }

    @Override
    @Nullable
    public final UUID getOwnerId() {
        return _ownerId;
    }

    @Override
    public final boolean hasOwner() {
        return _ownerId != null;
    }

    @Override
    public final boolean setOwner(@Nullable UUID ownerId) {

        UUID oldId = _ownerId;

        RegionOwnerChangedEvent event = new RegionOwnerChangedEvent(new ReadOnlyRegion(this), oldId, ownerId);
        Nucleus.getEventManager().callBukkit(this, event);

        if (event.isCancelled())
            return false;

        if (!onOwnerChanged(oldId, ownerId)) {
            return false;
        }

        _ownerId = ownerId;

        IDataNode dataNode = getDataNode();
        if (dataNode != null) {
            dataNode.set("owner-id", ownerId);
            dataNode.save();
        }

        return true;
    }

    @Override
    public final void setCoords(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        // unregister while math is updated,
        // is re-registered after math update (see UpdateMath)
        regionManager().unregister(this);

        super.setCoords(p1, p2);

        IDataNode dataNode = getDataNode();
        if (dataNode != null) {
            dataNode.set("p1", getP1());
            dataNode.set("p2", getP2());
            dataNode.save();
        }

        onCoordsChanged(getP1(), getP2());
    }

    @Override
    public MetaStore getMeta() {
        return _meta;
    }

    @Override
    public boolean addEventHandler(IRegionEventHandler handler) {
        PreCon.notNull(handler);

        boolean isFirstHandler = _eventHandlers.isEmpty();

        if (_eventHandlers.add(handler)) {
            if (isFirstHandler) {
                // update registration
                regionManager().register(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEventHandler(IRegionEventHandler handler) {
        PreCon.notNull(handler);

        if (_eventHandlers.remove(handler)) {

            if (_eventHandlers.isEmpty()) {
                // update registration
                regionManager().register(this);
            }
            return true;
        }
        return false;
    }

    @Override
    public final boolean contains(Material material) {

        synchronized (_sync) {

            if (getWorld() == null)
                return false;

            int xlen = getXEnd();
            int ylen = getYEnd();
            int zlen = getZEnd();

            for (int x = getXStart(); x <= xlen; x++) {

                for (int y = getYStart(); y <= ylen; y++) {

                    for (int z = getZStart(); z <= zlen; z++) {

                        Block block = getWorld().getBlockAt(x, y, z);
                        if (block.getType() != material)
                            continue;

                        return true;
                    }
                }
            }

            _sync.notifyAll();

            return false;
        }
    }

    @Override
    public final LinkedList<Location> find(Material material) {

        synchronized (_sync) {
            LinkedList<Location> results = new LinkedList<>();

            if (getWorld() == null)
                return results;

            int xlen = getXEnd();
            int ylen = getYEnd();
            int zlen = getZEnd();

            for (int x = getXStart(); x <= xlen; x++) {

                for (int y = getYStart(); y <= ylen; y++) {

                    for (int z = getZStart(); z <= zlen; z++) {

                        Block block = getWorld().getBlockAt(x, y, z);
                        if (block.getType() != material)
                            continue;

                        results.add(block.getLocation());
                    }
                }
            }

            return results;
        }
    }

    @Override
    public final void refreshChunks() {
        World world = getWorld();

        if (world == null)
            return;

        Collection<IChunkCoords> chunks = getChunkCoords();

        for (IChunkCoords chunk : chunks) {
            world.refreshChunk(chunk.getX(), chunk.getZ());
        }
    }

    @Override
    public final void removeEntities(Class<?>... entityTypes) {

        synchronized (_sync) {
            Collection<IChunkCoords> chunks = getChunkCoords();
            for (IChunkCoords chunkInfo : chunks) {

                Chunk chunk = chunkInfo.getChunk();
                if (chunk == null)
                    return;

                for (Entity entity : chunk.getEntities()) {

                    if (contains(entity.getLocation())) {

                        if (entityTypes == null || entityTypes.length == 0) {
                            entity.remove();
                            continue;
                        }

                        for (Class<?> itemType : entityTypes) {

                            if (!itemType.isInstance(entity))
                                continue;

                            entity.remove();
                            break;
                        }
                    }
                }

            }
        }
    }

    @Override
    public Class<? extends IRegion> getRegionClass() {
        return getClass();
    }

    @Override
    public final boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public final void dispose() {
        regionManager().unregister(this);
        _instances.remove(this);

        onDispose();

        _isDisposed = true;
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        synchronized (_sync) {
            return this == obj;
        }
    }

    /**
     * Set the value of the event watcher flag and update the regions registration
     * with the global region manager.
     *
     * @param isEventListener  True to allow player enter and leave events. If entry/exit events
     *                         are not used, false.
     */
    protected void setEventListener(boolean isEventListener) {
        if (isEventListener != _isEventListener) {
            _isEventListener = isEventListener;

            regionManager().register(this);
        }
    }

    /**
     * Causes the onPlayerEnter method to re-fire if the player is already in the region.
     *
     * @param player  The player to forget.
     */
    protected void forgetPlayer(Player player) {
        regionManager().forgetPlayer(player, this);
    }

    /**
     * Initializes region coordinates without saving to the data node.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    protected final void initCoords(@Nullable Location p1, @Nullable Location p2) {
        if (p1 != null && p2 != null) {
            super.setCoords(p1, p2);
        }
        updateMath();
    }

    /**
     * Initial load of settings from regions data node.
     *
     * @param dataNode  The data node to load from
     */
    protected void loadSettings(final IDataNode dataNode) {

        Location p1 = dataNode.getLocation("p1");
        Location p2 = dataNode.getLocation("p2");

        initCoords(p1, p2);

        _ownerId = dataNode.getUUID("owner-id");
        _enterPriority = dataNode.getEnum("region-enter-priority", _enterPriority, RegionEventPriority.class);
        _leavePriority = dataNode.getEnum("region-leave-priority", _leavePriority, RegionEventPriority.class);
    }

    /*
    * Update region math variables.
    */
    @Override
    protected void updateMath() {

        super.updateMath();

        regionManager().register(this);
    }

    /**
     * Invoked when the coordinates for the region are changed.
     *
     * <p>Intended for optional override.</p>
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     *
     * @throws IOException
     */
    protected void onCoordsChanged(@Nullable Location p1, @Nullable Location p2) {
        // do nothing
    }

    /**
     * Invoked when a player enters the region, but only if the region is an event watcher and
     * {@link #canDoPlayerEnter} returns true when invoked by the global region manager.
     *
     * <p>Intended for optional override.</p>
     *
     * @param player  The player entering the region.
     */
    protected void onPlayerEnter (Player player, EnterRegionReason reason) {
        // do nothing
    }

    /**
     * Invoked when a player leaves the region, but only if the region is an event watcher and
     * {@link #canDoPlayerLeave} returns true when invoked by the global region manager.
     *
     * <p>Intended for optional override.</p>
     *
     * @param player  The player leaving the region.
     */
    protected void onPlayerLeave (Player player, LeaveRegionReason reason) {
        // do nothing
    }

    /**
     * Invoked to determine if {@link #onPlayerEnter} can be invoked on the specified player
     * for the specified reason.
     *
     * <p>Normally returns true.</p>
     *
     * <p>Intended for optional override.</p>
     *
     * @param player  The player entering the region.
     *
     * @return  True to allow further handling. Normally returns true unless overridden.
     */
    protected boolean canDoPlayerEnter(Player player, EnterRegionReason reason) {
        return true;
    }

    /**
     * Invoked to determine if {@link #onPlayerLeave} can be invoked on the specified player
     * for the specified reason.
     *
     * <p>Intended for optional override.</p>
     *
     * @param player  The player leaving the region.
     *
     * @return  True to allow further handling. Normally returns true unless overridden.
     */
    protected boolean canDoPlayerLeave(Player player, LeaveRegionReason reason) {
        return true;
    }

    /**
     * Invoked when the owner of the region is changed.
     *
     * <p>Note that other plugins can easily change the region
     * owner value. If the owner is important, this should be used.</p>
     *
     * <p>Intended for optional override.</p>
     *
     * @param oldOwnerId  The ID of the previous owner of the region.
     * @param newOwnerId  The ID of the new owner of the region.
     *
     * @return  True to allow the owner change. Normally returns true unless overridden.
     */
    protected boolean onOwnerChanged(@Nullable UUID oldOwnerId, @Nullable UUID newOwnerId) {
        return true;
    }

    /**
     * Invoked when the region is disposed.
     *
     * <p>Intended for optional override.</p>
     */
    protected void onDispose() {
        // do nothings
    }

    // helper method to get internal region manager.
    private InternalRegionManager regionManager() {
        return (InternalRegionManager) Nucleus.getRegionManager();
    }

    /*
     * private implementation of IRegionEventListener
     */
    private static class RegionListener implements IRegionEventListener {

        final Region _region;

        RegionListener(Region region) {
            _region = region;
        }

        @Override
        public void onPlayerEnter(Player player, EnterRegionReason reason) {

            if (_region.canDoPlayerEnter(player, reason))
                onPlayerEnter(player, reason);

            for (IRegionEventHandler handler : _region._eventHandlers) {
                if (handler.canDoPlayerEnter(player, reason)) {
                    handler.onPlayerEnter(player, reason);
                }
            }
        }

        @Override
        public void onPlayerLeave(Player player, LeaveRegionReason reason) {

            if (_region.canDoPlayerLeave(player, reason))
                onPlayerLeave(player, reason);

            for (IRegionEventHandler handler : _region._eventHandlers) {
                if (handler.canDoPlayerLeave(player, reason)) {
                    handler.onPlayerLeave(player, reason);
                }
            }
        }
    }

    /*
     * Listens for world unload and load events and handles
     * regions in the world appropriately.
     */
    private static class BukkitListener implements Listener {

        @EventHandler
        private void onWorldLoad(WorldLoadEvent event) {

            String worldName = event.getWorld().getName();
            for (Region region : _instances.keySet()) {
                if (region.isDefined() && worldName.equals(region.getWorldName())) {
                    // fix locations

                    //noinspection ConstantConditions
                    region.getP1().setWorld(event.getWorld());

                    //noinspection ConstantConditions
                    region.getP2().setWorld(event.getWorld());
                }
            }
        }

        @EventHandler
        private void onWorldUnload(WorldUnloadEvent event) {

            String worldName = event.getWorld().getName();
            for (Region region : _instances.keySet()) {
                if (region.isDefined() && worldName.equals(region.getWorldName())) {
                    // remove world from locations, helps garbage collector

                    //noinspection ConstantConditions
                    region.getP1().setWorld(null);

                    //noinspection ConstantConditions
                    region.getP2().setWorld(null);
                }
            }
        }
    }
}
