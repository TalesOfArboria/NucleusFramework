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


package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.bukkit.regions.RegionOwnerChangedEvent;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Rand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public abstract class Region {

    private final Object _sync = new Object();

    protected String _name;
    protected String _searchName;
    protected IDataNode _dataNode;
    protected Plugin _plugin;

    private Location _p1;
    private Location _p2;

    protected String _p1Path = "p1";
    protected String _p2Path = "p2";

    private Location _lastP1;
    private Location _lastP2;

    private int _startX;
    private int _startY;
    private int _startZ;

    private int _endX;
    private int _endY;
    private int _endZ;

    private int _xWidth;
    private int _zWidth;
    private int _yHeight;
    private int _xBlockWidth;
    private int _zBlockWidth;
    private int _yBlockHeight;
    private long _volume;

    private List<Chunk> _chunks;

    private Location _center;
    private int _chunkX;
    private int _chunkZ;
    private int _chunkXWidth;
    private int _chunkZWidth;

    private boolean _isPlayerWatcher = false;

    private int _hash = -1;

    private Map<Object, Object> _meta = new HashMap<Object, Object>(30);

    private UUID _ownerId;

    private String _entryMessage = null;
    private String _exitMessage = null;

    private Map<Plugin, String> _extEntryMessages;
    private Map<Plugin, String> _extExitMessages;


    private enum MessageType {
        ENTRY,
        EXIT
    }


    /**
     * Empty constructor
     */
    protected Region(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
    }


    /**
     * Named constructor
     */
    protected Region(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _name = name;
        _plugin = plugin;
        _searchName = name.toLowerCase();
    }


    /**
     * Constructor
     */
    public Region(Plugin plugin, String name, final IDataNode settings) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(settings);

        _name = name;
        _plugin = plugin;
        _searchName = name.toLowerCase();
        _dataNode = settings;

        initCoords(settings.getLocation(_p1Path), settings.getLocation(_p2Path));

        _ownerId = settings.getUUID("owner-id");
        _entryMessage = settings.getString("entry-message");
        _exitMessage = settings.getString("entry-message");


        Set<String> entryPluginNames =settings.getSubNodeNames("ext-entry-messages");
        if (entryPluginNames != null && !entryPluginNames.isEmpty()) {

            _extEntryMessages = new HashMap<Plugin, String>(entryPluginNames.size());

            for (String pluginName : entryPluginNames) {

                Plugin pl = Bukkit.getPluginManager().getPlugin(pluginName);

                if (pl == null)
                    continue;

                String msg = settings.getString("ext-entry-messages." + pluginName);
                if (msg == null)
                    continue;

                _extEntryMessages.put(pl, msg);
            }

        }

        Bukkit.getScheduler().runTaskLater(GenericsLib.getPlugin(), new Runnable() {

            @Override
            public void run () {

                loadExtMessages(settings, MessageType.ENTRY);
                loadExtMessages(settings, MessageType.EXIT);
            }

        }, 20);


        if (_entryMessage != null || _exitMessage != null)
            setIsPlayerWatcher(true);
    }


    /**
     * Get the name of the region.
     */
    public final String getName() {
        return _name;
    }

    /**
     * Get the name of the region in lower case.
     */
    public final String getSearchName() {
        return _searchName;
    }

    /**
     * Get the owning plugin.
     */
    public final Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the world the region is in.
     */
    @Nullable
    public final World getWorld() {
        if (_p1 != null)
            return _p1.getWorld();

        if (_p2 != null)
            return _p2.getWorld();

        return null;
    }

    /**
     * Used to determine if the region subscribes to player events.
     */
    public final boolean isPlayerWatcher() {
        return _isPlayerWatcher;
    }

    /**
     * Get the id of the region owner.
     */
    @Nullable
    public UUID getOwnerId() {
        return _ownerId;
    }

    /**
     * Determine if the region has an owner.
     */
    public boolean hasOwner() {
        return _ownerId != null;
    }

    /**
     * Set the regions owner.
     *
     * @param ownerId  The id of the new owner.
     */
    public boolean setOwner(UUID ownerId) {

        UUID oldId = _ownerId;

        RegionOwnerChangedEvent event = RegionOwnerChangedEvent.callEvent(new ReadOnlyRegion(this), oldId, ownerId);

        if (event.isCancelled())
            return false;


        _ownerId = ownerId;

        if (_dataNode != null) {
            _dataNode.set("owner-id", ownerId);
            _dataNode.saveAsync(null);
        }

        onOwnerChanged(oldId, ownerId);

        return true;
    }

    /**
     * Determine if the regions cuboid points have been set.
     */
    public final boolean isDefined() {
        return _p1 != null && _p2 != null;
    }

    /**
     * Get the cuboid regions first point location.
     */
    public final Location getP1() {
        if (_p1 == null)
            return null;

        synchronized (_sync) {
            return _p1.clone();
        }
    }

    /**
     * Get the cuboid regions seconds point location.
     */
    public final Location getP2() {
        if (_p2 == null)
            return null;

        synchronized (_sync) {
            return _p2.clone();
        }
    }

    /**
     * Get the cuboid regions lower point location.
     */
    public final Location getLowerPoint() {
        return getP1();
    }

    /**
     * Get the cuboid regions upper point location.
     */
    public final Location getUpperPoint() {
        return getP2();
    }

    /**
     * Set the regions cuboid point coordinates.
     *
     * <p>Saves to the regions data node if it has one.</p>
     *
     * @param p1  The first point location.
     * @param p2  The second point location.
     */
    public final void setCoords(Location p1, Location p2) {
        PreCon.notNull(p1);
        PreCon.notNull(p2);

        GenericsLib.getRegionManager().unregister(this);

        setPoint(RegionPoint.P1, p1);
        setPoint(RegionPoint.P2, p2);

        _chunks = null;
        updateMath();

        if (_dataNode != null)
            _dataNode.saveAsync(null);

        try {
            onCoordsChanged(_p1, _p2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the smallest X axis coordinates
     * of the region.
     */
    public final int getXStart() {
        return _startX;
    }

    /**
     * Get the smallest Y axis coordinates
     * of the region.
     */
    public final int getYStart() {
        return _startY;
    }

    /**
     * Get the smallest Z axis coordinates
     * of the region.
     */
    public final int getZStart() {
        return _startZ;
    }

    /**
     * Get the largest X axis coordinates
     * of the region.
     */
    public final int getXEnd() {
        return _endX;
    }

    /**
     * Get the largest Y axis coordinates
     * of the region.
     */
    public final int getYEnd() {
        return _endY;
    }

    /**
     * Get the largest Z axis coordinates
     * of the region.
     */
    public final int getZEnd() {
        return _endZ;
    }

    /**
     * Get the X axis width of the region.
     */
    public final int getXWidth() {
        return _xWidth;
    }

    /**
     * Get the Z axis width of the region.
     */
    public final int getZWidth() {
        return _zWidth;
    }

    /**
     * Get the Y axis height of the region.
     */
    public final int getYHeight() {
        return _yHeight;
    }

    /**
     * Get the number of blocks that make up the width of the
     * region on the X axis.
     */
    public final int getXBlockWidth() {
        return _xBlockWidth;
    }

    /**
     * Get the number of blocks that make up the width of the
     * region on the Z axis.
     */
    public final int getZBlockWidth() {
        return _zBlockWidth;
    }

    /**
     * Get the number of blocks that make up the height of the
     * region on the Y axis.
     */
    public final int getYBlockHeight() {
        return _yBlockHeight;
    }

    /**
     * Get the total volume of the region.
     */
    public final long getVolume() {
        return _volume;
    }

    /**
     * Get the center location of the region.
     */
    public final Location getCenter() {
        if (_center == null)
            return null;
        return _center.clone();
    }

    /**
     * Get the smallest X axis coordinates from the chunks
     * the region intersects with.
     */
    public final int getChunkX() {
        return _chunkX;
    }

    /**
     * Get the smallest Z axis coordinates from the chunks
     * the region intersects with.
     */
    public final int getChunkZ() {
        return _chunkZ;
    }

    /**
     * Get the number of chunks that comprise the chunk width
     * on the X axis of the region.
     */
    public final int getChunkXWidth() {
        return _chunkXWidth;
    }

    /**
     * Get the number of chunks that comprise the chunk width
     * on the Z axis of the region.
     */
    public int getChunkZWidth() {
        return _chunkZWidth;
    }


    /**
     * Determine if the region is 1 block tall.
     */
    public boolean isFlatHorizontal() {
        return getYBlockHeight() == 1;
    }

    /**
     * Determine if the region is 1 block wide on the
     * X or Z axis and is not 1 block tall.
     */
    public boolean isFlatVertical() {
        return !isFlatHorizontal() &&
                (getZBlockWidth() == 1 || getXBlockWidth() == 1);
    }

    /**
     * Determine if the region contains the specified material.
     *
     * @param material  The material to search for.
     */
    public final boolean contains(Material material) {

        synchronized (_sync) {

            if (getWorld() == null)
                return false;

            int xlen = _endX;
            int ylen = _endY;
            int zlen = _endZ;

            for (int x = _startX; x <= xlen; x++) {

                for (int y = _startY; y <= ylen; y++) {

                    for (int z = _startZ; z <= zlen; z++) {

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
    public final boolean contains(Location loc) {
        if (getWorld() == null)
            return false;

        synchronized (_sync) {
            if (!isDefined() || !loc.getWorld().getName().equals(getWorld().getName())) {
                return false;
            }

            int x = loc.getBlockX();
            int y = loc.getBlockY();
            int z = loc.getBlockZ();

            _sync.notifyAll();

            return x >= _startX && x <= _endX &&
                    y >= _startY && y <= _endY &&
                    z >= _startZ && z <= _endZ;
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
    public final boolean contains(Location loc, boolean cx, boolean cy, boolean cz) {

        if (getWorld() == null)
            return false;

        synchronized (_sync) {
            if (!isDefined() || !loc.getWorld().getName().equals(getWorld().getName())) {
                return false;
            }

            if (cx) {
                int x = loc.getBlockX();
                if (x < _startX || x > _endX)
                    return false;
            }

            if (cy) {
                int y = loc.getBlockY();
                if (y < _startY || y > _endY)
                    return false;
            }

            if (cz) {
                int z = loc.getBlockZ();
                if (z < _startZ || z > _endZ)
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
    public final Set<Location> find(Material material) {

        synchronized (_sync) {
            HashSet<Location> results = new HashSet<Location>(100);

            if (getWorld() == null)
                return results;

            int xlen = _endX;
            int ylen = _endY;
            int zlen = _endZ;

            for (int x = _startX; x <= xlen; x++) {

                for (int y = _startY; y <= ylen; y++) {

                    for (int z = _startZ; z <= zlen; z++) {

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
     * Get all chunks that contain at least a portion of the region.
     */
    public final List<Chunk> getChunks() {
        if (getWorld() == null)
            return null;

        synchronized (_sync) {
            if (_chunks == null) {


                if (_p1 == null || _p2 == null) {
                    return new ArrayList<>(0);
                }

                Chunk c1 = getWorld().getChunkAt(_p1);
                Chunk c2 = getWorld().getChunkAt(_p2);

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
    public void setMeta(Object key, Object value) {
        if (value == null) {
            _meta.remove(key);
            return;
        }

        _meta.put(key, value);
    }

    /**
     * Get the message displayed to players when they
     * enter the region.
     */
    @Nullable
    public String getEntryMessage() {
        return _entryMessage;
    }

    /**
     * Set the message displayed to players when they enter
     * the region.
     *
     * @param message  The message to display.
     */
    public void setEntryMessage(@Nullable String message) {
        _entryMessage = message;

        if (_dataNode != null) {
            _dataNode.set("entry-message", message);
            _dataNode.saveAsync(null);
        }

        if (message != null)
            setIsPlayerWatcher(true);
    }

    /**
     * Get the message displayed to players when they leave the region.
     */
    @Nullable
    public String getExitMessage() {
        return _exitMessage;
    }

    /**
     * Set the message displayed to players when they leave the region.
     *
     * @param message  The message to display.
     */
    public void setExitMessage(@Nullable String message) {
        _exitMessage = message;

        if (_dataNode != null) {
            _dataNode.set("exit-message", message);
            _dataNode.saveAsync(null);
        }

        if (message != null)
            setIsPlayerWatcher(true);
    }

    /**
     * Get the message displayed to players on behalf of another plugin
     * when they enter the region.
     *
     * @param plugin  The plugin.
     */
    @Nullable
    public String getEntryMessage(Plugin plugin) {
        PreCon.notNull(plugin);

        if (_extEntryMessages == null)
            return null;

        return _extEntryMessages.get(plugin);

    }

    /**
     * Set the message displayed to players on behalf of another plugin
     * when they enter the region.
     *
     * @param plugin   The plugin.
     * @param message  The message to display.
     */
    public void setEntryMessage(Plugin plugin, @Nullable String message) {
        PreCon.notNull(plugin);

        if (_extEntryMessages == null)
            _extEntryMessages = new HashMap<Plugin, String>(10);

        if (message != null) {
            _extEntryMessages.put(plugin, message);
            setIsPlayerWatcher(true);
        }
        else {
            _extEntryMessages.remove(plugin);
        }

        if (_dataNode != null) {
            _dataNode.set("ext-entry-messages." + plugin.getName(), message);
            _dataNode.saveAsync(null);
        }
    }

    /**
     * Get the message displayed on behalf of another plugin
     * when a player leaves the region.
     *
     * @param plugin  The plugin.
     */
    @Nullable
    public String getExitMessage(Plugin plugin) {
        if (_extExitMessages == null)
            return null;

        return _extExitMessages.get(plugin);
    }

    /**
     * Set the message displayed on behalf of another plugin
     * when a player enters the region.
     *
     * @param plugin   The plugin.
     * @param message  The message to display.
     */
    public void setExitMessage(Plugin plugin, @Nullable String message) {
        if (_extExitMessages == null)
            _extExitMessages = new HashMap<Plugin, String>(10);

        if (message != null) {
            _extExitMessages.put(plugin, message);
            setIsPlayerWatcher(true);
        }
        else {
            _extExitMessages.remove(plugin);
        }

        if (_dataNode != null) {
            _dataNode.set("ext-exit-messages." + plugin.getName(), message);
            _dataNode.saveAsync(null);
        }
    }

    /**
     * Dispose the region by releasing resources and
     * unregistering it from the central region manager.
     */
    public void dispose() {
        GenericsLib.getRegionManager().unregister(this);
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
    protected final void initCoords(Location p1, Location p2) {
        _p1 = p1;
        _p2 = p2;
        updateMath();
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
    protected void onCoordsChanged(Location p1, Location p2) throws IOException {
        // do nothing
    }

    /**
     * Called when a player enters the region,
     * but only if the region is a player watcher and 
     * canDoPlayerEnter() returns true.
     *
     * Intended for override if needed.
     *
     * @param p  the player entering the region.
     */
    protected void onPlayerEnter (Player p) {
        // do nothing
    }

    /**
     * Called when a player leaves the region,
     * but only if the region is a player watcher and 
     * canDoPlayerLeave() returns true.
     *
     * Intended for override if needed.
     *
     * @param p  the player leaving the region.
     */
    protected void onPlayerLeave (Player p) {
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
    protected boolean canDoPlayerEnter(Player p) {
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
    protected boolean canDoPlayerLeave(Player p) {
        return true;
    }

    /**
     * Called when the owner of the region is changed.
     *
     * Intended for override if needed.
     *
     * @param oldOwnerId  The id of the previous owner of the region.
     * @param newOwnerId  The id of the new owner of the region.
     */
    protected void onOwnerChanged(@Nullable UUID oldOwnerId, @Nullable UUID newOwnerId) {
        // do nothing
    }

    /**
     * Used by RegionEventManager to execute onPlayerEnter event.
     */
    void doPlayerEnter (Player p) {
        if (_entryMessage != null) {
            Messenger.tell(_plugin, p, _entryMessage);
        }

        if (_extEntryMessages != null) {

            for (Entry<Plugin, String> pluginStringEntry : _extEntryMessages.entrySet()) {

                String message = pluginStringEntry.getValue();

                Messenger.tell(pluginStringEntry.getKey(), p, message);
            }
        }

        if (canDoPlayerEnter(p))
            onPlayerEnter(p);
    }

    /**
     * Used by RegionEventManager to execute onPlayerLeave event.
     */
    void doPlayerLeave (Player p) {
        if (_exitMessage != null) {
            Messenger.tell(_plugin, p, _exitMessage);
        }

        if (_extExitMessages != null) {

            for (Entry<Plugin, String> pluginStringEntry : _extExitMessages.entrySet()) {

                String message = pluginStringEntry.getValue();

                Messenger.tell(pluginStringEntry.getKey(), p, message);
            }
        }

        if (canDoPlayerLeave(p))
            onPlayerLeave(p);
    }

    /*
     * Update region math variables
     */
    private void updateMath() {

        if (_p1 == null || _p2 == null)
            return;

        synchronized (_sync) {

            _startX = Math.min(_p1.getBlockX(), _p2.getBlockX());
            _startY = Math.min(_p1.getBlockY(), _p2.getBlockY());
            _startZ = Math.min(_p1.getBlockZ(), _p2.getBlockZ());

            _endX = Math.max(_p1.getBlockX(), _p2.getBlockX());
            _endY = Math.max(_p1.getBlockY(), _p2.getBlockY());
            _endZ = Math.max(_p1.getBlockZ(), _p2.getBlockZ());

            _xWidth = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getX() - _p2.getX());
            _zWidth = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getZ() - _p2.getZ());
            _yHeight = _p1 == null || _p2 == null ? 0 : (int)Math.abs(_p1.getY() - _p2.getY());

            _xBlockWidth = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockX() - _p2.getBlockX()) + 1;
            _zBlockWidth = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockZ() - _p2.getBlockZ()) + 1;
            _yBlockHeight = _p1 == null || _p2 == null ? 0 : Math.abs(_p1.getBlockY() - _p2.getBlockY()) + 1;

            _volume = _xWidth * _zWidth * _yHeight;

            if (getWorld() != null) {
                double xCenter = _startX + (_xBlockWidth / 2);
                double yCenter = _startY + (_yBlockHeight / 2);
                double zCenter = _startZ + (_zBlockWidth / 2);

                _center = new Location(getWorld(), xCenter, yCenter, zCenter);
            }

            _chunkX = Math.min(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX() ? _p1.getChunk().getX() : _p2.getChunk().getX();
            _chunkZ = Math.min(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ() ? _p1.getChunk().getZ() : _p2.getChunk().getZ();

            int chunkEndX = Math.max(_p1.getBlockX(), _p2.getBlockX()) == _p1.getBlockX() ? _p1.getChunk().getX() : _p2.getChunk().getX();
            int chunkEndZ = Math.max(_p1.getBlockZ(), _p2.getBlockZ()) == _p1.getBlockZ() ? _p1.getChunk().getZ() : _p2.getChunk().getZ();

            _chunkXWidth = chunkEndX - _chunkX + 1;
            _chunkZWidth = chunkEndZ - _chunkZ + 1;

            _sync.notifyAll();
        }

        GenericsLib.getRegionManager().register(this);
    }


    /*
     * Load external messages set by other plugins.
     */
    private void loadExtMessages(IDataNode settings, MessageType type) {

        String msgNodeName = type == MessageType.ENTRY
                ? "ext-entry-messages"
                : "ext-exit-messages";

        Set<String> pluginNames = settings.getSubNodeNames(msgNodeName);
        if (pluginNames != null && !pluginNames.isEmpty()) {

            Map<Plugin, String> map = new HashMap<Plugin, String>(10);
            setIsPlayerWatcher(true);

            for (String pluginName : pluginNames) {

                Plugin pl = Bukkit.getPluginManager().getPlugin(pluginName);

                if (pl == null)
                    continue;

                String msg = settings.getString(msgNodeName + '.' + pluginName);
                if (msg == null)
                    continue;

                map.put(pl, msg);
            }

            switch (type) {
                case ENTRY:
                    _extEntryMessages = map;
                    break;
                case EXIT:
                    _extExitMessages = map;
                    break;
            }

        }

    }

    /*
     * Set one of the region points.
     */
    private void setPoint(RegionPoint point, Location l) {
        Location lower;
        Location upper;

        switch (point) {
            case P1: {
                _lastP1 = l.clone();
                lower = _lastP1.clone();
                upper = _lastP2 != null
                        ? _lastP2.clone()
                        : _p2;
                break;
            }
            case P2: {
                _lastP2 = l.clone();
                lower = _lastP1 != null
                        ? _lastP1.clone()
                        : _p1;
                upper = _lastP2.clone();
                break;
            }
            default: {
                upper = null;
                lower = null;
            }
        }
        if (lower != null && upper != null) {
            double tmp;
            if (lower.getX() > upper.getX()) {
                tmp = lower.getX();
                lower.setX(upper.getX());
                upper.setX(tmp);
            }
            if (lower.getY() > upper.getY()) {
                tmp = lower.getY();
                lower.setY(upper.getY());
                upper.setY(tmp);
            }
            if (lower.getZ() > upper.getZ()) {
                tmp = lower.getZ();
                lower.setZ(upper.getZ());
                upper.setZ(tmp);
            }
        }
        if (lower != null) {
            _p1 = lower;
            if (_dataNode != null)
                _dataNode.set(_p1Path, lower);
        }
        if (upper != null) {
            _p2 = upper;
            if (_dataNode != null)
                _dataNode.set(_p2Path, upper);
        }

    }

    @Override
    public int hashCode() {
        if (_hash == -1) {
            _hash = _name != null
                    ? _name.hashCode() ^ super.hashCode()
                    : Rand.getInt() ^ super.hashCode();
        }
        return _hash;
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

    static enum RegionPoint {
        P1,
        P2
    }


}
