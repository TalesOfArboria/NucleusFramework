package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.ListIterator;

/**
 * Test a {@link ListIterator} implementation.
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class ListIteratorRunnable<E> implements Runnable {

    final ListIterator<E> _iterator;
    final List<E> _values;
    final E _value1;
    final E _value2;

    /**
     * Constructor.
     *
     * @param iterator  The iterator to test.
     * @param values    The values contained in the iterator. at least 3 values required.
     * @param value1    A value to insert for testing.
     * @param value2    A value to insert for testing.
     */
    public ListIteratorRunnable(ListIterator<E> iterator, List<E> values, E value1, E value2) {
        _iterator = iterator;
        _values = values;
        _value1 = value1;
        _value2 = value2;

        if (values.size() <= 2)
            throw new AssertionError("A size greater than 2 is required for proper testing.");
    }

    @Override
    public void run() {

        assertEquals(false, _iterator.hasPrevious());

        assertEquals(true, _iterator.hasNext());
        assertEquals(_values.get(0), _iterator.next());

        if (_iterator.hasPrevious()) {
            assertEquals(true, _iterator.hasPrevious());

            assertEquals(true, _iterator.hasNext());
            assertEquals(_values.get(1), _iterator.next());

            assertEquals(true, _iterator.hasPrevious());
            assertEquals(_values.get(1), _iterator.previous());

            assertEquals(true, _iterator.hasPrevious());
            assertEquals(_values.get(0), _iterator.previous());

            assertEquals(false, _iterator.hasPrevious());

            _iterator.next();

        } else {
            try {
                _iterator.previous();
                throw new AssertionError("UnsupportedOperationException expected.");
            } catch (UnsupportedOperationException ignore) {}
        }

        //test add
        try {
            _iterator.add(_value2);
            assertEquals(true, _iterator.hasPrevious());
            assertEquals(_value2, _iterator.previous());
        }
        catch (UnsupportedOperationException ignore) {}

        // test set
        try {

            _iterator.set(_value1);
            _iterator.next();
            assertEquals(_value1, _iterator.previous());

        }
        catch(UnsupportedOperationException ignore) {}


        try {
            try {
                _iterator.add(_value2);
                // next or previous must be called before calling set after add
                _iterator.set(_value1);
                throw new AssertionError("IllegalStateException expected.");
            } catch (IllegalStateException ignore) {}

            // test remove: must be called after calling next or previous
            try {
                _iterator.remove();
                throw new AssertionError("IllegalStateException expected.");
            } catch (IllegalStateException ignore) {
            }

            // go to beginning
            while (_iterator.hasPrevious())
                _iterator.previous();

            // test remove
            _iterator.next();
            _iterator.remove();

        }
        catch(UnsupportedOperationException ignore) {}
    }
}
