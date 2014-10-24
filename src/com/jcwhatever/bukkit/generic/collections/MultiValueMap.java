package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Allows adding multiple values per key.
 *
 * <p>Also allows getting keys based on value.</p>
 *
 * @param <K> Key type
 * @param <V> Value type
 */
public class MultiValueMap <K, V> {

    // keyed to key type
	Map<K, Set<V>> _valueMap;

    // keyed to value type
	Map<V, Set<K>> _keyMap;

    private int _initialSetSize = 10;


    /**
     * Constructor.
     */
    public MultiValueMap() {
        _valueMap = new HashMap<>(20);
        _keyMap = new HashMap<>(20);
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity of the map.
     */
    public MultiValueMap(int size) {
        PreCon.positiveNumber(size);

        _valueMap = new HashMap<>(size);
        _keyMap = new HashMap<>(size);
    }

    /**
     * Constructor.
     *
     * @param size       The initial capacity of the map.
     * @param entrySize  The initial capacity of the internal collections for each key.
     */
    public MultiValueMap(int size, int entrySize) {
        PreCon.positiveNumber(size);
        PreCon.positiveNumber(entrySize);

        _valueMap = new HashMap<>(size);
        _keyMap = new HashMap<>(size);
        _initialSetSize = entrySize;
    }

    /**
     * Remove all items from the map.
     */
	public void clear() {
		_valueMap.clear();
		_keyMap.clear();
	}

    /**
     * Get the number of values in the map.
     */
    public int valueSize() {
        int size = 0;
        for (Set<V> set : _valueMap.values()) {
            size += set.size();
        }
        return size;
    }

    /**
     * Get the number of keys in the map.
     */
    public int keySize() {
        return _valueMap.size();
    }

    /**
     * Determine if the map contains a key.
     *
     * @param key  The key to check.
     */
	public boolean containsKey(K key) {
        PreCon.notNull(key);

		return _valueMap.containsKey(key);
	}

    /**
     * Determine if the map contains a value.
     *
     * @param value  The value to check for.
     */
	public boolean containsValue(V value) {
        PreCon.notNull(value);

		return _keyMap.containsKey(value);
	}

    /**
     * Get a value associated with a key in the map.
     *
     * <p>Order of values is not guaranteed.</p>
     *
     * @param key  The key to check.
     */
    @Nullable
	public V getValue(K key) {
        PreCon.notNull(key);

		Set<V> set = _valueMap.get(key);
		if (set == null)
			return null;
		
		if (set.isEmpty())
			return null;
		
		return new ArrayList<V>(set).get(0);
	}

    /**
     * Get a list of values associated with the specified key.
     * @param key  The key to check.
     */
    @Nullable
    public List<V> getValues(K key) {
        PreCon.notNull(key);

        Set<V> set = _valueMap.get(key);
        if (set == null)
            return null;

        return new ArrayList<V>(set);
    }

    /**
     * Gets a key associated with the value.
     *
     * <p>Order of keys is not guaranteed.</p>
     *
     * @param value  The value to check.
     */
    @Nullable
	public K getKey(V value) {
        PreCon.notNull(value);

		Set<K> set = _keyMap.get(value);
		if (set == null)
			return null;
		
		if (set.isEmpty())
			return null;
		
		return new ArrayList<K>(set).get(0);
	}

    /**
     * Get a list of keys associated with the specified value.
     *
     * @param value  The value to check.
     */
    @Nullable
	public List<K> getKeys(V value) {
        PreCon.notNull(value);

		Set<K> keys = _keyMap.get(value);
		if (keys == null)
			return null;
		
		return new ArrayList<K>(keys);
	}

    /**
     * Determine if the map is empty.
     */
	public boolean isEmpty() {
		return _valueMap.isEmpty();
	}

    /**
     * Get the keys from the map.
     */
	public Set<K> keySet() {
		return _valueMap.keySet();
	}

    /**
     * Put a value into the map.
     *
     * @param key    The key associated with the value.
     * @param value  The value
     *
     * @return self
     */
	public MultiValueMap<K, V> put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

		Set<V> set = _valueMap.get(key);
		if (set == null) {
			set = new HashSet<V>(_initialSetSize);
			_valueMap.put(key, set);
		}
		
		set.add(value);
		
		return this;
	}

    /**
     * Remove all values associated with a key.
     *
     * @param key  The key to use.
     *
     * @return  Returns removed values, if any.
     */
    @Nullable
	public List<V> remove(K key) {
        PreCon.notNull(key);

		Set<V> values = _valueMap.get(key);
		if (values == null)
			return null;

        for (V value : values) {
            removeValue(value);
        }

        _valueMap.remove(key);

		return new ArrayList<V>(values);
	}

    /**
     * Remove a value from all keys in the map.
     *
     * @param value  The value to remove.
     *
     * @return Number of values removed.
     */
	public int removeValue(V value) {
        PreCon.notNull(value);

		Set<K> keys = _keyMap.get(value);
		if (keys == null)
			return 0;

        int removeCount = 0;
		
		for (K key : keys) {
			Set<V> values = _valueMap.get(key);
			if (values == null)
				continue;
			
			values.remove(value);
            removeCount++;
			
			if (values.size() == 0)
				remove(key);
		}

        return removeCount;
	}

    /**
     * Remove a value from a key value collection.
     *
     * @param key    The key to check.
     * @param value  The value to remove.
     *
     * @return  True if value was found and removed.
     */
    public boolean removeValue(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        Set<V> values = _valueMap.get(key);
        return values != null && values.remove(value);
    }

    /**
     * Get all values in the map.
     */
	public Collection<V> values() {
		return _keyMap.keySet();
	}

}
