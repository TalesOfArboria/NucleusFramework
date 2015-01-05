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

package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.nucleus.mixins.IPluginOwned;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Tracks a collection of player objects or player representative objects and aids in
 * automatically removing the player entry when the player logs out.
 * <p>
 *     The player collection is responsible for calling {@code #notifyPlayerRemoved}
 *     method when a player is removed from the collection and {@code #notifyPlayerAdded}
 *     when a player is added to the collection.
 * </p>
 */
public final class PlayerCollectionTracker implements IPluginOwned {

    private final IPlayerCollection _collection;
    private final PlayerCollectionListener _listener;
    protected final transient Object _sync = new Object();

    /**
     * Constructor.
     *
     * @param collection  The player collection to track.
     */
    public PlayerCollectionTracker(IPlayerCollection collection) {
        _collection = collection;
        _listener = PlayerCollectionListener.get(collection.getPlugin());
    }

    /**
     * Get the collections owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _collection.getPlugin();
    }

    /**
     * Get the player collection.
     */
    public IPlayerCollection getCollection() {
        return _collection;
    }

    /**
     * Call to notify player listener that the player is
     * no longer in the collection.
     *
     * @param playerId  The player id.
     */
    public void notifyPlayerRemoved(UUID playerId) {
        _listener.removePlayer(playerId, this);
    }

    /**
     * Call to notify the player listener that the player
     * has been added to the collection.
     *
     * @param playerId  The player Id.
     */
    public void notifyPlayerAdded(UUID playerId) {
        _listener.addPlayer(playerId, this);
    }
}
