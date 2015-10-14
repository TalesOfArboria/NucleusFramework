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

package com.jcwhatever.nucleus.events.teleport;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a teleport is scheduled.
 *
 * @see com.jcwhatever.nucleus.managed.teleport.Teleporter
 * @see com.jcwhatever.nucleus.managed.teleport.ITeleportManager
 */
public class TeleportScheduledEvent extends PlayerEvent implements ICancellable, Cancellable {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), TeleportScheduledEvent.class);

    private final Location _to;
    private int _delay;
    private boolean _isCancelled;

    /**
     * Constructor.
     *
     * @param player  The player scheduled to be teleported.
     * @param to      The location to be teleported to.
     * @param delay   The teleport delay in ticks.
     */
    public TeleportScheduledEvent(Player player, Location to, int delay) {
        super(player);
        PreCon.notNull(player);
        PreCon.notNull(to);

        _to = to;
        _delay = delay;
    }

    /**
     * Get the teleport delay in ticks.
     */
    public int getDelayTicks() {
        return _delay;
    }

    /**
     * Set the teleport delay.
     *
     * @param ticks  The number of ticks to delay.
     */
    public void setDelayTicks(int ticks) {
        _delay = ticks;
    }

    /**
     * Get a direct reference to the location the player will be teleported to.
     */
    public Location getTo() {
        return _to;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

