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
import com.jcwhatever.nucleus.providers.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import javax.annotation.Nullable;

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
        IDataNode nameData = getNameData();

        synchronized (_dataSync) {

            for (IDataNode node : nameData) {
                String name = node.getString("");

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

        IDataNode nameData = getNameData();

        synchronized (_dataSync) {
            return nameData.getString(playerId.toString());
        }
    }

    /*
     * Update the NucleusFramework player name/player id map.
     */
    private void setPlayerName(UUID playerId, String name) {
        PreCon.notNull(playerId);
        PreCon.notNullOrEmpty(name);

        IDataNode nameData = getNameData();

        String currentName = getPlayerName(playerId);

        if (name.equals(currentName))
            return;

        synchronized (_dataSync) {
            nameData.set(playerId.toString(), name);
        }

        nameData.save();
    }

    // get the node that contains player id/name data.
    private IDataNode getNameData() {

        if (_nameData == null) {

            synchronized (_dataSync) {
                if (_nameData != null)
                    return _nameData;

                IDataNode data = DataStorage.get(Nucleus.getPlugin(), new DataPath("player-names"));
                if (!data.load()) {
                    NucMsg.warning("Failed to load player names file.");
                }
                _nameData = data;
            }
        }
        return _nameData;
    }

    private class BukkitEventListener implements Listener {

        @EventHandler(priority= EventPriority.LOW)
        private void onPlayerJoin(PlayerJoinEvent event) {

            final Player p = event.getPlayer();

            // update player name in id lookup
            setPlayerName(p.getUniqueId(), p.getName());
        }
    }
}
