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
import com.jcwhatever.nucleus.events.NucleusLoadedEvent;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Collections that implement {@link IPlayerCollection} register
 * players added to the collection with {@link PlayerCollectionListener}
 * so that the player entry can be removed if the player logs out.
 */
final class PlayerCollectionListener implements IPluginOwned, Listener {

    private static Map<Plugin, PlayerCollectionListener> _listeners = new WeakHashMap<>(30);

    /**
     * Get the singleton instance of the player collection listener
     */
    static PlayerCollectionListener get(Plugin plugin) {

        PlayerCollectionListener listener = _listeners.get(plugin);

        if (listener == null) {
            listener = new PlayerCollectionListener(plugin);

            PluginManager pm = Bukkit.getServer().getPluginManager();
            pm.registerEvents(listener, Nucleus.getPlugin());

            _listeners.put(plugin, listener);
        }

        return listener;
    }

    private final Plugin _plugin;
    private final Map<PlayerCollectionTracker, Set<UUID>> _trackers = new WeakHashMap<>(100);
    private final AsyncPlayerRemover _playerRemover = new AsyncPlayerRemover();
    private boolean _isRemoverStarted;

    /**
     * Private Constructor.
     *
     * @param plugin  The owning plugin.
     */
    private PlayerCollectionListener(Plugin plugin) {
        _plugin = plugin;
        startRemover();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Register a collection tracker.
     *
     * @param tracker  The tracker.
     */
    public void registerTracker(PlayerCollectionTracker tracker) {
        synchronized (_trackers) {
            _trackers.put(tracker, new HashSet<UUID>(Bukkit.getMaxPlayers() / 2));
        }
    }

    public void addPlayer(UUID playerId, PlayerCollectionTracker tracker) {
        synchronized (_trackers) {
            Set<UUID> playerIds = _trackers.get(tracker);
            if (playerIds == null)
                throw new IllegalStateException("tracker not registered.");

            playerIds.add(playerId);
        }
    }

    public void removePlayer(UUID playerId, PlayerCollectionTracker tracker) {
        synchronized (_trackers) {
            Set<UUID> playerIds = _trackers.get(tracker);
            if (playerIds == null)
                throw new IllegalStateException("tracker not registered.");

            playerIds.remove(playerId);
        }
    }

    // remove a player from all collections
    private void removePlayer(Player player) {

        if (_trackers.isEmpty())
            return;

        synchronized (_playerRemover.queue) {
            _playerRemover.queue.offer(player.getUniqueId());
        }
    }

    private void startRemover() {
        if (_isRemoverStarted || !Nucleus.getPlugin().isLoaded())
            return;

        Scheduler.runTaskRepeatAsync(getPlugin(), 1, 1, _playerRemover);
        _isRemoverStarted = true;
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

    @EventHandler(priority = EventPriority.MONITOR)
    private void onNucleusLoaded(@SuppressWarnings("unused") NucleusLoadedEvent event) {
        startRemover();
    }

    class AsyncPlayerRemover implements Runnable {

        final Queue<UUID> queue = new ArrayDeque<>(Bukkit.getMaxPlayers() / 2);
        final Map<PlayerCollectionTracker, Set<UUID>> trackers = new HashMap<>(100);

        @Override
        public void run() {

            synchronized (queue) {
                if (queue.isEmpty())
                    return;
            }

            synchronized (_trackers) {
                if (_trackers.isEmpty())
                    return;

                trackers.putAll(_trackers);
            }

            synchronized (queue) {
                while (!queue.isEmpty()) {

                    UUID playerId = queue.remove();

                    for (Map.Entry<PlayerCollectionTracker, Set<UUID>> entry : trackers.entrySet()) {

                        if (!entry.getValue().contains(playerId))
                            continue;

                        try {
                            entry.getKey().getCollection().removePlayer(playerId);
                        }
                        catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            trackers.clear();
        }
    }
}
