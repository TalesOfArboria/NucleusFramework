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


package com.jcwhatever.bukkit.generic.player;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Utility to get a block selected by a player
 */
public final class PlayerBlockSelect implements Listener {

    private PlayerBlockSelect() {}

    private static Map<UUID, PlayerBlockSelectHandler> _handlers
            = new PlayerMap<PlayerBlockSelectHandler>(GenericsLib.getPlugin());

    private static PlayerBlockSelect _listener;

    /**
     * Wait for the player to click a block and run the handler.
     *
     * @param p        The player.
     * @param handler  The handler to run when the player selects a block.
     */
    public static void query(Player p, PlayerBlockSelectHandler handler) {
        PreCon.notNull(p);
        PreCon.notNull(handler);

        // register event listener if its not already registered.
        if (_listener == null) {
            _listener = new PlayerBlockSelect();

            Bukkit.getPluginManager().registerEvents(_listener, GenericsLib.getPlugin());
        }

        // place handler into map
        _handlers.put(p.getUniqueId(), handler);
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    private void onPlayerSelectBlock(PlayerInteractEvent event) {

        // make sure the event has a block
        if (!event.hasBlock())
            return;

        // make sure the player clicked the block
        if (event.getAction() != Action.LEFT_CLICK_BLOCK &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // see if the player is expected to select a block
        PlayerBlockSelectHandler action = _handlers.remove(event.getPlayer().getUniqueId());
        if (action == null)
            return;

        // cancel event to prevent damage due to selection
        event.setCancelled(true);

        boolean isSelectAccepted = action.onBlockSelect(
                event.getPlayer(), event.getClickedBlock(), event.getAction());

        if (!isSelectAccepted) {

            // place handler back into map to try again
            _handlers.put(event.getPlayer().getUniqueId(), action);
        }
    }

    /**
     * A handler to call when the player selects a block.
     */
    public static abstract class PlayerBlockSelectHandler {
        public abstract boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction);
    }
}

