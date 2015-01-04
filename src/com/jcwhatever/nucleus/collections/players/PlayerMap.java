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

import com.jcwhatever.nucleus.collections.wrappers.AbstractIteratorWrapper;
import com.jcwhatever.nucleus.collections.wrappers.AbstractSetWrapper;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.utils.PreCon;

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
public class PlayerMap<V> implements IPlayerCollection, Map<UUID, V> {

	private final Plugin _plugin;
	private final Map<UUID, V> _map;
	private final transient KeySetWrapper _keyset = new KeySetWrapper();
	private final transient PlayerCollectionTracker _tracker;
	private final transient Object _sync = new Object();

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
		_plugin = plugin;
		_map = new HashMap<UUID, V>(size);
		_tracker = new PlayerCollectionTracker(this);
	}

	@Override
	public Object getSync() {
		return _sync;
	}

	@Override
	public Plugin getPlugin() {
		return _plugin;
	}

	@Override
	public synchronized void clear() {

		for (UUID playerId : _map.keySet()) {
			_tracker.notifyPlayerRemoved(playerId);
		}

		_map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		synchronized (_sync) {
			return _map.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		synchronized (_sync) {
			return _map.containsValue(value);
		}
	}

	@Override
	public Set<Entry<UUID, V>> entrySet() {
		synchronized (_sync) {
			return new HashSet<Entry<UUID, V>>(_map.entrySet());
		}
	}

	@Override
	public V get(Object key) {
		synchronized (_sync) {
			return _map.get(key);
		}
	}

	public V get(Player p) {
		synchronized (_sync) {
			return _map.get(p.getUniqueId());
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (_sync) {
			return _map.isEmpty();
		}
	}

	@Override
	public Set<UUID> keySet() {
		synchronized (_sync) {
			return _keyset;
		}
	}

	@Override
	public V put(UUID key, V value) {
		PreCon.notNull(key);

		synchronized (_sync) {
			_tracker.notifyPlayerAdded(key);
			return _map.put(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends UUID, ? extends V> pairs) {
		PreCon.notNull(pairs);

		synchronized (_sync) {
			for (UUID playerId : pairs.keySet()) {
				_tracker.notifyPlayerAdded(playerId);
			}
			_map.putAll(pairs);
		}
	}

	@Override
	public V remove(Object key) {
		PreCon.notNull(key);

		synchronized (_sync) {
			V item = _map.remove(key);

			if (key instanceof UUID) {
				_tracker.notifyPlayerRemoved((UUID) key);
			}

			return item;
		}
	}

	@Override
	public int size() {
		synchronized (_sync) {
			return _map.size();
		}
	}

	@Override
	public Collection<V> values() {
		synchronized (_sync) {
			return _map.values();
		}
	}

	@Override
	public void removePlayer(Player p) {
		synchronized (_sync) {
			_map.remove(p.getUniqueId());
		}
	}

	private class KeySetWrapper extends AbstractSetWrapper<UUID> {

		@Override
		public boolean add(UUID e) {
			synchronized (_sync) {
				if (_map.keySet().add(e)) {
					_tracker.notifyPlayerAdded(e);
					return true;
				}
				return false;
			}
		}

		@Override
		public boolean remove(Object o) {
			synchronized (_sync) {
				UUID id = null;
				if (o instanceof UUID) {
					id = (UUID) o;
				} else if (o instanceof Player) {
					id = ((Player) o).getUniqueId();
				} else if (o instanceof IPlayerReference) {
					Player player = ((IPlayerReference) o).getPlayer();
					if (player != null) {
						id = player.getUniqueId();
					}
				}

				if (id == null)
					return false;

				if (_map.keySet().remove(id)) {
					_tracker.notifyPlayerRemoved(id);
					return true;
				}

				return false;
			}
		}

		@Override
		public boolean addAll(Collection<? extends UUID> c) {
			synchronized (_sync) {
				boolean isChanged = false;
				for (UUID id : c) {
					isChanged = add(id) || isChanged;
				}
				return isChanged;
			}
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			synchronized (_sync) {
				boolean isChanged = false;
				for (Object id : c) {
					isChanged = remove(id) || isChanged;
				}
				return isChanged;
			}
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			synchronized (_sync) {
				Set<?> removed = new HashSet<>(this);
				for (Object obj : c) {
					removed.remove(obj);
				}

				boolean isChanged = false;

				for (Object obj : removed) {
					isChanged = remove(obj) || isChanged;
				}

				return isChanged;
			}
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
		protected Set<UUID> getSet() {
			return _map.keySet();
		}
	}

	private class KeySetIteratorWrapper extends AbstractIteratorWrapper<UUID> {

		Iterator<UUID> iterator = _map.keySet().iterator();

		@Override
		public void remove() {
			synchronized (_sync) {
				_tracker.notifyPlayerRemoved(_current);

				iterator.remove();
			}
		}

		@Override
		protected Iterator<UUID> getIterator() {
			return iterator;
		}
	}
}
