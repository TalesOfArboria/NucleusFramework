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

package com.jcwhatever.nucleus.internal.managed.scripting;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Global meta map accessible by all scripts.
 *
 * <p>One instance used for all scripts.</p>
 */
public class GlobalMeta implements IDisposable {

    private static final Map<String, Object> _globalMap = new HashMap<>(40);

    static void reset() {
        _globalMap.clear();
    }

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Set a meta value.
     *
     * @param key    The meta key.
     * @param value  The meta value.
     *
     * @return  The previous value.
     */
    @Nullable
    public Object set(String key, @Nullable Object value) {
        PreCon.notNull(key);

        if (value == null)
            return _globalMap.remove(key);

        return _globalMap.put(key, value);
    }

    /**
     * Get a meta value.
     *
     * @param key  The meta key.
     *
     * @return  The value or null if not set.
     */
    @Nullable
    public Object get(String key) {
        return _globalMap.get(key);
    }
}