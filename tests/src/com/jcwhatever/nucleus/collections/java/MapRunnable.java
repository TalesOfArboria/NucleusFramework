package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Ignore;

import java.util.Map;

/**
 * Test a {@link Map} implementation.
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
@Ignore
public class MapRunnable<K, V> implements Runnable {

    final Map<K, V> _map;
    final K _key1;
    final K _key2;
    final K _key3;
    final V _value1;
    final V _value2;
    final V _value3;

    /**
     * Constructor.
     *
     * @param map     The map to test.
     * @param key1    A key to use for testing.
     * @param key2    A key to use for testing.
     * @param key3    A key to use for testing.
     * @param value1  A value to use for testing.
     * @param value2  A value to use for testing.
     * @param value3  A value to use for testing.
     */
    public MapRunnable(Map<K, V> map, K key1, K key2, K key3, V value1, V value2, V value3) {
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
        assertEquals(true, _map.isEmpty());
        assertEquals(0, _map.size());
        assertEquals(false, _map.containsKey(_key1));
        assertNotNull(_map.toString());

        try {
            assertNull(_map.put(_key1, _value1));
        }
        catch (UnsupportedOperationException e) {
            return;
        }

        assertEquals(_value1, _map.put(_key1, _value2));

        assertEquals(_value2, _map.get(_key1));

        assertEquals(1, _map.size());

        assertEquals(_value2, _map.remove(_key1));

        assertEquals(0, _map.size());

        _map.put(_key1, _value1);
        assertEquals(1, _map.size());

        _map.put(_key2, _value2);
        assertEquals(2, _map.size());

        _map.put(_key3, _value3);
        assertEquals(3, _map.size());

        assertEquals(_value1, _map.get(_key1));
        assertEquals(_value2, _map.get(_key2));
        assertEquals(_value3, _map.get(_key3));

        _map.put(_key1, _value1);
        _map.put(_key2, _value2);
        _map.put(_key2, _value3);
        assertEquals(3, _map.size());
        _map.clear();

        assertEquals(0, _map.size());

        assertEquals(null, _map.get(_key1));
        assertEquals(null, _map.get(_key2));
        assertEquals(null, _map.get(_key3));

        assertEquals(false, _map.containsKey(_key1));
        assertEquals(false, _map.containsKey(_key2));
        assertEquals(false, _map.containsKey(_key3));
    }
}
