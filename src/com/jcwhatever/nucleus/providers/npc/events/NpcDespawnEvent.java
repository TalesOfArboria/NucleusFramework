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
 * Called when an {@link INpc} is despawned.
 */
public class NpcDespawnEvent extends NpcEvent implements Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerList();

    private final NpcDespawnReason _reason;
    private boolean _isCancelled;

    /**
     * Specifies a reason for the NPC being despawned.
     */
    public enum NpcDespawnReason {
        /**
         * The NPC was invoked to despawn by a plugin or script.
         */
        INVOKED,
        /**
         * The NPC is being respawned.
         */
        RESPAWN,
        /**
         * The NPC's spawned entity has died.
         */
        DEATH,
        /**
         * The chunk the NPC is in has unloaded.
         */
        CHUNK_UNLOAD,
        /**
         * The world the NPC is in has unloaded.
         */
        WORLD_UNLOAD,
        /**
         * The NPC is being disposed.
         */
        DISPOSED
    }

    /**
     * Constructor.
     *
     * @param npc  The NPC the event is for.
     */
    public NpcDespawnEvent(INpc npc, NpcDespawnReason reason) {
        super(npc);

        PreCon.notNull(reason);

        _reason = reason;
    }

    /**
     * Get the reason the NPC was despawned.
     */
    public NpcDespawnReason getReason() {
        return _reason;
    }

    /**
     * Determine if the event can be cancelled.
     *
     * <p>Event is only cancellable if the reason for despawn is
     * {@link NpcDespawnReason#INVOKED} or {@link NpcDespawnReason#CHUNK_UNLOAD}.</p>
     */
    public boolean isCancellable() {
        return _reason == NpcDespawnReason.INVOKED ||
                _reason == NpcDespawnReason.CHUNK_UNLOAD;
    }

    @Override
    public boolean isCancelled() {
        return isCancellable() && _isCancelled;
    }

    /**
     * Set the cancelled state.
     *
     * <p>Check the {@link #isCancellable} method returns true
     * before using.</p>
     *
     * @param isCancelled  True to cancel.
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        PreCon.isValid(isCancellable(), "Event is not cancellable");

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
