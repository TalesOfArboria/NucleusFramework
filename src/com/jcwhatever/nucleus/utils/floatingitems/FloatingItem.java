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


package com.jcwhatever.nucleus.utils.floatingitems;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.observer.agent.AgentHashMap;
import com.jcwhatever.nucleus.events.floatingitems.FloatingItemDespawnEvent;
import com.jcwhatever.nucleus.events.floatingitems.FloatingItemSpawnEvent;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.entity.TrackedEntity;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
public class FloatingItem implements IFloatingItem {

    private static final Location CENTERED_LOCATION = new Location(null, 0, 0, 0);
    private static BukkitListener _listener;

    private final String _name;
    private final String _searchName;
    private final ItemStack _item;
    private final IDataNode _dataNode;

    private UUID _entityId;
    private TrackedEntity _trackedEntity;
    private boolean _canPickup;
    private boolean _isCentered = true;
    private int _respawnTimeSeconds = 20;
    private boolean _isSpawned;

    private boolean _isDisposed;
    private Location _currentLocation;

    private AgentHashMap<String, UpdateAgent> _updateAgents =
            new AgentHashMap<String, UpdateAgent>()
                    .set("onPickup", new UpdateAgent<Player>())
                    .set("onSpawn", new UpdateAgent<Entity>())
                    .set("onDespawn", new UpdateAgent<Entity>());

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

    /**
     * Get the floating items name.
     */
    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Get the floating item.
     */
    @Override
    public ItemStack getItem() {
        return _item.clone();
    }

    /**
     * Get the entities unique id.
     */
    @Override
    public UUID getUniqueId() {
        return _entityId;
    }

    /**
     * Get the current item entity.
     *
     * @return  Null if not spawned.
     */
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
    @Override
    @Nullable
    public IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Determine if the item is spawned as an entity.
     */
    @Override
    public boolean isSpawned() {
        return _isSpawned;
    }

    /**
     * Get the location of the floating item.
     *
     * @return  Null if no location is set yet.
     */
    @Override
    @Nullable
    public Location getLocation() {
        return _currentLocation;
    }

    /**
     * Determine if the item can be picked up.
     */
    @Override
    public boolean canPickup() {
        return _canPickup;
    }

    /**
     * Set if the item can be picked up.
     *
     * @param canPickup  True to allow players to pickup the item.
     */
    @Override
    public void setCanPickup(boolean canPickup) {
        _canPickup = canPickup;

        if (_dataNode != null) {
            _dataNode.set("can-pickup", canPickup);
            _dataNode.save();
        }
    }

    /**
     * Determine if the item is spawned centered within
     * the block at the spawn location.
     */
    @Override
    public boolean isCentered() {
        return _isCentered;
    }

    /**
     * Set item spawned centered within the block
     * at the spawn location.
     *
     * @param isCentered  True to center.
     */
    @Override
    public void setCentered(boolean isCentered) {
        _isCentered = isCentered;

        if (_dataNode != null) {
            _dataNode.set("is-centered", isCentered);
            _dataNode.save();
        }
    }

    /**
     * Get the number of seconds before the item is respawned
     * after being picked up.
     */
    @Override
    public int getRespawnTimeSeconds() {
        return _respawnTimeSeconds;
    }

    /**
     * Set the number of seconds before the item is respawned
     * after being picked up.
     *
     * @param seconds  The number of seconds.
     */
    @Override
    public void setRespawnTimeSeconds(int seconds) {
        _respawnTimeSeconds = seconds;

        if (_dataNode != null) {
            _dataNode.set("respawn-time-seconds", seconds);
            _dataNode.save();
        }
    }

    /**
     * Spawn the floating item entity.
     */
    @Override
    public boolean spawn() {
        return _currentLocation != null && spawn(_currentLocation);
    }

    /**
     * Spawn the floating item entity.
     */
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

        if (_dataNode != null) {
            _dataNode.set("location", location);
            _dataNode.set("is-spawned", true);
            _dataNode.set("entity-id", _entityId);
            _dataNode.save();
        }

        // notify onSpawn subscribers
        @SuppressWarnings("unchecked")
        UpdateAgent<Entity> agent = _updateAgents.get("onSpawn");
        agent.update(entity);

        return true;
    }

    /**
     * Despawn the floating item entity.
     */
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

        if (_dataNode != null) {
            _dataNode.set("is-spawned", false);

            if (Nucleus.getPlugin().isEnabled())
                _dataNode.set("entity-id", null);

            _dataNode.save();
        }

        @SuppressWarnings("unchecked")
        UpdateAgent<Entity> agent = _updateAgents.get("onDespawn");
        agent.update(entity);

        return true;
    }

    /**
     * Give a copy of the item to a player.
     *
     * @param p  The player.
     */
    @Override
    public boolean give(Player p) {
        PreCon.notNull(p);

        if (!InventoryUtils.hasRoom(p.getInventory(), _item))
            return false;

        p.getInventory().addItem(_item.clone());
        return true;
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
     * Get updated when the item is spawned.
     *
     * @param subscriber  The update subscriber.
     *
     * @return  Self for chaining.
     */
    public FloatingItem onSpawn(IUpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.get("onSpawn").register(subscriber);

        return this;
    }

    /**
     * Get updated when the item is despawned.
     *
     * @param subscriber  The update subscriber.
     *
     * @return  Self for chaining.
     */
    public FloatingItem onDespawn(IUpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.get("onDespawn").register(subscriber);

        return this;
    }

    /**
     * Get updated when the item is picked up by a player.
     *
     * <p>Is updated event if the player is prevented from
     * picking up the item.</p>
     *
     * @param subscriber  The update subscriber. The subscriber will receive the player
     *                    that was detected picking up the item.
     *
     * @return  Self for chaining.
     */
    public FloatingItem onPickup(IUpdateSubscriber<Player> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.get("onPickup").register(subscriber);

        return this;
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

        @SuppressWarnings("unchecked")
        UpdateAgent<Player> agent = _updateAgents.get("onPickup");
        agent.update(p);
    }

    private void loadSettings() {
        if (_dataNode == null)
            return;

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

        onLoadSettings(_dataNode);
    }
}
