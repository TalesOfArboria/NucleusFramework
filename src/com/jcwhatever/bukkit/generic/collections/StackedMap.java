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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * Can add multiple values per key except values are placed in a {@Code Stack}.
 * Removing a value by key pops an item off the stack associated with the
 * provided key.
 *
 * @param <K>  Key type
 * @param <V>  Value type
 */
public class StackedMap <K, V> implements Map<K, V> {

	Map<K, Stack<V>> _map;

    /**
     * Constructor.
     */
    public StackedMap() {
        _map = new HashMap<K, Stack<V>>(10);
    }

    /**
     * Constructor.
     *
     * @param size  The initial size.
     */
    public StackedMap(int size) {
        PreCon.positiveNumber(size);

        _map = new HashMap<K, Stack<V>>(size);
    }

    /**
     * Determine if the map is empty.
     */
    @Override
    public boolean isEmpty() {
        return _map.isEmpty();
    }

    /**
     * Clear all items
     */
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

		return _map.containsKey(key);
	}

    /**
     * Determine if the map contains the specified value.
     *
     * @param value  The value to check.
     */
	@Override
	public boolean containsValue(Object value) {
        PreCon.notNull(value);

		for (Stack<V> stack : _map.values()) {
			if (stack.contains(value))
				return true;
		}
		return false;
	}

    /**
     * Unsupported.
     */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

    /**
     * Get a value using the specified key.
     * <p>The value returned is peeked from the stack, if one is found.</p>
     *
     * @param key  The key to check.
     */
	@Override
    @Nullable
	public V get(Object key) {
        PreCon.notNull(key);

		Stack<V> stack = _map.get(key);
		if (stack != null && !stack.isEmpty())
			return stack.peek();
		
		return null;
	}


    /**
     * Get the maps keys.
     */
	@Override
	public Set<K> keySet() {
		return _map.keySet();
	}

    /**
     * Put a value into the map.
     *
     * @param key    The key to use.
     * @param value  The value to add.
     */
	@Override
	public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

		Stack<V> stack = _map.get(key);
		if (stack == null) {
			stack = new Stack<V>();
			_map.put(key, stack);
		}

		stack.push(value);
		
		return value;
	}

    /**
     * Unsupported.
     */
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		throw new UnsupportedOperationException();
	}

    /**
     * Remove a value by key. Pops a value from the internal stack
     * represented by the key.
     *
     * @param key  The key of the stack to pop.
     *
     * @return Returns the popped value, if any.
     */
	@Override
    @Nullable
	public V remove(Object key) {
        PreCon.notNull(key);

		Stack<V> stack = _map.get(key);
		if (stack == null) {
			return null;
		}
		
		if (stack.isEmpty())
			_map.remove(key);

		return stack.pop();
	}

    /**
     * Get the number of entries in the map.
     * Or in other terms the number of keys, or
     * the number of stacks.
     */
	@Override
	public int size() {
		return _map.size();
	}

    /**
     * Get the number of items in the internal stack
     * represented by the specified key.
     *
     * @param key  The key to check.
     */
	public int keySize(Object key) {
        PreCon.notNull(key);

		Stack<V> stack = _map.get(key);
		if (stack == null || stack.isEmpty()) {
			return 0;
		}
		
		return stack.size();
	}

    /**
     * Get all values in the map.
     */
	@Override
	public Collection<V> values() {

		Collection<Stack<V>> values = _map.values();
		Set<V> results = new HashSet<V>(values.size());
		
		for (Stack<V> stack : values) {
			results.addAll(stack);
		}

		return results;
	}

}
