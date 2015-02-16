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

package com.jcwhatever.nucleus.providers.npc.events;

import com.jcwhatever.nucleus.providers.npc.INpc;

import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

/**
 * Called when an {@link INpc} is damaged by a block.
 */
public class NpcDamageByBlockEvent extends NpcDamageEvent {

    private static final HandlerList handlers = new HandlerList();

    /**
     * Constructor.
     *
     * @param npc    The NPC the event is for.
     * @param event  The parent {@link EntityDamageByBlockEvent}.
     */
    public NpcDamageByBlockEvent(INpc npc, EntityDamageByBlockEvent event) {
        super(npc, event);
    }

    /**
     * Get the block that caused the damage.
     */
    public Block getBlock() {
        return getParentEvent().getDamager();
    }

    /**
     * Get the parent {@link EntityDamageByBlockEvent}.
     */
    @Override
    public EntityDamageByBlockEvent getParentEvent() {
        return (EntityDamageByBlockEvent)super.getParentEvent();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
