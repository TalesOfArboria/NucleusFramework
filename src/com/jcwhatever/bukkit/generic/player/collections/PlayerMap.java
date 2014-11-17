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

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A {@code HashMap} that stores elements using the player ID as key.
 * <p>
 *     When the player logs out, the entry is automatically removed.
 * </p>
 *
 * @param <V>  The value type
 */
public class PlayerMap<V> implements Map<UUID, V>, IPlayerCollection {

	private final Map<UUID, V> _map;
	private final PlayerCollectionListener _listener;

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
		_map = new HashMap<UUID, V>(size);
		_listener = PlayerCollectionListener.get(plugin);
	}

	@Override
	public Plugin getPlugin() {
		return _listener.getPlugin();
	}

	@Override
	public synchronized void clear() {

		for (UUID playerId : _map.keySet()) {
			_listener.removePlayer(playerId, this);
		}

		_map.clear();
	}

	@Override
	public synchronized boolean containsKey(Object key) {
		return _map.containsKey(key);
	}

	@Override
	public synchronized boolean containsValue(Object value) {
		return _map.containsValue(value);
	}

	@Override
	public synchronized Set<Entry<UUID, V>> entrySet() {
		return new HashSet<Entry<UUID, V>>(_map.entrySet());
	}

	@Override
	public synchronized V get(Object key) {
		return _map.get(key);
	}

	public synchronized V get(Player p) {
		return _map.get(p.getUniqueId());
	}

	@Override
	public synchronized boolean isEmpty() {
		return _map.isEmpty();
	}

	@Override
	public synchronized Set<UUID> keySet() {
		return new HashSet<UUID>(_map.keySet());
	}

	@Override
	public synchronized V put(UUID key, V value) {

		_listener.addPlayer(key, this);
		return _map.put(key, value);
	}

	@Override
	public synchronized void putAll(Map<? extends UUID, ? extends V> pairs) {
		for (UUID playerId : pairs.keySet()) {
			_listener.addPlayer(playerId, this);
		}
		_map.putAll(pairs);
	}

	@Override
	public synchronized V remove(Object key) {
		V item = _map.remove(key);

		if (key instanceof UUID) {
			_listener.removePlayer((UUID)key, this);
		}

		return item;
	}

	@Override
	public synchronized int size() {
		return _map.size();
	}

	@Override
	public synchronized Collection<V> values() {
		return new ArrayList<V>(_map.values());
	}

	@Override
	public synchronized void removePlayer(Player p) {
		_map.remove(p.getUniqueId());
	}


	/**
	 * Call to remove references that prevent
	 * the garbage collector from collecting
	 * the instance after it is no longer needed.
	 */
	@Override
	public void dispose() {
		clear();
	}
}
