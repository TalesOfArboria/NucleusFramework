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

package com.jcwhatever.nucleus.internal.blockselect;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelectHandler;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelector;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

/**
 * Internal implementation of {@link IBlockSelector}.
 */
public final class InternalBlockSelector implements IBlockSelector, Listener {

    private final Map<UUID, IBlockSelectHandler> _handlers = new PlayerMap<>(Nucleus.getPlugin());

    public InternalBlockSelector() {
        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    public boolean isSelecting(Player player) {
        PreCon.notNull(player);

        return _handlers.containsKey(player.getUniqueId());
    }

    @Override
    public void query(Player player, IBlockSelectHandler handler) {
        PreCon.notNull(player);
        PreCon.notNull(handler);

        // place handler into map
        _handlers.put(player.getUniqueId(), handler);
    }

    @Override
    public void cancel(Player player) {
        PreCon.notNull(player);

        _handlers.remove(player.getUniqueId());
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
        IBlockSelectHandler action = _handlers.remove(event.getPlayer().getUniqueId());
        if (action == null)
            return;

        // cancel event to prevent damage due to selection
        event.setCancelled(true);

        BlockSelectResult result = action.onBlockSelect(
                event.getPlayer(), event.getClickedBlock(), event.getAction());

        if (result == BlockSelectResult.CONTINUE) {

            // place handler back into map to try again
            _handlers.put(event.getPlayer().getUniqueId(), action);
        }
    }
}
