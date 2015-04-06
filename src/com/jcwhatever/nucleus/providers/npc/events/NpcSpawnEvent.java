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

import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when an {@link INpc} is spawned.
 */
public class NpcSpawnEvent extends NpcEvent implements Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerList();

    private final NpcSpawnReason _reason;
    private boolean _isCancelled;

    /**
     * Specifies a reason for an NPC to spawn.
     */
    public enum NpcSpawnReason {

        /**
         * The NPC was invoked to spawn by a plugin or script.
         */
        INVOKED,
        /**
         * The NPC is being respawned.
         */
        RESPAWN,
        /**
         * The NPC is being respawned due to the chunk that its in being loaded.
         */
        CHUNK_LOAD,
        /**
         * The NPC is being respawned due to the world that its in being loaded.
         */
        WORLD_LOAD
    }

    /**
     * Constructor.
     *
     * @param npc  The NPC the event is for.
     */
    public NpcSpawnEvent(INpc npc, NpcSpawnReason reason) {
        super(npc);

        PreCon.notNull(reason);

        _reason = reason;
    }

    /**
     * Get the reason the NPC is being spawned.
     */
    public NpcSpawnReason getReason() {
        return _reason;
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
