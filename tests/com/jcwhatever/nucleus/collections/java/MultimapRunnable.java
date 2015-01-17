package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Multimap;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import java.util.Collection;
import java.util.Map.Entry;

/*
 * 
 */
public class MultimapRunnable<V> implements Runnable {

    final Multimap<String, V> _map;
    final V _value1;
    final V _value2;
    final V _value3;

    public MultimapRunnable(Multimap<String, V> map, V value1, V value2, V value3) {
        _map = map;
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
        _map.put("a", _value1);
        _map.put("a", _value2);
        assertEquals(2, _map.size());
        assertEquals(true, _map.containsKey("a"));
        assertEquals(true, _map.containsEntry("a", _value1));
        assertEquals(true, _map.containsEntry("a", _value2));
        assertEquals(false, _map.containsEntry("a", _value3));

        // test removeAll
        Collection<V> removed =  _map.removeAll("a");
        assertEquals(2, removed.size());
        assertEquals(true, removed.contains(_value1));
        assertEquals(true, removed.contains(_value2));
        assertEquals(false, removed.contains(_value3));

        assertEquals(0, _map.size());

        // test putAll
        assertEquals(true, _map.putAll("a", ArrayUtils.asList(_value1, _value2, _value3)));
        assertEquals(3, _map.size());
        assertEquals(true, _map.containsKey("a"));
        assertEquals(true, _map.containsEntry("a", _value1));
        assertEquals(true, _map.containsEntry("a", _value2));
        assertEquals(true, _map.containsEntry("a", _value3));


        // test replaceValues
        Collection<V> previous = _map.replaceValues("a", ArrayUtils.asList(_value1));
        assertEquals(3, previous.size());
        assertEquals(true, _map.containsKey("a"));
        assertEquals(true, _map.containsEntry("a", _value1));
        assertEquals(false, _map.containsEntry("a", _value2));
        assertEquals(false, _map.containsEntry("a", _value3));

        _map.clear();
        assertEquals(0, _map.size());

        SetRunnable<String> setTest = new SetRunnable<String>(_map.keySet(), "a", "b", "c");
        setTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        CollectionRunnable<V> valuesTest = new CollectionRunnable<V>(_map.values(), _value1, _value2, _value3);
        valuesTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        CollectionRunnable<Entry<String, V>> entryTest = new CollectionRunnable<>(_map.entries(),
                getEntry("a", _value1), getEntry("b", _value2), getEntry("c", _value3));
        valuesTest.run();

        _map.clear();
        assertEquals(0, _map.size());

        MapRunnable<Collection<V>> mapTest = new MapRunnable<>(_map.asMap(),
                ArrayUtils.asList(_value1),
                ArrayUtils.asList(_value1, _value2),
                ArrayUtils.asList(_value1, _value2, _value3));
        mapTest.run();

    }


    private Entry<String, V> getEntry(final String key, final V value) {
        return new Entry<String, V>() {
            V val = value;

            @Override
            public String getKey() {
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
