package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;

import java.util.Map;

/**
 * Test a {@link Map} implementation.
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
@Ignore
public class MapTest<V> implements Runnable {

    final Map<String, V> _map;
    final V _value1;
    final V _value2;
    final V _value3;

    /**
     * Constructor.
     *
     * @param map     The map to test.
     * @param value1  A value to use for testing.
     * @param value2  A value to use for testing.
     * @param value3  A value to use for testing.
     */
    public MapTest(Map<String, V> map, V value1, V value2, V value3) {
        _map = map;
        _value1 = value1;
        _value2 = value2;
        _value3 = value3;
    }

    @Override
    public void run() {
        assertTrue(_map.isEmpty());
        assertEquals(0, _map.size());
        assertFalse(_map.containsKey("a"));
        assertNotNull(_map.toString());

        try {
            assertNull(_map.put("a", _value1));
        }
        catch (UnsupportedOperationException e) {
            return;
        }

        assertEquals(_value1, _map.put("a", _value2));

        assertEquals(_value2, _map.get("a"));

        assertEquals(1, _map.size());

        assertEquals(_value2, _map.remove("a"));

        assertEquals(0, _map.size());

        _map.put("a", _value1);
        assertEquals(1, _map.size());
        _map.put("b", _value2);
        assertEquals(2, _map.size());
        _map.put("c", _value3);
        assertEquals(3, _map.size());

        assertEquals(_value1, _map.get("a"));
        assertEquals(_value2, _map.get("b"));
        assertEquals(_value3, _map.get("c"));

        _map.put("a", _value1);
        _map.put("b", _value2);
        _map.put("c", _value3);
        assertEquals(3, _map.size());
        _map.clear();

        assertEquals(0, _map.size());

        assertEquals(null, _map.get("a"));
        assertEquals(null, _map.get("b"));
        assertEquals(null, _map.get("c"));
    }
}
