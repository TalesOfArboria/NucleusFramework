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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder.ListMultimapBuilder;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * A {@code ListMultimap} wrapper that stores elements using the player ID as key.
 *
 * <p> When the player logs out, the entry is automatically removed.</p>
 *
 * <p>Thread safe.</p>
 *
 * <p>The maps iterators must be used inside a synchronized block which locks the
 * map instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 *
 * @param <V>  The value type
 */
public class PlayerListMultimap<V> extends PlayerMultimap<V> {

    ListMultimap<UUID, V> _map;

    /**
     * Constructor.
     *
     * @param plugin   The owning plugin.
     */
    public PlayerListMultimap(Plugin plugin) {
        super(plugin);

        _map = ListMultimapBuilder.hashKeys().arrayListValues().build();
    }

    @Override
    public List<V> get(@Nonnull UUID playerId) {
        return (List<V>)super.get(playerId);
    }

    @Override
    public List<V> removeAll(@Nonnull Object playerId) {
        return (List<V>)super.removeAll(playerId);
    }

    @Override
    public List<V> replaceValues(UUID playerId, Iterable<? extends V> iterable) {
        return (List<V>)super.replaceValues(playerId, iterable);
    }

    @Override
    protected Multimap<UUID, V> map() {
        return _map;
    }
}
