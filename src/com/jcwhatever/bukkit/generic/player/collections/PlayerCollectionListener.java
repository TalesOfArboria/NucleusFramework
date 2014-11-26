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


package com.jcwhatever.bukkit.generic.player.collections;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.MultiValueMap;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Collections that implement {@code IPlayerCollection} register
 * players added to the collection with {@code PlayerCollectionListener}
 * so that the player entry can be removed if the player logs out.
 */
final class PlayerCollectionListener implements Listener {

    private static Map<Plugin, PlayerCollectionListener> _listeners = new WeakHashMap<>(30);

    // synchronization object
    private static final Object _sync = new Object();

    /**
     * Get the singleton instance of the player collection listener
     */
    static PlayerCollectionListener get(Plugin plugin) {

        PlayerCollectionListener listener = _listeners.get(plugin);

        if (listener == null) {
            synchronized (_sync) {
                //noinspection ConstantConditions
                if (listener == null) { // check again in case previous thread already instantiated
                    listener = new PlayerCollectionListener(plugin);

                    PluginManager pm = GenericsLib.getLib().getServer().getPluginManager();
                    pm.registerEvents(listener, GenericsLib.getLib());

                    _listeners.put(plugin, listener);
                }
            }
        }

        return listener;
    }

    private final Plugin _plugin;

    // keyed to player id, a map of collections a player is contained in
    private final MultiValueMap<UUID, IPlayerCollection> _collectionMap = new MultiValueMap<>(100, 25);

    /**
     * Private Constructor.
     *
     * @param plugin  The owning plugin.
     */
    private PlayerCollectionListener(Plugin plugin) {
        _plugin = plugin;
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Register a collection as containing the specified player.
     *
     * @param p           The player.
     * @param collection  The collection.
     */
    public void addPlayer(Player p, IPlayerCollection collection) {
        addPlayer(p.getUniqueId(), collection);
    }

    /**
     * Register a collection as containing the specified player.
     *
     * @param playerId    The player Id.
     * @param collection  The collection.
     */
    public void addPlayer(UUID playerId, IPlayerCollection collection) {
        synchronized (_sync) {
            _collectionMap.put(playerId, collection);
        }
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param p           The player.
     * @param collection  The collection.
     */
    public void removePlayer(Player p, IPlayerCollection collection) {
        removePlayer(p.getUniqueId(), collection);
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param playerId    The id of the player.
     * @param collection  The collection.
     */
    public void removePlayer(UUID playerId, IPlayerCollection collection) {
        synchronized (_sync) {
            _collectionMap.removeValue(playerId, collection);
        }
    }

    // remove a player from all collections
    private void removePlayer(Player p) {
        List<IPlayerCollection> collections = _collectionMap.getValues(p.getUniqueId());
        if (collections == null || collections.isEmpty())
            return;

        Scheduler.runTaskLaterAsync(GenericsLib.getLib(), 1, new RemovePlayer(p, collections));
    }

    // event handler, Remove player from all collections when logged out
    @EventHandler(priority = EventPriority.MONITOR) // last
    private void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    // event handler, Remove player from all collections when kicked
    @EventHandler(priority = EventPriority.MONITOR) // last
    private void onPlayerQuit(PlayerKickEvent event) {
        removePlayer(event.getPlayer());
    }

    // clear collections if generics plugin is disabled
    @EventHandler
    private void onGenericsDisable(PluginDisableEvent event) {
        if (event.getPlugin() == _plugin) {
            _collectionMap.clear();
        }
    }

    // Asynchronous removal of player from collections
    static class RemovePlayer implements Runnable {
        private Player p;
        private List<IPlayerCollection> collections;

        public RemovePlayer(Player p, List<IPlayerCollection> collections) {
            this.p = p;
            this.collections = collections;
        }

        @Override
        public void run() {
            synchronized (_sync) {
                for (IPlayerCollection collection : collections) {
                    collection.removePlayer(p);
                }
            }
        }

    }



}
