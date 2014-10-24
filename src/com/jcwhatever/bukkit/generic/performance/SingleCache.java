package com.jcwhatever.bukkit.generic.performance;

import javax.annotation.Nullable;

public class SingleCache <K, V> {

	private K _key;
	private V _value;
	private boolean _hasValue = false;
	
	public boolean keyEquals(@Nullable Object key) {
		if (_key == null)
			return false;
		
		return _key.equals(key);
	}

    @Nullable
	public K getKey() {
		return _key;
	}

    @Nullable
	public V getValue() {
		return _value;
	}
	
	public void set(K key, @Nullable V value) {
		_key = key;
		_value = value;
		_hasValue = true;
	}
	
	public void reset() {
		_key = null;
		_value = null;
		_hasValue = false;
	}

	public boolean hasValue() {
		return _hasValue;
	}
	
}
