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


package com.jcwhatever.bukkit.generic.performance;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

public class TripleKeySingleCache <K1, K2, K3, V> {

    private K1 _key1;
    private K2 _key2;
    private K3 _key3;
    private V _value;
    private boolean _hasValue = false;

    public boolean keyEquals(@Nullable Object key1, @Nullable Object key2, @Nullable Object key3) {
        if (_key1 == null || _key2 == null || _key3 == null)
            return false;

        return _key1.equals(key1) && _key2.equals(key2) && _key3.equals(key3);
    }

    @Nullable
    public K1 getKey1() {
        return _key1;
    }

    @Nullable
    public K2 getKey2() {
        return _key2;
    }

    @Nullable
    public K3 getKey3() {
        return _key3;
    }

    @Nullable
    public V getValue() {
        return _value;
    }

    public void set(K1 key1, K2 key2, K3 key3, @Nullable V value) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);
        PreCon.notNull(key3);

        _key1 = key1;
        _key2 = key2;
        _key3 = key3;
        _value = value;
        _hasValue = true;
    }

    public void reset() {
        _key1 = null;
        _key2 = null;
        _key3 = null;
        _value = null;
        _hasValue = false;
    }

    public boolean hasValue() {
        return _hasValue;
    }
}
