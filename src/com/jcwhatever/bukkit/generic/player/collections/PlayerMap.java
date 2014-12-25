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

import com.jcwhatever.bukkit.generic.collections.AbstractIteratorWrapper;
import com.jcwhatever.bukkit.generic.collections.AbstractSetWrapper;
import com.jcwhatever.bukkit.generic.mixins.IPlayerReference;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public class PlayerMap<V> extends AbstractPlayerCollection implements Map<UUID, V> {

	private final transient Map<UUID, V> _map;
	private final transient KeySetWrapper _keyset = new KeySetWrapper();

	private transient boolean _isDisposed;

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
		super(plugin);
		_map = new HashMap<UUID, V>(size);
	}

	@Override
	public synchronized void clear() {

		for (UUID playerId : _map.keySet()) {
			notifyPlayerRemoved(playerId);
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
		return _keyset;
	}

	@Override
	public synchronized V put(UUID key, V value) {
		PreCon.notNull(key);

		notifyPlayerAdded(key);
		return _map.put(key, value);
	}

	@Override
	public synchronized void putAll(Map<? extends UUID, ? extends V> pairs) {
		PreCon.notNull(pairs);

		for (UUID playerId : pairs.keySet()) {
			notifyPlayerAdded(playerId);
		}
		_map.putAll(pairs);
	}

	@Override
	public synchronized V remove(Object key) {
		PreCon.notNull(key);

		V item = _map.remove(key);

		if (key instanceof UUID) {
			notifyPlayerRemoved((UUID)key);
		}

		return item;
	}

	@Override
	public synchronized int size() {
		return _map.size();
	}

	@Override
	public synchronized Collection<V> values() {
		return _map.values();
	}

	@Override
	public synchronized void removePlayer(Player p) {
		_map.remove(p.getUniqueId());
	}

	@Override
	public boolean isDisposed() {
		return _isDisposed;
	}

	/**
	 * Call to remove references that prevent
	 * the garbage collector from collecting
	 * the instance after it is no longer needed.
	 */
	@Override
	public void dispose() {
		clear();
		_isDisposed = true;
	}

	private class KeySetWrapper extends AbstractSetWrapper<UUID> {

		@Override
		public boolean add(UUID e) {
			if (_map.keySet().add(e)) {
				notifyPlayerAdded(e);
				return true;
			}
			return false;
		}

		@Override
		public boolean remove(Object o) {
			UUID id = null;
			if (o instanceof UUID) {
				id = (UUID)o;
			}
			else if (o instanceof Player) {
				id = ((Player)o).getUniqueId();
			}
			else if (o instanceof IPlayerReference) {
				Player player = ((IPlayerReference)o).getPlayer();
				if (player != null) {
					id =player.getUniqueId();
				}
			}

			if (id == null)
				return false;

			if (_map.keySet().remove(id)) {
				notifyPlayerRemoved(id);
				return true;
			}

			return false;
		}

		@Override
		public boolean addAll(Collection<? extends UUID> c) {
			boolean isChanged = false;
			for (UUID id : c) {
				isChanged = isChanged || add(id);
			}
			return isChanged;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean isChanged = false;
			for (Object id : c) {
				isChanged = isChanged || remove(id);
			}
			return isChanged;
		}

		@Override
		public boolean retainAll(Collection<?> c) {

			Set<?> removed = new HashSet<>(this);
			for (Object obj : c) {
				removed.remove(obj);
			}

			boolean isChanged = false;

			for (Object obj : removed) {
				isChanged = isChanged || remove(obj);
			}

			return isChanged;
		}

		@Override
		public void clear() {
			PlayerMap.this.clear();
		}

		@Override
		public Iterator<UUID> iterator() {
			return new KeySetIteratorWrapper();
		}

		@Override
		protected Collection<UUID> getCollection() {
			return _map.keySet();
		}
	}

	private class KeySetIteratorWrapper extends AbstractIteratorWrapper<UUID> {

		Iterator<UUID> iterator = _map.keySet().iterator();

		@Override
		public void remove() {

			notifyPlayerRemoved(_current);

			iterator.remove();
		}

		@Override
		protected Iterator<UUID> getIterator() {
			return iterator;
		}
	}
}
