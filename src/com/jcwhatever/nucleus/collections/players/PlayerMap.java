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

import com.jcwhatever.nucleus.collections.wrap.MapWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A {@link HashMap} that stores elements using the player ID as key.
 * <p>
 *     When the player logs out, the entry is automatically removed.
 * </p>
 *
 * <p>Thread safe.</p>
 *
 * <p>The maps iterators must be used inside a synchronized block which locks the
 * map instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 *
 * @param <V>  The value type
 */
public class PlayerMap<V> extends MapWrapper<UUID, V> implements IPlayerCollection {

	private final Plugin _plugin;
	private final Map<UUID, V> _map;
	private final transient PlayerCollectionTracker _tracker;

	/**
	 * Constructor.
	 */
	public PlayerMap(Plugin plugin) {
		this(plugin, 50);
	}

	/**
	 * Constructor.
	 */
	public PlayerMap(Plugin plugin, int size) {
		super(SyncStrategy.SYNC);

		PreCon.notNull(plugin);

		_plugin = plugin;
		_map = new HashMap<UUID, V>(size);
		_tracker = new PlayerCollectionTracker(this);
	}

	@Override
	protected void onPut(UUID key, V value) {
		_tracker.notifyPlayerAdded(key);
	}

	@Override
	protected void onRemove(Object key, V removed) {

		UUID playerId;

		if (key instanceof UUID) {
			playerId = (UUID)key;
		}
		else if (key instanceof Player) {
			playerId = ((Player) key).getUniqueId();
		}
		else if (key instanceof IPlayerReference) {
			playerId = ((IPlayerReference) key).getPlayer().getUniqueId();
		}
		else {
			throw new ClassCastException();
		}

		_tracker.notifyPlayerRemoved(playerId);
	}

	@Override
	protected void onClear(Collection<Entry<UUID, V>> entries) {

		for (Entry<UUID, V> entry : entries) {
			_tracker.notifyPlayerRemoved(entry.getKey());
		}
	}

	@Override
	public Plugin getPlugin() {
		return _plugin;
	}

	public V get(Player p) {
		return super.get(p.getUniqueId());
	}

	public V get(IPlayerReference p) {
		return super.get(p.getPlayer().getUniqueId());
	}

	@Override
	public void removePlayer(UUID playerId) {
		assert _sync != null;

		synchronized (_sync) {
			_map.remove(playerId);
		}
	}

	@Override
	protected Map<UUID, V> map() {
		return _map;
	}
}
