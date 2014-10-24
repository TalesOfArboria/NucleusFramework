package com.jcwhatever.bukkit.generic.performance;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import javax.annotation.Nullable;

public class DualKeySingleCache <K1, K2, V> {

    private K1 _key1;
    private K2 _key2;
    private V _value;
    private boolean _hasValue = false;

    public boolean keyEquals(@Nullable Object key1, @Nullable Object key2) {
        if (_key1 == null || _key2 == null)
            return false;

        return _key1.equals(key1) && _key2.equals(key2);
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
    public V getValue() {
        return _value;
    }

    public void set(K1 key1, K2 key2, @Nullable V value) {
        PreCon.notNull(key1);
        PreCon.notNull(key2);

        _key1 = key1;
        _key2 = key2;
        _value = value;
        _hasValue = true;
    }

    public void reset() {
        _key1 = null;
        _key2 = null;
        _value = null;
        _hasValue = false;
    }

    public boolean hasValue() {
        return _hasValue;
    }

}
