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

package com.jcwhatever.bukkit.generic.player.collections;

import com.jcwhatever.bukkit.generic.mixins.IDisposable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Collection that contains player references that must be
 * removed when a player logs out.
 * <p>
 *     The implementation is responsible for calling {@code notifyPlayerRemoved}
 *     method when a player is removed from the collection and {@code notifyPlayerAdded}
 *     when a player is added to the collection.
 * </p>
 * <p>
 *     The abstract method {@code removePlayer} should not call {@code notifyPlayerRemoved}
 *     or {@code notifyPlayerAdded} because it's used to remove the player when
 *     the player logs out.
 * </p>
 * <p>
 *     The collection should by synchronized since the {@code removePlayer} method
 *     may be called from an asynchronous thread.
 * </p>
 */
public abstract class AbstractPlayerCollection implements IDisposable {

    private final Plugin _plugin;
    private final PlayerCollectionListener _listener;

    protected AbstractPlayerCollection(Plugin plugin) {
        _plugin = plugin;
        _listener = PlayerCollectionListener.get(plugin);
    }

    /**
     * Get the collections owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Called to remove the specified player
     * from the collection.
     *
     * @param p  The player to remove.
     */
    public abstract void removePlayer(Player p);

    /**
     * Call to notify player listener that the player is
     * no longer in the collection.
     *
     * @param playerId  The player id.
     */
    protected final void notifyPlayerRemoved(UUID playerId) {
        _listener.removePlayer(playerId, this);
    }

    /**
     * Call to notify the player listener that the player
     * has been added to the collection.
     *
     * @param playerId  The player Id.
     */
    protected final void notifyPlayerAdded(UUID playerId) {
        _listener.addPlayer(playerId, this);
    }
}
