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
 * A meta data container used to pass arguments into
 * view instances.
 */
public class ViewArguments {

    private Map<Object, Object> _argumentMap = new HashMap<Object, Object>(10);

    /**
     * Constructor.
     *
     * @param arguments  View arguments to add.
     */
    public ViewArguments(ViewArgument ... arguments) {
        this(null, arguments);
    }

    /**
     * Constructor.
     *
     * @param merge      Optional existing view arguments to add.
     * @param arguments  View arguments to add. Overrides merged values.
     */
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

    /**
     * Called to set a value. Allows extended types to
     * change values.
     *
     * @param key    The meta key.
     * @param value  The value to set.
     *
     * @param <T>  The meta value type.
     */
    protected <T> void set(MetaKey<T> key, @Nullable T value) {
        PreCon.notNull(key);

        if (value == null) {
            _argumentMap.remove(key);
        }
        else {
            _argumentMap.put(key, value);
        }
    }

    /**
     * A single view argument.
     */
    public static class ViewArgument {
        private final Object _key;
        private final Object _value;

        /**
         * Constructor.
         *
         * @param key    The meta key.
         * @param value  The meta value.
         *
         * @param <T>  The meta value type.
         */
        public <T> ViewArgument(MetaKey<T> key, T value) {
            PreCon.notNull(key);
            PreCon.notNull(value);

            _key = key;
            _value = value;
        }

        /**
         * Get the meta key.
         */
        public Object getKey() {
            return _key;
        }

        /**
         * Get the meta value.
         */
        public Object getValue() {
            return _value;
        }
    }
}
