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


package com.jcwhatever.bukkit.generic.events.bukkit.floatingitems;

import com.jcwhatever.bukkit.generic.items.floating.FloatingItem;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FloatingItemPickUpEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final FloatingItem _item;
    private final Player _player;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param item    The item being despawned.
     * @param player  The player picking up the item.
     */
    public FloatingItemPickUpEvent(FloatingItem item, Player player) {
        PreCon.notNull(item);
        PreCon.notNull(player);

        _item = item;
        _player = player;
    }

    /**
     * Get the floating item that is being despawned.
     */
    public FloatingItem getFloatingItem() {
        return _item;
    }

    /**
     * Get the player who is picking up the item.
     */
    public Player getPlayer() {
        return _player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }
}

