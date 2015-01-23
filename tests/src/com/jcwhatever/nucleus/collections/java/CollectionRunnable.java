package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Ignore;

import java.util.Collection;
import java.util.Iterator;

/**
 * Test a {@link Collection} implementation.
 *
 * <p>Also runs {@link IterableRunnable}.</p>
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
@Ignore
public class CollectionRunnable<E> implements Runnable {

    final Collection<E> _collection;
    final E _value1;
    final E _value2;
    final E _value3;

    /**
     * Constructor.
     *
     * @param collection  The collection to test.
     * @param value1      A value to use for testing.
     * @param value2      A value to use for testing.
     * @param value3      A value to use for testing.
     */
    public CollectionRunnable(Collection<E> collection, E value1, E value2, E value3) {
        _collection = collection;
        _value1 = value1;
        _value2 = value2;
        _value3 = value3;
    }

    @Override
    public void run() {

        // test empty
        assertEquals(true, _collection.isEmpty());
        assertEquals(0, _collection.size());
        assertNotNull(_collection.toString());

        try {
            // test add 1 value
            assertEquals(true, _collection.add(_value1));
            assertEquals(false, _collection.isEmpty());
            assertEquals(1, _collection.size());
            assertEquals(true, _collection.contains(_value1));

            // test remove 1 value
            assertEquals(true, _collection.remove(_value1));
            assertEquals(true, _collection.isEmpty());
            assertEquals(0, _collection.size());
            assertEquals(false, _collection.contains(_value1));

            // test add 2 values
            assertEquals(true, _collection.add(_value1));
            assertEquals(true, _collection.add(_value2));
            assertEquals(2, _collection.size());
            assertEquals(false, _collection.isEmpty());
            assertEquals(true, _collection.contains(_value1));
            assertEquals(true, _collection.contains(_value2));

            // test containsAll
            assertEquals(true, _collection.containsAll(ArrayUtils.asList(_value1, _value2)));

            // test iterator
            Iterator<E> iterator = _collection.iterator();

            int size = 0;
            synchronized (_collection) { // prevent exceptions from iterators that require external sync

                // test iterator hasNext and next
                assertEquals(true, iterator.hasNext());
                assertNotNull(iterator.next());

                // test iterator remove
                try {
                    iterator.remove();
                    assertEquals(1, _collection.size());
                } catch (UnsupportedOperationException ignore) {
                    size++;
                }

                // test iterator hasNext and next (second iteration)
                assertEquals(true, iterator.hasNext());
                assertNotNull(iterator.next());

                // test iterator remove (second iteration)
                try {
                    iterator.remove();
                    assertEquals(0, _collection.size());
                } catch (UnsupportedOperationException ignore) {
                    size++;
                }

                // test iterator hasNext (iteration complete)
                assertEquals(false, iterator.hasNext());
            }

            // test iterator remove valid
            assertEquals(size, _collection.size());
            assertEquals(false, _collection.contains(_value1));
            assertEquals(false, _collection.contains(_value2));

            // make sure collection has values
            if (size == 0) {
                _collection.add(_value1);
                _collection.add(_value2);
                assertEquals(2, _collection.size());
            }

            // test clear
            _collection.clear();
            assertEquals(0, _collection.size());

            // test addAll
            _collection.addAll(ArrayUtils.asList(_value1, _value2, _value3));
            assertEquals(3, _collection.size());

            // test removeAll
            _collection.removeAll(ArrayUtils.asList(_value2, _value3));
            assertEquals(1, _collection.size());
            assertEquals(true, _collection.contains(_value1));
            assertEquals(false, _collection.contains(_value2));
            assertEquals(false, _collection.contains(_value3));

            // test toArray
            Object[] array = _collection.toArray();
            assertArrayEquals(new Object[] { _value1 }, array);

            _collection.toArray(array);
            assertArrayEquals(new Object[] { _value1 }, array);

            _collection.add(_value2);
            _collection.add(_value3);
            assertEquals(3, _collection.size());

            // test retainAll
            _collection.retainAll(ArrayUtils.asList(_value1, _value2));
            assertEquals(2, _collection.size());
            assertEquals(true, _collection.contains(_value1));
            assertEquals(true, _collection.contains(_value2));
            assertEquals(false, _collection.contains(_value3));

            IterableRunnable<E> iterableTest = new IterableRunnable<>(_collection, _collection.size(), new Runnable() {
                @Override
                public void run() {
                    _collection.add(_value1);
                    _collection.add(_value2);
                    _collection.add(_value3);
                }
            });
            iterableTest.run();

            _collection.clear();

        }
        catch (UnsupportedOperationException ignore) {}

    }
}
