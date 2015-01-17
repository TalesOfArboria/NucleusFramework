package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

/**
 * Tests a {@link Set} implementation.
 *
 * <p>Also runs {@link CollectionRunnable} on the set.</p>
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class SetRunnable<E> implements Runnable {

    final Set<E> _set;
    final E _value1;
    final E _value2;
    final E _value3;

    /**
     * Constructor.
     *
     * @param set     The set to test.
     * @param value1  A value to use for testing.
     * @param value2  A value to use for testing.
     * @param value3  A value to use for testing.
     */
    public SetRunnable(Set<E> set, E value1, E value2, E value3) {
        _set = set;
        _value1 = value1;
        _value2 = value2;
        _value3 = value3;
    }

    @Override
    public void run() {
        CollectionRunnable<E> collectionTest = new CollectionRunnable<E>(_set, _value1, _value2, _value3);
        collectionTest.run();

        try {
            _set.clear();
            assertEquals(true, _set.isEmpty());
            assertEquals(0, _set.size());
            assertNotNull(_set.toString());


            _set.add(_value1);
            _set.add(_value1);
            _set.add(_value1);
        }
        catch (UnsupportedOperationException e) {
            return;
        }

        assertEquals(1, _set.size());


        _set.add(_value2);
        assertEquals(2, _set.size());


        assertEquals(true, _set.contains(_value1));
        assertEquals(true, _set.contains(_value2));


        _set.remove(_value2);

        assertEquals(1, _set.size());
        assertEquals(false, _set.contains(_value2));

        _set.clear();
        assertEquals(true, _set.isEmpty());
        assertEquals(0, _set.size());
    }
}
