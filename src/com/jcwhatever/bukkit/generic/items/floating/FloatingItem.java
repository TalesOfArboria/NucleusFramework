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


package com.jcwhatever.bukkit.generic.items.floating;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.events.bukkit.floatingitems.FloatingItemDespawnEvent;
import com.jcwhatever.bukkit.generic.events.bukkit.floatingitems.FloatingItemSpawnEvent;
import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.entity.EntityUtils;
import com.jcwhatever.bukkit.generic.utils.entity.TrackedEntity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a controlled item stack entity.
 *
 * <p>Note that a data node to store the entity info to disk is
 * optional, but recommended even if the the use is only transient.
 * Without the data node, expect left over items that can be picked
 * up after server restarts or crashes.</p>
 */
public class FloatingItem implements IDisposable {

    private static BukkitListener _listener;

    private final String _name;
    private final ItemStack _item;
    private final IDataNode _dataNode;


    private UUID _entityId;
    private TrackedEntity _trackedEntity;
    private boolean _canPickup;
    private int _respawnTimeSeconds = 20;
    private boolean _isSpawned;

    private boolean _isDisposed;
    private Location _currentLocation;

    private List<PickupHandler> _pickupHandlers;
    private List<Runnable> _spawnHandlers;
    private List<Runnable> _despawnHandlers;

    /**
     * Constructor.
     *
     * @param name      The unique name of the item.
     * @param item      The item.
     */
    public FloatingItem(String name, ItemStack item) {
        this(name, item, null, null);
    }

    /**
     * Constructor.
     *
     * @param name             The unique name of the item.
     * @param item             The item.
     * @param initialLocation  Optional initial location of the item.
     */
    public FloatingItem(String name, ItemStack item, @Nullable Location initialLocation) {
        this(name, item, initialLocation, null);
    }

    /**
     * Constructor.
     *
     * @param name             The unique name of the item.
     * @param item             The item.
     * @param initialLocation  Optional initial location of the item.
     * @param dataNode         Optional data node to store item settings in.
     */
    public FloatingItem(String name, ItemStack item, @Nullable Location initialLocation,
                        @Nullable IDataNode dataNode) {

        PreCon.notNullOrEmpty(name);
        PreCon.notNull(item);

        _name = name;
        _item = item;
        _currentLocation = initialLocation;
        _dataNode = dataNode;

        if (_listener == null) {
            _listener = new BukkitListener();

            Bukkit.getPluginManager().registerEvents(_listener, GenericsLib.getLib());
        }

        loadSettings();
    }

    /**
     * Get the floating items name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get the floating item.
     */
    public ItemStack getItem() {
        return _item.clone();
    }

    /**
     * Get the entities unique id.
     */
    public UUID getUniqueId() {
        return _entityId;
    }

    /**
     * Get the current item entity.
     *
     * @return  Null if not spawned.
     */
    @Nullable
    public Entity getEntity() {
        return _trackedEntity.getEntity();
    }

    /**
     * Get the floating items data node, if any.
     */
    @Nullable
    public IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Determine if the item is spawned as an entity.
     */
    public boolean isSpawned() {
        return _isSpawned;
    }

    /**
     * Get the location of the floating item.
     *
     * @return  Null if no location is set yet.
     */
    @Nullable
    public Location getLocation() {
        return _currentLocation;
    }

    /**
     * Determine if the item can be picked up.
     */
    public boolean canPickup() {
        return _canPickup;
    }

    /**
     * Set if the item can be picked up.
     *
     * @param canPickup  True to allow players to pickup the item.
     */
    public void setCanPickup(boolean canPickup) {
        _canPickup = canPickup;

        if (_dataNode != null) {
            _dataNode.set("can-pickup", canPickup);
            _dataNode.saveAsync(null);
        }
    }

    /**
     * Get the number of seconds before the item is respawned
     * after being picked up.
     */
    public int getRespawnTimeSeconds() {
        return _respawnTimeSeconds;
    }

    /**
     * Set the number of seconds before the item is respawned
     * after being picked up.
     *
     * @param seconds  The number of seconds.
     */
    public void setRespawnTimeSeconds(int seconds) {
        _respawnTimeSeconds = seconds;

        if (_dataNode != null) {
            _dataNode.set("respawn-time-seconds", seconds);
            _dataNode.saveAsync(null);
        }
    }

    /**
     * Spawn the floating item entity.
     */
    public boolean spawn() {
        return _currentLocation != null && spawn(_currentLocation);
    }

    /**
     * Spawn the floating item entity.
     */
    public boolean spawn(Location location) {
        PreCon.notNull(location);

        if (_isDisposed)
            throw new RuntimeException("Cannot spawn a disposed item.");

        FloatingItemSpawnEvent event = new FloatingItemSpawnEvent(this);

        GenericsLib.getEventManager().callBukkit(event);

        if (event.isCancelled())
            return false;

        if (!despawn())
            return false;

        _isSpawned = true;

        _currentLocation = location;

        // get corrected location
        final Location spawnLocation = LocationUtils.getBlockLocation(location)
                .add(0.5, 0.5, 0.5);

        if (!location.getChunk().isLoaded()) {
            _listener.registerPendingSpawn(this);
            return true;
        }

        // spawn item entity
        Entity entity = location.getWorld().dropItem(spawnLocation, _item.clone());
        _trackedEntity = EntityUtils.trackEntity(entity);
        _entityId = entity.getUniqueId();
        entity.setVelocity(new Vector(0, 0, 0));

        // register entity
        _listener.register(this);

        // prevent stack merging
        Item item = (Item)entity;
        ItemMeta meta = item.getItemStack().getItemMeta();
        meta.setDisplayName(_entityId.toString());
        item.getItemStack().setItemMeta(meta);

        if (_dataNode != null) {
            _dataNode.set("location", location);
            _dataNode.set("is-spawned", true);
            _dataNode.set("entity-id", _entityId);
            _dataNode.saveAsync(null);
        }

        if (_spawnHandlers != null) {
            for (Runnable runnable : _spawnHandlers)
                runnable.run();
        }

        return true;
    }

