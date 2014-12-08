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



package com.jcwhatever.bukkit.generic.views.data;

import com.jcwhatever.bukkit.generic.utils.MetaKey;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * A meta data container used in view instances.
 */
public class ViewArguments {

    private Map<Object, Object> _argumentMap = new HashMap<Object, Object>(10);

    public ViewArguments(ViewArgument ... arguments) {
        this(null, arguments);
    }

    public ViewArguments(@Nullable ViewArguments merge, ViewArgument ... arguments) {

        if (merge != null) {
            _argumentMap.putAll(merge._argumentMap);
        }

        for (ViewArgument arg : arguments) {
            _argumentMap.put(arg.getKey(), arg.getValue());
        }
    }

    /**
     * Determine if an argument is set.
     *
     * @param key  The argument key.
     */
    public boolean hasArg(MetaKey<?> key) {

        return _argumentMap.containsKey(key);
    }

    /**
     * Get all argument keys that are set.
     */
    public Set<Object> getArgumentKeys() {
        return new HashSet<>(_argumentMap.keySet());
    }

    /**
     * Get an argument value.
     *
     * @param key  The argument key.
     *
     * @param <T>  The value type.
     */
    @Nullable
    public <T> T get(MetaKey<T> key) {
        PreCon.notNull(key);

        @SuppressWarnings("unchecked")
        T value = (T) _argumentMap.get(key);

        return value;
    }

    /**
     * Get an argument value as an object.
     *
     * @param key  The object key.
     */
    @Nullable
    public Object getObject(Object key) {
        PreCon.notNull(key);

        return _argumentMap.get(key);
    }

    protected <T> void set(MetaKey<T> key, T value) {
        if (value == null) {
            _argumentMap.remove(key);
        }
        else {
            _argumentMap.put(key, value);
        }
    }

    void setObject(Object key, Object value) {
        if (value == null) {
            _argumentMap.remove(key);
        }
        else {
            _argumentMap.put(key, value);
        }
    }

    public static class ViewArgument {
        private final Object _key;
        private final Object _value;

        public <T> ViewArgument(MetaKey<T> key, T value) {
            PreCon.notNull(key);
            PreCon.notNull(value);

            _key = key;
            _value = value;
        }

        public Object getKey() {
            return _key;
        }

        public Object getValue() {
            return _value;
        }
    }
}
