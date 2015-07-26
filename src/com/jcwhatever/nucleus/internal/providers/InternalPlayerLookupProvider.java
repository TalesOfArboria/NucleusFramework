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

package com.jcwhatever.nucleus.internal.providers;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.playerlookup.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultAgent;
import com.jcwhatever.nucleus.utils.observer.future.IFutureResult;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Nucleus frameworks default player lookup provider
 */
public final class InternalPlayerLookupProvider extends Provider implements IPlayerLookupProvider {

    private final Object _dataSync = new Object();
    private volatile IDataNode _nameData;

    public InternalPlayerLookupProvider(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), plugin);
        setInfo(new InternalProviderInfo(this.getClass(),
                "NucleusPlayerLookup", "Default player lookup provider."));
    }

    @Nullable
    @Override
    public UUID getPlayerId(String playerName) {
        PreCon.notNullOrEmpty(playerName);

        // check for online player first
        Player p = Bukkit.getPlayer(playerName);
        if (p != null)
            return p.getUniqueId();

        // check stored id/name map
        IDataNode nameData = getPlayerData();

        synchronized (_dataSync) {

            for (IDataNode node : nameData) {
                String name = node.getString("name");

                if (name != null && name.equalsIgnoreCase(playerName)) {

                    try {
                        return UUID.fromString(node.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public String getPlayerName(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode data = getPlayerData(playerId);
        if (data == null)
            return null;

        synchronized (_dataSync) {
            return data.getString("name");
        }
    }

    @Nullable
    @Override
    public Date getFirstLogin(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode data = getPlayerData(playerId);
        if (data == null)
            return null;

        synchronized (_dataSync) {
            return data.getDate("first");
        }
    }

    @Nullable
    @Override
    public Date getLastLogin(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode data = getPlayerData(playerId);
        if (data == null)
            return null;

        synchronized (_dataSync) {
            return data.getDate("last");
        }
    }

    @Override
    public int getLoginCount(UUID playerId) {
        PreCon.notNull(playerId);

        IDataNode data = getPlayerData(playerId);
        if (data == null)
            return 0;

        synchronized (_dataSync) {
            return data.getInteger("count", 0);
        }
    }

    @Override
    public IFutureResult<Collection<UUID>> searchNames(String searchText, int maxResults) {
        LinkedList<PlayerData> unsorted = new LinkedList<>();

        String lower = searchText.toLowerCase();

        IDataNode playersNode = getPlayerData();
        for (IDataNode node : playersNode) {
            String name = node.getString("name");
            if (name == null)
                continue;

            if (name.toLowerCase().contains(lower))
                unsorted.add(new PlayerData(name, TextUtils.parseUUID(node.getName())));
        }

        Collection<PlayerData> sorted = CollectionUtils.textSearch(
                unsorted, searchText, new CollectionUtils.ISearchTextGetter<PlayerData>() {
                    @Override
                    public String getText(PlayerData element) {
                        return element.name;
                    }
                });

        List<UUID> result = new ArrayList<>(Math.min(sorted.size(), maxResults));

        int count = 0;
        for (PlayerData data : sorted) {
            if (count >= maxResults)
                break;
            result.add(data.id);
            count++;
        }

        return new FutureResultAgent<Collection<UUID>>().success(result);
    }

    /*
     * Update the NucleusFramework player name/player id map.
     */
    private void setPlayerName(UUID playerId, String name) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(name);

        Date loginDate = new Date();

        IDataNode data = getPlayerData(playerId);

        synchronized (_dataSync) {
            if (data == null) {
                data = getPlayerData().getNode(playerId.toString());

                data.set("first", loginDate);
            }

            int count = data.getInteger("count", 0);

            data.set("last", loginDate);
            data.set("count", count + 1);
        }

        String currentName = getPlayerName(playerId);

        if (name.equals(currentName))
            return;

        synchronized (_dataSync) {
            data.set("name", name);
        }

        data.save();
    }

    // get the node that contains player id/name data.
    private IDataNode getPlayerData() {

        if (_nameData == null) {

            synchronized (_dataSync) {
                if (_nameData != null)
                    return _nameData;

                String dataPath = Bukkit.getOnlineMode() ? "player-data" : "offline-player-data";

                IDataNode data = DataStorage.get(Nucleus.getPlugin(), getDataPath(dataPath));
                if (!data.load()) {
                    NucMsg.warning("Failed to load player data file.");
                }
                _nameData = data;
            }
        }
        return _nameData;
    }

    @Nullable
    private IDataNode getPlayerData(UUID playerId) {
        IDataNode data = getPlayerData();
        String nodeName = playerId.toString();

        if (!data.hasNode(nodeName))
            return null;

        return data.getNode(nodeName);
    }

    private class BukkitEventListener implements Listener {

        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            final Player p = event.getPlayer();

            // update player name in id lookup
            setPlayerName(p.getUniqueId(), p.getName());
        }
    }

    private static class PlayerData {
        String name;
        UUID id;

        PlayerData(String name, UUID id) {
            this.name = name;
            this.id = id;
        }
    }
}
