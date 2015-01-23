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

import com.jcwhatever.nucleus.collections.wrap.MultimapWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * A {@code Multimap} wrapper that stores elements using the player ID as key.
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
public abstract class PlayerMultimap<V> extends MultimapWrapper<UUID, V> implements IPlayerCollection {

    private final Plugin _plugin;
    private final transient PlayerCollectionTracker _tracker;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     */
    public PlayerMultimap(Plugin plugin) {
        super(SyncStrategy.SYNC);

        PreCon.notNull(plugin);

        _plugin = plugin;
        _tracker = new PlayerCollectionTracker(this);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void removePlayer(Player p) {
        PreCon.notNull(p);

        assert _sync != null;

        synchronized (_sync) {
            map().removeAll(p.getUniqueId());
        }
    }

    @Override
    protected void onPut(UUID key, V value) {
        _tracker.notifyPlayerAdded(key);
    }

    @Override
    protected void onPutAll(UUID key, Iterable<? extends V> values) {
        _tracker.notifyPlayerAdded(key);
    }

    @Override
    protected void onRemove(Object key, Object value) {

        UUID playerId = PlayerUtils.getPlayerId(key);
        if (playerId == null)
            throw new ClassCastException();

        _tracker.notifyPlayerRemoved(playerId);
    }

    @Override
    protected void onRemoveAll(Object key, Collection<V> values) {
        UUID playerId = PlayerUtils.getPlayerId(key);
        if (playerId == null)
            throw new ClassCastException();

        _tracker.notifyPlayerRemoved(playerId);
    }

    @Override
    protected void onClear(Collection<Entry<UUID, V>> entries) {

        for (Entry<UUID, V> entry : entries) {
            _tracker.notifyPlayerRemoved(entry.getKey());
        }
    }
}
