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

package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.DateUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Tracks useful information about players.
 */
public final class PlayerTracker {

    private static PlayerTracker _instance;

    public static PlayerTracker get() {
        if (_instance == null)
            _instance = new PlayerTracker();

        return _instance;
    }

    private final Object _sync = new Object();
    private Map<Player, Date> _lastLogins = new WeakHashMap<>(100);
    private Map<Player, Date> _lastWorldChange = new WeakHashMap<>(100);

    /**
     * Private Constructor.
     */
    private PlayerTracker() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), Nucleus.getPlugin());
    }

    /**
     * Get the time the player logged in for the current session.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not logged in.
     */
    @Nullable
    public Date getLoginDate(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {
            return _lastLogins.get(player);
        }
    }

    /**
     * Get the time the player last changed worlds in the current session.
     *
     * @param player  The player to check.
     *
     * @return  Null if the player is not logged in.
     */
    @Nullable
    public Date getLastWorldChangeDate(Player player) {
        PreCon.notNull(player);

        synchronized (_sync) {
            return _lastWorldChange.get(player);
        }
    }

    /**
     * Get the number of milliseconds the player has been on
     * the server during the current login session.
     *
     * @param player  The player to check.
     *
     * @return 0 if the player is not online.
     */
    public long getSessionTime(Player player) {
        PreCon.notNull(player);

        Date date = getLoginDate(player);
        if (date == null)
            return 0;

        return DateUtils.getDeltaMilliseconds(date, new Date());
    }

    /**
     * Get the number of milliseconds the player has been in
     * the world they are currently in.
     *
     * @param player  The player to check.
     *
     * @return 0 if the player is not online.
     */
    public long getWorldSessionTime(Player player) {
        PreCon.notNull(player);

        Date date = getLastWorldChangeDate(player);
        if (date == null)
            return 0;

        return DateUtils.getDeltaMilliseconds(date, new Date());
    }

    private class EventListener implements Listener {

        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            Player p = event.getPlayer();

            synchronized (_sync) {
                _lastLogins.put(p, new Date());
                _lastWorldChange.put(event.getPlayer(), new Date());
            }
        }

        @EventHandler
        private void onPlayerLeave(PlayerQuitEvent event) {
            synchronized (_sync) {
                _lastLogins.remove(event.getPlayer());
                _lastWorldChange.remove(event.getPlayer());
            }
        }

        @EventHandler
        private void onPlayerKick(PlayerKickEvent event) {
            synchronized (_sync) {
                _lastLogins.remove(event.getPlayer());
                _lastWorldChange.remove(event.getPlayer());
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onPlayerTeleport(PlayerTeleportEvent event) {

            if (event.isCancelled())
                return;

            if (event.getTo().getWorld().equals(event.getFrom().getWorld()))
                return;

            synchronized (_sync) {
                _lastWorldChange.put(event.getPlayer(), new Date());
            }
        }

        @EventHandler
        private void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == Nucleus.getPlugin()) {
                _instance = null;
            }
        }
    }

}
