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


package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.MultiBiMap;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.Scheduler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Collections that implement {@code IPlayerCollection} register
 * players added to the collection with {@code PlayerCollectionListener}
 * so that the player entry can be removed if the player logs out.
 */
final class PlayerCollectionListener implements IPluginOwned, Listener {

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

                    PluginManager pm = Nucleus.getPlugin().getServer().getPluginManager();
                    pm.registerEvents(listener, Nucleus.getPlugin());

                    _listeners.put(plugin, listener);
                }
            }
        }

        return listener;
    }

    private final Plugin _plugin;

    // keyed to player id, a map of collections a player is contained in
    private final MultiBiMap<UUID, PlayerCollectionTracker> _trackerMap = new MultiBiMap<>(100, 25);

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
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Register a collection as containing the specified player.
     *
     * @param p        The player.
     * @param tracker  The collection tracker.
     */
    public void addPlayer(Player p, PlayerCollectionTracker tracker) {
        addPlayer(p.getUniqueId(), tracker);
    }

    /**
     * Register a collection as containing the specified player.
     *
     * @param playerId  The player Id.
     * @param tracker   The collection.
     */
    public void addPlayer(UUID playerId, PlayerCollectionTracker tracker) {
        synchronized (_sync) {
            _trackerMap.put(playerId, tracker);
        }
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param p        The player.
     * @param tracker  The collection.
     */
    public void removePlayer(Player p, PlayerCollectionTracker tracker) {
        removePlayer(p.getUniqueId(), tracker);
    }

    /**
     * Unregister a collection as containing the specified player.
     *
     * @param playerId  The id of the player.
     * @param tracker   The collection.
     */
    public void removePlayer(UUID playerId, PlayerCollectionTracker tracker) {
        synchronized (_sync) {
            _trackerMap.removeValue(playerId, tracker);
        }
    }

    // remove a player from all collections
    private void removePlayer(Player p) {
        Set<PlayerCollectionTracker> trackers = _trackerMap.get(p.getUniqueId());
        if (trackers == null || trackers.isEmpty())
            return;

        Scheduler.runTaskLaterAsync(Nucleus.getPlugin(), 1, new RemovePlayer(p, trackers));
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

    // clear collections if the owning plugin is disabled
    @EventHandler
    private void onNucleusDisable(PluginDisableEvent event) {
        if (event.getPlugin() == _plugin) {
            _trackerMap.clear();
        }
    }

    // Asynchronous removal of player from collections
    static class RemovePlayer implements Runnable {
        private Player p;
        private Collection<PlayerCollectionTracker> trackers;

        public RemovePlayer(Player p, Collection<PlayerCollectionTracker> trackers) {
            this.p = p;
            synchronized (_sync) {
                this.trackers = new ArrayList<>(trackers);
            }
        }

        @Override
        public void run() {
            for (PlayerCollectionTracker tracker : trackers) {
                tracker.getCollection().removePlayer(p);
            }
        }
    }
}
