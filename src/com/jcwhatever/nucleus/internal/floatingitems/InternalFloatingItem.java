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

package com.jcwhatever.nucleus.internal.floatingitems;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.floatingitems.FloatingItemDespawnEvent;
import com.jcwhatever.nucleus.events.floatingitems.FloatingItemSpawnEvent;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.managed.entity.ITrackedEntity;
import com.jcwhatever.nucleus.managed.floatingitems.IFloatingItem;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

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
public class InternalFloatingItem implements IFloatingItem {

    private static final Location CENTERED_LOCATION = new Location(null, 0, 0, 0);
    private static BukkitListener _listener;

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;
    private final ItemStack _item;
    private final IDataNode _dataNode;

    private UUID _entityId;
    private ITrackedEntity _trackedEntity;
    private boolean _canPickup;
    private boolean _isCentered = true;
    private int _respawnTimeSeconds = 20;
    private boolean _isSpawned;

    private boolean _isDisposed;
    private Location _currentLocation;

    private NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor.
     *
     * @param name             The unique name of the item.
     * @param item             The item.
     * @param initialLocation  Optional initial location of the item.
     * @param dataNode         Data node to store item settings in.
     */
    public InternalFloatingItem(Plugin plugin,
                                String name, ItemStack item,
                                @Nullable Location initialLocation,
                                IDataNode dataNode) {

        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(item);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
        _item = item;
        _currentLocation = initialLocation;
        _dataNode = dataNode;

        if (_listener == null) {
            _listener = new BukkitListener();

            Bukkit.getPluginManager().registerEvents(_listener, Nucleus.getPlugin());
        }

        loadSettings();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public UUID getUniqueId() {
        return _entityId;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public ItemStack getItem() {
        return _item.clone();
    }

    @Override
    @Nullable
    public Entity getEntity() {
        if (_trackedEntity == null) {
            return null;
        }

        return _trackedEntity.getEntity();
    }

    /**
     * Get the floating items data node, if any.
     */
    @Nullable
    public IDataNode getDataNode() {
        return _dataNode;
    }

    @Override
    public boolean isSpawned() {
        return _isSpawned;
    }

    @Override
    @Nullable
    public Location getLocation() {
        return getLocation(new Location(null, 0, 0, 0));
    }

    @Nullable
    @Override
    public Location getLocation(Location output) {
        return LocationUtils.copy(_currentLocation, output);
    }

    @Override
    public boolean canPickup() {
        return _canPickup;
    }

    @Override
    public void setCanPickup(boolean canPickup) {
        _canPickup = canPickup;

        _dataNode.set("can-pickup", canPickup);
        _dataNode.save();
    }

    @Override
    public boolean isCentered() {
        return _isCentered;
    }

    @Override
    public void setCentered(boolean isCentered) {
        _isCentered = isCentered;

        _dataNode.set("is-centered", isCentered);
        _dataNode.save();
    }

    @Override
    public int getRespawnTimeSeconds() {
        return _respawnTimeSeconds;
    }

    @Override
    public void setRespawnTimeSeconds(int seconds) {
        _respawnTimeSeconds = seconds;

        _dataNode.set("respawn-time-seconds", seconds);
        _dataNode.save();
    }

    @Override
    public boolean spawn() {
        return _currentLocation != null && spawn(_currentLocation);
    }

    @Override
    public boolean spawn(Location location) {
        PreCon.notNull(location);

        if (_isDisposed)
            throw new RuntimeException("Cannot spawn a disposed item.");

        location = location.clone(); // clone: prevent external changes from affecting the location

        FloatingItemSpawnEvent event = new FloatingItemSpawnEvent(this);

        Nucleus.getEventManager().callBukkit(this, event);

        if (event.isCancelled())
            return false;

        if (!despawn())
            return false;

        _isSpawned = true;

        _currentLocation = location;

        // get corrected location
        final Location spawnLocation = _isCentered
                ? LocationUtils.getCenteredLocation(location, CENTERED_LOCATION).add(0, 0.5, 0) // add y 0.5 to prevent falling through surface block
                : LocationUtils.add(location, 0, 0.5, 0);

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

        _dataNode.set("location", location);
        _dataNode.set("is-spawned", true);
        _dataNode.set("entity-id", _entityId);
        _dataNode.save();

        _agents.update("onSpawn", entity);

        return true;
    }

    @Override
    public boolean despawn() {

        if (_trackedEntity == null)
            return true;

        Entity entity = _trackedEntity.getEntity();

        FloatingItemDespawnEvent event = new FloatingItemDespawnEvent(this);

        if (Nucleus.getPlugin().isEnabled())
            Nucleus.getEventManager().callBukkit(this, event);

        if (event.isCancelled())
            return false;

        _listener.unregister(this);

        _isSpawned = false;

        _listener.unregisterPendingSpawn(this);
        EntityUtils.removeEntity(entity);

        _trackedEntity = null;
        _entityId = null;


        _dataNode.set("is-spawned", false);

        if (_plugin.isEnabled()) {
            _dataNode.set("entity-id", null);
        }

        _dataNode.save();


        _agents.update("onDespawn", entity);

        return true;
    }

    @Override
    public boolean give(Player player) {
        PreCon.notNull(player);

        if (!InventoryUtils.hasRoom(player.getInventory(), _item))
            return false;

        player.getInventory().addItem(_item.clone());
        return true;
    }

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

    @Override
    public InternalFloatingItem onSpawn(IUpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onSpawn").register(subscriber);

        return this;
    }

    @Override
    public InternalFloatingItem onDespawn(IUpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onDespawn").register(subscriber);

        return this;
    }

    @Override
    public InternalFloatingItem onPickup(IUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onPickup").register(subscriber);

        return this;
    }

    void onPickup(Player p) {

        _agents.update("onPickup", p);
    }

    private void loadSettings() {

        _canPickup = _dataNode.getBoolean("can-pickup", _canPickup);
        _isCentered = _dataNode.getBoolean("is-centered", _isCentered);
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
        else {
            _isSpawned = false;
        }
    }
}

