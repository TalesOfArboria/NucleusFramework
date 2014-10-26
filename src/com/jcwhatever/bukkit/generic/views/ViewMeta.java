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



package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * A meta data container used in view instances.
 */
public class ViewMeta {
	
	private Map<Object, Object> _metaMap = new HashMap<Object, Object>(10);

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
