/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A hash map that holds weak references to the values. Key/Value pairs
 * are automatically removed if the value is garbage collected.
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class WeakValueMap<K, V> implements Map<K, V> {

	private Map<K, WeakValue> _map;

    /**
     * Constructor.
     */
    public WeakValueMap() {
        _map = new HashMap<K, WeakValue>(20);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity
     */
    public WeakValueMap(int size) {
        PreCon.positiveNumber(size);

        _map = new HashMap<K, WeakValue>(size);
    }

	@Override
	public void clear() {
		_map.clear();
	}

    /**
     * Determine if the map contains the specified key.
     *
     * @param key  The key to check.
     */
	@Override
	public boolean containsKey(Object key) {
        PreCon.notNull(key);

		WeakValue item = _map.get(key);
		if (item == null)
			return false;

		if (item.get() == null) {
			_map.remove(key);
			return false;
		}

		return true;
	}


	@Override
	public boolean containsValue(Object value) {
        PreCon.notNull(value);

		return _map.containsValue(value);
	}

    /**
     * Get the maps entry set.
     */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {

		Set<Map.Entry<K, V>> entrySet = new HashSet<Map.Entry<K, V>>(_map.entrySet().size());

		for (final Map.Entry<K, WeakValue> set : _map.entrySet()) {
			entrySet.add(new Map.Entry<K, V>() {

				@Override
				public K getKey() {
					return set.getKey();
				}

				@Override
				public V getValue() {
					return set.getValue().get();
				}

				@Override
				public V setValue(V value) {
					return set.setValue(new WeakValue(value)).get();
				}
			});
		}
		
		return entrySet;
	}

    /**
     * Get a value by key.
     *
     * @param key  The key to use.
     */
	@Override
	public V get(Object key) {
        PreCon.notNull(key);

		WeakValue item = _map.get(key);
		if (item == null)
			return null;
		
		if (item.get() == null) {
			_map.remove(key);
			return null;
		}
		
		return item.get();
	}

    /**
     * Determine if the map is empty.
     */
	@Override
	public boolean isEmpty() {
		return _map.isEmpty();
	}

    /**
     * Get the maps keyset.
     */
	@Override
	public Set<K> keySet() {
		return _map.keySet();
	}

    /**
     * Put a value into the map.
     * @param key    The key for the value.
     * @param value  The value.
     */
	@Override
	public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

		WeakValue item = _map.put(key, new WeakValue(value));
		if (item == null)
			return null;
		
		return item.get();
	}

    /**
     * Put all items from the specified map.
     *
     * @param map  The map.
     */
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
        PreCon.notNull(map);

		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			_map.put(entry.getKey(), new WeakValue(entry.getValue()));
		}
	}

    /**
     * Remove an item by key.
     *
     * @param key  The key to use.
     */
	@Override
	public V remove(Object key) {
        PreCon.notNull(key);

		WeakValue item = _map.remove(key);
		if (item == null)
			return null;
		
		return item.get();
	}

    /**
     * The size of the map.
     */
	@Override
	public int size() {
		return _map.size();
	}

    /**
     * Get all values in the map.
     */
	@Override
	public Collection<V> values() {

		Collection<WeakValue> values = _map.values();
        List<V> list = new ArrayList<V>(values.size());

		for (WeakValue value : values) {
			if (value.get() != null)
				list.add(value.get());
		}
		
		return list;
	}

	private class WeakValue {

		private WeakReference<V> _reference;
		private int _hash;

		WeakValue(V value) {
			_reference = new WeakReference<V>(value);
			_hash = value.hashCode();
		}

		public V get() {
			return _reference.get();
		}

		@Override
		public int hashCode() {
			return _hash;
		}

		@Override
		public boolean equals(Object obj) {
            PreCon.notNull(obj);

            return _reference.get() != null && _reference.get().equals(obj);
        }
	}

}
