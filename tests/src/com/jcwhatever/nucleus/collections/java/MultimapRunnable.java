package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Multimap;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import java.util.Collection;
import java.util.Map.Entry;

/*
 * 
 */
public class MultimapRunnable<K, V> implements Runnable {

    final Multimap<K, V> _map;
    final K _key1;
    final K _key2;
    final K _key3;
    final V _value1;
    final V _value2;
    final V _value3;

    public MultimapRunnable(Multimap<K, V> map, K key1, K key2, K key3, V value1, V value2, V value3) {
        _map = map;
        _key1 = key1;
        _key2 = key2;
        _key3 = key3;
        _value1 = value1;
        _value2 = value2;
        _value3 = value3;
    }

    @Override
    public void run() {

        _map.clear();

        assertEquals(0, _map.size());
        assertEquals(true, _map.isEmpty());

        // test put
        _map.put(_key1, _value1);
        _map.put(_key1, _value2);
        assertEquals(2, _map.size());
        assertEquals(true, _map.containsKey(_key1));
        assertEquals(true, _map.containsEntry(_key1, _value1));
        assertEquals(true, _map.containsEntry(_key1, _value2));
        assertEquals(false, _map.containsEntry(_key1, _value3));

        // test removeAll
        Collection<V> removed =  _map.removeAll(_key1);
        assertEquals(2, removed.size());
        assertEquals(true, removed.contains(_value1));
        assertEquals(true, removed.contains(_value2));
        assertEquals(false, removed.contains(_value3));

        assertEquals(0, _map.size());

        // test putAll
        assertEquals(true, _map.putAll(_key1, ArrayUtils.asList(_value1, _value2, _value3)));
        assertEquals(3, _map.size());
        assertEquals(true, _map.containsKey(_key1));
        assertEquals(true, _map.containsEntry(_key1, _value1));
        assertEquals(true, _map.containsEntry(_key1, _value2));
        assertEquals(true, _map.containsEntry(_key1, _value3));


        // test replaceValues
        Collection<V> previous = _map.replaceValues(_key1, ArrayUtils.asList(_value1));
        assertEquals(3, previous.size());
        assertEquals(true, _map.containsKey(_key1));
        assertEquals(true, _map.containsEntry(_key1, _value1));
        assertEquals(false, _map.containsEntry(_key1, _value2));
        assertEquals(false, _map.containsEntry(_key1, _value3));

        _map.clear();
        assertEquals(0, _map.size());

        SetRunnable<K> setTest = new SetRunnable<>(_map.keySet(), _key1, _key2, _key3);
        setTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        CollectionRunnable<V> valuesTest = new CollectionRunnable<V>(_map.values(), _value1, _value2, _value3);
        valuesTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        CollectionRunnable<Entry<K, V>> entryTest = new CollectionRunnable<>(_map.entries(),
                getEntry(_key1, _value1), getEntry(_key2, _value2), getEntry(_key3, _value3));
        entryTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        MapRunnable<K, Collection<V>> mapTest = new MapRunnable<>(_map.asMap(),
                _key1, _key2, _key3,
                ArrayUtils.asList(_value1),
                ArrayUtils.asList(_value1, _value2),
                ArrayUtils.asList(_value1, _value2, _value3));
        mapTest.run();

    }


    private Entry<K, V> getEntry(final K key, final V value) {
        return new Entry<K, V>() {
            V val = value;

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return val;
            }

            @Override
            public V setValue(V value) {
                V prev = val;
                val = value;
                return prev;
            }
        };
    }
}
