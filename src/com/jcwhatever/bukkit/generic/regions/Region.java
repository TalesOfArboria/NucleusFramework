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

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.bukkit.regions.RegionOwnerChangedEvent;
import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.regions.data.RegionMath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

public abstract class Region extends RegionMath implements IRegion, IDisposable {

    private static final Map<Region, Void> _instances = new WeakHashMap<>(100);
    private static BukkitListener _bukkitListener;

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private IDataNode _dataNode;
    private RegionPriority _enterPriority = RegionPriority.DEFAULT;
    private RegionPriority _leavePriority = RegionPriority.DEFAULT;

    private boolean _isPlayerWatcher = false;
    private String _worldName;
    private UUID _ownerId;
    private List<Chunk> _chunks;
    private Map<Object, Object> _meta = new HashMap<Object, Object>(30);
    private List<IRegionEventHandler> _eventHandlers = new ArrayList<>(10);

    /**
     * The priority/order the region is handled by the
     * region manager in relation to other regions.
     */
    public enum RegionPriority {
        /**
         * The last to be handled.
         */
        LAST      (4),
        /**
         * Low priority. Handled second to last.
         */
        LOW       (3),
        /**
         * Normal priority.
         */
        DEFAULT   (2),
        /**
         * High priority. Handled second.
         */
        HIGH      (1),
        /**
         * Highest priority. Handled first.
         */
        FIRST     (0);

        private final int _order;

        RegionPriority(int order) {
            _order = order;
        }

        /**
         * Get a sort order index number.
         */
        public int getSortOrder() {
            return _order;
        }
    }

    /**
     * Type of region priority.
     */
    public enum PriorityType {
        ENTER,
        LEAVE
    }

    /**
     * Reasons a player enters a region.
     */
    public enum EnterRegionReason {
        /**
         * The player moved into the region.
         */
        MOVE,
        /**
         * The player teleported into the region.
         */
        TELEPORT,
        /**
         * The player respawned into the region.
         */
        RESPAWN,
        /**
         * The player joined the server and
         * spawned into the region.
         */
        JOIN_SERVER
    }

    /**
     * Reasons a player leaves a region.
     */
    public enum LeaveRegionReason {
        /**
         * The player moved out of the region.
         */
        MOVE,
        /**
         * The player teleported out of the region.
         */
        TELEPORT,
        /**
         * The player died in the region.
         */
        DEAD,
        /**
         * The player left the server while
         * in the region.
         */
        QUIT_SERVER
    }

    /**
     * For internal use.
     */
    public enum RegionReason {
        MOVE        (EnterRegionReason.MOVE,        LeaveRegionReason.MOVE),
        DEAD        (null,                          LeaveRegionReason.DEAD),
        TELEPORT    (EnterRegionReason.TELEPORT,    LeaveRegionReason.TELEPORT),
        RESPAWN     (EnterRegionReason.RESPAWN,     LeaveRegionReason.DEAD),
        JOIN_SERVER (EnterRegionReason.JOIN_SERVER, null),
        QUIT_SERVER (null,                          LeaveRegionReason.QUIT_SERVER);

        private final EnterRegionReason _enter;
        private final LeaveRegionReason _leave;

        RegionReason(EnterRegionReason enter, LeaveRegionReason leave) {
            _enter = enter;
            _leave = leave;
        }

        /**
         * Get the enter reason equivalent.
         */
        @Nullable
        public EnterRegionReason getEnterReason() {
            return _enter;
        }

        /**
         * Get the leave reason equivalent.
         */
        @Nullable
        public LeaveRegionReason getLeaveReason() {
            return _leave;
        }
    }

