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

package com.jcwhatever.bukkit.generic.internal;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.storage.DataPath;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.StorageLoadHandler;
import com.jcwhatever.bukkit.generic.storage.StorageLoadResult;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;

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
import java.util.Set;
import java.util.UUID;
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

    private IDataNode _nameData;
    private Map<Player, Date> _lastLogins = new WeakHashMap<>(100);
    private Map<Player, Date> _lastWorldChange = new WeakHashMap<>(100);

    /**
     * Private Constructor.
     */
    private PlayerTracker() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), GenericsLib.getPlugin());
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

        return _lastLogins.get(player);
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

        return _lastWorldChange.get(player);
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

    /**
     * Gets player Id from the players name using stored id to
     * name map if the player is not online.
     *
     * <p>Will not return an id if the player is not online and
     * has not logged in to the server before.</p>
     *
     * @param playerName  The name of the player
     *
     * @return Null if not found
     */
    @Nullable
    public UUID getPlayerId(String playerName) {
        PreCon.notNullOrEmpty(playerName);

        // check for online player first
        Player p = Bukkit.getPlayer(playerName);
        if (p != null)
            return p.getUniqueId();

        // check stored id/name map
        IDataNode nameData = getNameData();

        Set<String> ids = nameData.getSubNodeNames();
        if (ids == null || ids.isEmpty())
            return null;

        for (String idStr : ids) {
            String name = nameData.getString(idStr);

            if (name != null && name.equalsIgnoreCase(playerName)) {

                try {
                    return UUID.fromString(idStr);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Get the name of a player from the player Id.
     *
     * <p>Checks the GenericsLib map of player names to player Id's</p>
     *
     * @param playerId  The id of the player.
     *
     * @return  Null if a record was not found.
     */
    @Nullable
    public String getPlayerName(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode nameData = getNameData();

        return nameData.getString(playerId.toString());
    }

    /*
     * Update the GenericsLib player name/player id map.
     */
    private void setPlayerName(UUID playerId, String name) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(name);

        IDataNode nameData = getNameData();

        String currentName = getPlayerName(playerId);

        if (name.equals(currentName))
            return;

        synchronized (_sync) {
            nameData.set(playerId.toString(), name);
        }

        nameData.saveAsync(null);
    }

    // get the node that contains player id/name data.
    private IDataNode getNameData() {

        if (_nameData == null) {

            synchronized (_sync) {

                IDataNode data = DataStorage.getStorage(GenericsLib.getPlugin(), new DataPath("player-names"));
                data.loadAsync(new StorageLoadHandler() {

                    @Override
                    public void onFinish(StorageLoadResult result) {
                        if (!result.isLoaded())
                            Msg.warning("Failed to load player names file.");

                    }

                });

                _nameData = data;
            }
        }
        return _nameData;
    }

    private class EventListener implements Listener {

        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            final Player p = event.getPlayer();

            // update player name in id lookup
            setPlayerName(p.getUniqueId(), p.getName());

            _lastLogins.put(p, new Date());
            _lastWorldChange.put(event.getPlayer(), new Date());
        }

        @EventHandler
        private void onPlayerLeave(PlayerQuitEvent event) {
            _lastLogins.remove(event.getPlayer());
            _lastWorldChange.remove(event.getPlayer());
        }

        @EventHandler
        private void onPlayerKick(PlayerKickEvent event) {
            _lastLogins.remove(event.getPlayer());
            _lastWorldChange.remove(event.getPlayer());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onPlayerTeleport(PlayerTeleportEvent event) {

            if (event.isCancelled())
                return;

            if (event.getTo().getWorld().equals(event.getFrom().getWorld()))
                return;

            _lastWorldChange.put(event.getPlayer(), new Date());
        }

        @EventHandler
        private void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == GenericsLib.getPlugin()) {
                _instance = null;
            }
        }
    }

}