    /**
     * Despawn the floating item entity.
     */
    public boolean despawn() {

        if (_trackedEntity == null)
            return true;

        Entity entity = _trackedEntity.getEntity();

        FloatingItemDespawnEvent event = new FloatingItemDespawnEvent(this);

        GenericsLib.getEventManager().callBukkit(event);

        if (event.isCancelled())
            return false;

        _listener.unregister(this);

        _isSpawned = false;

        _listener.unregisterPendingSpawn(this);
        EntityUtils.removeEntity(entity);

        _trackedEntity = null;
        _entityId = null;

        if (_dataNode != null) {
            _dataNode.set("is-spawned", false);
            _dataNode.set("entity-id", null);
            _dataNode.saveAsync(null);
        }

        if (_despawnHandlers != null) {
            for (Runnable runnable : _despawnHandlers)
                runnable.run();
        }

        return true;
    }

    /**
     * Give a copy of the item to a player.
     *
     * @param p  The player.
     */
    public void give(Player p) {
        PreCon.notNull(p);

        p.getInventory().addItem(_item.clone());
    }

    /**
     * Determine if the floating item has been disposed.
     */
    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        despawn();
        _listener.unregister(this);
        _isDisposed = true;
    }

    /**
     * Add a callback to run when the item is spawned.
     *
     * @param handler  The callback handler.
     */
    public void addOnSpawn(Runnable handler) {
        PreCon.notNull(handler);

        if (_spawnHandlers == null)
            _spawnHandlers = new ArrayList<>(10);

        _spawnHandlers.add(handler);
    }

    /**
     * Remove a spawn callback handler.
     *
     * @param handler  The callback handler.
     */
    public void removeOnSpawn(Runnable handler) {
        PreCon.notNull(handler);

        if (_spawnHandlers == null)
            return;

        _spawnHandlers.remove(handler);
    }

    /**
     * Add a callback to run when the item is despawned.
     *
     * @param handler  The callback handler.
     */
    public void addOnDespawn(Runnable handler) {
        PreCon.notNull(handler);

        if (_despawnHandlers == null)
            _despawnHandlers = new ArrayList<>(10);

        _despawnHandlers.add(handler);
    }

    /**
     * Remove a despawn callback handler.
     *
     * @param handler  The callback handler.
     */
    public void removeOnDespawn(Runnable handler) {
        PreCon.notNull(handler);

        if (_despawnHandlers == null)
            return;

        _despawnHandlers.remove(handler);
    }

    /**
     * Add a callback to run when an attempt is made to pickup the item.
     *
     * @param handler  The callback handler.
     */
    public void addOnPickup(PickupHandler handler) {
        PreCon.notNull(handler);

        if (_pickupHandlers == null)
            _pickupHandlers = new ArrayList<>(10);

        _pickupHandlers.add(handler);
    }

    /**
     * Remove a pickup callback handler.
     *
     * @param handler  The callback handler.
     */
    public void removeOnPickup(PickupHandler handler) {
        PreCon.notNull(handler);

        if (_pickupHandlers == null)
            return;

        _pickupHandlers.remove(handler);
    }

    /**
     * Called after the items data node settings are loaded
     *
     * @param dataNode  The items data node.
     */
    protected void onLoadSettings(@SuppressWarnings("unused") IDataNode dataNode) {
        // do nothing
    }

    void onPickup(Player p, boolean isCancelled) {
        if (_pickupHandlers == null)
            return;

        for (PickupHandler handler : _pickupHandlers)
            handler.onPickup(p, this, isCancelled);
    }

    private void loadSettings() {
        if (_dataNode == null)
            return;

        _canPickup = _dataNode.getBoolean("can-pickup", _canPickup);
        _respawnTimeSeconds = _dataNode.getInteger("respawn-time-seconds", _respawnTimeSeconds);
        _isSpawned = _dataNode.getBoolean("is-spawned", _isSpawned);
        _currentLocation = _dataNode.getLocation("location", _currentLocation);
        _entityId = _dataNode.getUUID("entity-id", _entityId);

        if (_currentLocation != null && _entityId != null) {

            // Find the entity from the chunk of the stored location
            Entity entity = EntityUtils.getEntityByUUID(_currentLocation.getChunk(), _entityId);

            if (entity != null) {
                _trackedEntity = EntityUtils.trackEntity(entity);
            }

            if (entity != null && !_isSpawned) {
                despawn();
            } else if (entity == null && _isSpawned) {
                spawn(_currentLocation);
            } else if (entity != null) {
                _listener.register(this);
            }

        }

        onLoadSettings(_dataNode);
    }

    private void setTrackedEntity(Entity entity) {
        _trackedEntity = EntityUtils.trackEntity(entity);
    }

    public static interface PickupHandler {
        void onPickup(Player p, FloatingItem item, boolean isCancelled);
    }


}
