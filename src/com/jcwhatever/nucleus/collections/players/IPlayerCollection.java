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

import org.bukkit.entity.Player;

/**
 * An interface that must be implemented in order to use a collection with a
 * {@code PlayerCollectionTracker}, which aids in automatically removing player entries.
 * <p>
 *     The player collection implementation is responsible for instantiating a new
 *     {@code PlayerCollectionTracker} for itself to use. It also responsible for
 *     calling {@code PlayerCollectionTracker#notifyPlayerRemoved} method when a player
 *     is removed from the collection and {@code PlayerCollectionTracker#notifyPlayerAdded}
 *     when a player is added to the collection.
 * </p>
 * <p>
 *     The method {@code #removePlayer} should not call {@code PlayerCollectionTracker#notifyPlayerRemoved}
 *     or {@code PlayerCollectionTracker#notifyPlayerAdded} because it's used to remove the player when
 *     the player logs out.
 * </p>
 * <p>
 *     The collection should by synchronized since the {@code #removePlayer} method
 *     may be called from an asynchronous thread.
 * </p>
 */
public interface IPlayerCollection extends IPluginOwned {

    /**
     * Called to automatically remove the specified player
     * from the collection.
     *
     * @param p  The player to remove.
     */
    void removePlayer(Player p);

    /**
     * Called to clear all elements from the collection.
     */
    void clear();
}
