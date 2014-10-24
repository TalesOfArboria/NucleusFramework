package com.jcwhatever.bukkit.generic.performance;

import java.util.HashMap;
import java.util.Map;

public class MultiCache<K, V> {

	private Map<String, SingleCache<K, V>> _cache = new HashMap<String, SingleCache<K, V>>();
	
	public void set(String cacheName, K key, V value) {
		SingleCache<K, V> cache = getCache(cacheName, true);
		if (cache == null)
			return;
		
		cache.set(key,  value);
	}
	
	public boolean keyEquals(String cacheName, K key) {
		SingleCache<K, V> cache = getCache(cacheName, false);
		if (cache == null)
			return false;
		
		return cache.keyEquals(key);
	}
	
	public void reset(String cacheName) {
		SingleCache<K, V> cache = getCache(cacheName, false);
		if (cache == null)
			return;
		
		cache.reset();
	}
	
	public void clear() {
		_cache.clear();
	}

	public SingleCache<K, V> getCache(String cacheName) {
		return getCache(cacheName, false);
	}
	
	private SingleCache<K, V> getCache(String cacheName, boolean addIfNotExists) {
		
		SingleCache<K, V> cache = _cache.get(cacheName);
		
		if (addIfNotExists && cache == null) {
			cache = new SingleCache<K, V>();
			_cache.put(cacheName, cache);
		}
		
		return cache;
	}
	
}
