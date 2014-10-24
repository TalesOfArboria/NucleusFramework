package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A meta data container used in view instances.
 */
public class ViewMeta {
	
	private Map<Object, Object> _metaMap = new HashMap<Object, Object>();

    /**
     * Set a meta data key value.
     *
     * @param key    The object key.
     * @param value  The key value.
     *
     * @return  Own instance.
     */
	public ViewMeta setMeta(Object key, @Nullable Object value) {
        PreCon.notNull(key);

		if (value == null) {
			_metaMap.remove(key);
			return this;
		}
		_metaMap.put(key, value);
		
		return this;
	}

    /**
     * Get a meta data key value.
     *
     * @param key  The object key.
     *
     * @param <T>  The value type.
     */
    @Nullable
	@SuppressWarnings("unchecked")
	public <T> T getMeta(Object key) {
        PreCon.notNull(key);

		return (T)_metaMap.get(key);
	}

    /**
     * Get a meta data key value.
     *
     * @param key  The object key.
     */
    @Nullable
	public Object getMetaObject(Object key) {
        PreCon.notNull(key);

		return _metaMap.get(key);
	}
	
	
}