    /**
     * Constructor
     */
    public Region(Plugin plugin, String name, @Nullable IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

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
            Bukkit.getPluginManager().registerEvents(_bukkitListener, GenericsLib.getLib());
        }
    }

    /**
     * Get the name of the region.
     */
    @Override
    public final String getName() {
        return _name;
    }

    /**
     * Get the name of the region in lower case.
     */
    @Override
    public final String getSearchName() {
        return _searchName;
    }

    /**
     * Get the owning plugin.
     */
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

    /**
     * Get the regions priority when handling player
     * entering region.
     */
    @Override
    public RegionPriority getPriority(PriorityType priorityType) {
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

    /**
     * Get the name of the world the region is in.
     */
    @Nullable
    public final String getWorldName() {
        return _worldName;
    }

    /**
     * Determine if the world the region is in is loaded.
     */
    public final boolean isWorldLoaded() {
        if (!isDefined())
            return false;

        if (getWorld() == null)
            return false;

        World world = Bukkit.getWorld(getWorld().getName());

        return getWorld().equals(world);
    }


    /**
     * Used to determine if the region subscribes to player events.
     */
    @Override
    public final boolean isPlayerWatcher() {
        return _isPlayerWatcher || !_eventHandlers.isEmpty();
    }

    /**
     * Get the id of the region owner.
     */
    @Override
    @Nullable
    public UUID getOwnerId() {
        return _ownerId;
    }

    /**
     * Determine if the region has an owner.
     */
    @Override
    public boolean hasOwner() {
        return _ownerId != null;
    }

    /**
     * Set the regions owner.
     *
     * @param ownerId  The id of the new owner.
     */
    @Override
    public boolean setOwner(@Nullable UUID ownerId) {

        UUID oldId = _ownerId;

        RegionOwnerChangedEvent event = RegionOwnerChangedEvent.callEvent(new ReadOnlyRegion(this), oldId, ownerId);

        if (event.isCancelled())
            return false;

        _ownerId = ownerId;

        IDataNode dataNode = getDataNode();
        if (dataNode != null) {
            dataNode.set("owner-id", ownerId);
            dataNode.saveAsync(null);
        }

        onOwnerChanged(oldId, ownerId);

        return true;
    }

    /**
     * Set the regions cuboid point coordinates.
     *
     * <p>Saves to the regions data node if it has one.</p>
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    @Override
    public final void setCoords(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        // unregister while math is updated,
        // is re-registered after math update (see UpdateMath)
        GenericsLib.getRegionManager().unregister(this);

        super.setCoords(p1, p2);
        updateWorld();
        _chunks = null;

        IDataNode dataNode = getDataNode();
        if (dataNode != null) {
            dataNode.set("p1", getP1());
            dataNode.set("p2", getP2());
            dataNode.saveAsync(null);
        }

        onCoordsChanged(getP1(), getP2());
    }

    /**
     * Add a transient region event handler.
     *
     * @param handler  The handler to add.
     */
    @Override
    public boolean addEventHandler(IRegionEventHandler handler) {
        PreCon.notNull(handler);

        boolean isFirstHandler = _eventHandlers.isEmpty();

        if (_eventHandlers.add(handler)) {
            if (isFirstHandler) {
                // update registration
                GenericsLib.getRegionManager().register(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Remove a transient event handler.
     *
     * @param handler  The handler to remove.
     */
    @Override
    public boolean removeEventHandler(IRegionEventHandler handler) {
        PreCon.notNull(handler);

        if (_eventHandlers.remove(handler)) {

            if (_eventHandlers.isEmpty()) {
                // update registration
                GenericsLib.getRegionManager().register(this);
            }
            return true;
        }
        return false;
    }

    /**
     * Determine if the region contains the specified material.
     *
     * @param material  The material to search for.
     */
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

    /**
     * Determine if the region contains the specified location.
     *
     * @param loc  The location to check.
     */
    @Override
    public final boolean contains(Location loc) {

        if (!isDefined())
            return false;

        if (loc.getWorld() == null)
            return false;

        if (!loc.getWorld().equals(getWorld()))
            return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return contains(x, y, z);
    }

    /**
     * Determine if the region contains the specified coordinates.
     *
     * @param x      The location X coordinates.
     * @param y      The location Y coordinates.
     * @param z      The location Z coordinates.
     */
    public final boolean contains(int x, int y, int z) {
        synchronized (_sync) {

            _sync.notifyAll();

            return x >= getXStart() && x <= getXEnd() &&
                    y >= getYStart() && y <= getYEnd() &&
                    z >= getZStart() && z <= getZEnd();
        }
    }

    /**
     * Determine if the region contains the the specified location
     * on the specified axis.
     *
     * @param loc  The location to check.
     * @param cx   True to check if the point is inside the region on the X axis.
     * @param cy   True to check if the point is inside the region on the Y axis.
     * @param cz   True to check if the point is inside the region on the Z axis.
     */
    @Override
    public final boolean contains(Location loc, boolean cx, boolean cy, boolean cz) {

        if (!isDefined())
            return false;

        synchronized (_sync) {

            if (!loc.getWorld().equals(getWorld()))
                return false;

            if (cx) {
                int x = loc.getBlockX();
                if (x < getXStart() || x > getXEnd())
                    return false;
            }

            if (cy) {
                int y = loc.getBlockY();
                if (y < getYStart() || y > getYEnd())
                    return false;
            }

            if (cz) {
                int z = loc.getBlockZ();
                if (z < getZStart() || z > getZEnd())
                    return false;
            }

            _sync.notifyAll();

            return true;
        }
    }

    /**
     * Get all locations that have a block of the specified material
     * within the region.
     *
     * @param material  The material to search for.
     */
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

            _sync.notifyAll();

            return results;
        }
    }

    /**
     * Determine if the region intersects with the chunk specified.
     *
     * @param chunk  The chunk.
     */
    public final boolean intersects(Chunk chunk) {
        PreCon.notNull(chunk);

        return isDefined() &&
                chunk.getWorld().equals(getWorld()) &&
                intersects(chunk.getX(), chunk.getZ());
    }

    /**
     * Determine if the region intersects with the chunk specified.
     *
     * @param chunkX  The chunk X coordinates.
     * @param chunkZ  The chunk Z coordinates.
     */
    public final boolean intersects(int chunkX, int chunkZ) {

        return getChunkX() <= chunkX && (getChunkX() + getChunkXWidth() - 1) >= chunkX &&
                getChunkZ() <= chunkZ && (getChunkZ() + getChunkZWidth() - 1) >= chunkZ;
    }

    /**
     * Get all chunks that contain at least a portion of the region.
     */
    @Override
    public final List<Chunk> getChunks() {
        if (getWorld() == null)
            return new ArrayList<>(0);

        synchronized (_sync) {
            if (_chunks == null) {

                if (!isDefined()) {
                    return new ArrayList<>(0);
                }

                Chunk c1 = getWorld().getChunkAt(getP1());
                Chunk c2 = getWorld().getChunkAt(getP2());

                int startX = Math.min(c1.getX(), c2.getX());
                int endX = Math.max(c1.getX(), c2.getX());

                int startZ = Math.min(c1.getZ(), c2.getZ());
                int endZ = Math.max(c1.getZ(), c2.getZ());

                ArrayList<Chunk> result = new ArrayList<Chunk>((endX - startX) * (endZ - startZ));

                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; z <= endZ; z++) {
                        result.add(getWorld().getChunkAt(x, z));
                    }
                }
                _chunks = result;
            }

            _sync.notifyAll();

            return new ArrayList<Chunk>(_chunks);
        }
    }

    /**
     * Refresh all chunks the region is in.
     */
    @Override
    public final void refreshChunks() {
        World world = getWorld();

        if (world == null)
            return;

        List<Chunk> chunks = getChunks();

        for (Chunk chunk : chunks) {
            world.refreshChunk(chunk.getX(), chunk.getZ());
        }
    }

    /**
     * Remove entities from the region.
     *
     * @param entityTypes  The entity types to remove.
     */
    @Override
    public final void removeEntities(Class<?>... entityTypes) {

        synchronized (_sync) {
            List<Chunk> chunks = getChunks();
            for (Chunk chunk : chunks) {
                for (Entity entity : chunk.getEntities()) {
                    if (this.contains(entity.getLocation())) {

                        if (entityTypes == null || entityTypes.length == 0) {
                            entity.remove();
                            continue;
                        }

                        for (Class<?> itemType : entityTypes) {
                            if (itemType.isInstance(entity)) {
                                entity.remove();
                                break;
                            }
                        }
                    }
                }
            }
            _sync.notifyAll();
        }
    }

    /**
     * Get a meta object from the region.
     *
     * @param key  The meta key.
     *
     * @param <T>  The object type.
     */
    @Override
    public <T> T getMeta(Object key) {
        @SuppressWarnings("unchecked") T item = (T)_meta.get(key);
        return item;
    }

    /**
     * Set a meta object value in the region.
     *
     * @param key    The meta key.
     * @param value  The meta value.
     */
    @Override
    public void setMeta(Object key, @Nullable Object value) {
        if (value == null) {
            _meta.remove(key);
            return;
        }

        _meta.put(key, value);
    }

    /**
     * Get the class of the region.
     */
    @Override
    public Class<? extends IRegion> getRegionClass() {
        return getClass();
    }

    /**
     * Dispose the region by releasing resources and
     * un-registering it from the central region manager.
     */
    @Override
    public final void dispose() {
        GenericsLib.getRegionManager().unregister(this);
        _instances.remove(this);

        onDispose();
    }

    /**
     * Set the value of the player watcher flag and update
     * the regions registration with the central region manager.
     *
     * @param isPlayerWatcher  True to allow player enter and leave events.
     */
    protected void setIsPlayerWatcher(boolean isPlayerWatcher) {
        if (isPlayerWatcher != _isPlayerWatcher) {
            _isPlayerWatcher = isPlayerWatcher;

            GenericsLib.getRegionManager().register(this);
        }
    }

    /**
     * Causes the onPlayerEnter method to re-fire
     * if the player is already in the region.
     * .
     * @param p  The player to reset.
     */
    protected void resetContainsPlayer(Player p) {
        GenericsLib.getRegionManager().resetPlayerRegion(p, this);
    }

    /**
     * Initializes region coordinates without saving to the data node.
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    protected final void initCoords(@Nullable Location p1, @Nullable Location p2) {
        setPoint(RegionPoint.P1, p1);
        setPoint(RegionPoint.P2, p2);

        updateWorld();
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
        _enterPriority = dataNode.getEnum("region-enter-priority", _enterPriority, RegionPriority.class);
        _leavePriority = dataNode.getEnum("region-leave-priority", _leavePriority, RegionPriority.class);
    }

    /*
    * Update region math variables
    */
    @Override
    protected void updateMath() {

        super.updateMath();

        GenericsLib.getRegionManager().register(this);
    }

    /**
     * Called when the coordinates for the region are changed
     *
     * <p>Intended for implementation use.</p>
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
     * Called when a player enters the region,
     * but only if the region is a player watcher and 
     * canDoPlayerEnter() returns true.
     *
     * <p>Intended for implementation use.</p>
     *
     * @param p  the player entering the region.
     */
    protected void onPlayerEnter (@SuppressWarnings("unused") Player p,
                                  @SuppressWarnings("unused") EnterRegionReason reason) {
        // do nothing
    }

    /**
     * Called when a player leaves the region,
     * but only if the region is a player watcher and 
     * canDoPlayerLeave() returns true.
     *
     * <p>Intended for implementation use.</p>
     *
     * @param p  the player leaving the region.
     */
    protected void onPlayerLeave (@SuppressWarnings("unused") Player p,
                                  @SuppressWarnings("unused") LeaveRegionReason reason) {
        // do nothing
    }

    /**
     * Called to determine if {@code onPlayerEnter}
     * can be called on the specified player.
     *
     * <p>Intended for override if needed.</p>
     *
     * @param p  The player entering the region.
     */
    protected boolean canDoPlayerEnter(@SuppressWarnings("unused") Player p,
                                       @SuppressWarnings("unused") EnterRegionReason reason) {
        return true;
    }

    /**
     * Called to determine if {@code onPlayerLeave}
     * can be called on the specified player.
     *
     * <p>Intended for override if needed.</p>
     *
     * @param p  The player leaving the region.
     */
    protected boolean canDoPlayerLeave(@SuppressWarnings("unused") Player p,
                                       @SuppressWarnings("unused") LeaveRegionReason reason) {
        return true;
    }

    /**
     * Called when the owner of the region is changed.
     *
     * <p>Intended for implementation use.</p>
     *
     * @param oldOwnerId  The id of the previous owner of the region.
     * @param newOwnerId  The id of the new owner of the region.
     */
    protected void onOwnerChanged(@SuppressWarnings("unused") @Nullable UUID oldOwnerId,
                                  @SuppressWarnings("unused") @Nullable UUID newOwnerId) {
        // do nothing
    }

    /**
     * Called when the region is disposed.
     */
    protected void onDispose() {
        // do nothings
    }

    /**
     * Used by RegionEventManager to execute onPlayerEnter event.
     */
    void doPlayerEnter (Player p, EnterRegionReason reason) {

        if (canDoPlayerEnter(p, reason))
            onPlayerEnter(p, reason);

        for (IRegionEventHandler handler : _eventHandlers) {
            if (handler.canDoPlayerEnter(p, reason)) {
                handler.onPlayerEnter(p, reason);
            }
        }
    }

    /**
     * Used by RegionEventManager to execute onPlayerLeave event.
     */
    void doPlayerLeave (Player p, LeaveRegionReason reason) {

        if (canDoPlayerLeave(p, reason))
            onPlayerLeave(p, reason);

        for (IRegionEventHandler handler : _eventHandlers) {
            if (handler.canDoPlayerLeave(p, reason)) {
                handler.onPlayerLeave(p, reason);
            }
        }
    }

    /*
     * Update world name if possible
     */
    private void updateWorld() {

        Location p1 = getP1();
        Location p2 = getP2();

        if (p1 == null || p2 == null) {
            _worldName = null;
            return;
        }

        World p1World = p1.getWorld();
        World p2World = p2.getWorld();

        boolean isWorldsMismatched = p1World != null && !p1World.equals(p2World) ||
                p2World != null && !p2World.equals(p1World);

        if (isWorldsMismatched) {
            throw new IllegalArgumentException("Both region points must be from the same world.");
        }

        if (p1World != null) {
            _worldName = p1World.getName();
        }
        else {

            IDataNode dataNode = getDataNode();

            if (dataNode != null) {
                _worldName = dataNode.getLocationWorldName("p1");
            }
        }
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        synchronized (_sync) {
            if (obj instanceof Region) {

                Region region = (Region)obj;
                return region == this;
            }

            _sync.notifyAll();
            return false;
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

                    region._chunks = null;
                }
            }
        }
    }
}
