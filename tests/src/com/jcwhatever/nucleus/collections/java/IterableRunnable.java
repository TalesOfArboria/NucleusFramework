package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Test an {@link Iterable} implementation.
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class IterableRunnable<E> implements Runnable {

    final Iterable<E> _iterable;
    final int _size;
    final Runnable _refill;

    /**
     * Constructor.
     *
     * @param iterable  The iterable to test.
     * @param size      The number of elements in the iterable.
     * @param refill    A runnable that can refill the elements of iterable.
     */
    public IterableRunnable(Iterable<E> iterable, int size, Runnable refill) {
        _iterable = iterable;
        _size = size;
        _refill = refill;

        if (size <= 0)
            throw new AssertionError("A size greater than zero is required for proper testing.");
    }

    @Override
    public void run() {

        Iterator<E> iterator = _iterable.iterator();

        // test iterator hasNext, next through entire collection
        for (int i=0; i < _size; i++) {
            assertEquals(true, iterator.hasNext());
            assertNotNull(iterator.next());
        }

        // test hasNext after iteration complete
        assertEquals(false, iterator.hasNext());

        // test next after iteration complete
        try {
            iterator.next();
            throw new AssertionError("NoSuchElementException expected.");
        }
        catch (NoSuchElementException ignore) {}

        try {

            iterator = _iterable.iterator();

            // test remove through entire iterator
            for (int i = 0; i < _size; i++) {
                assertEquals(true, iterator.hasNext());

                iterator.next();
                iterator.remove();
            }

            iterator = _iterable.iterator();

            // test remove on empty iterator, hasNext and next not called.
            try {
                iterator.remove();
                throw new AssertionError("IllegalStateException expected.");
            } catch (IllegalStateException ignore) {
            }

            _refill.run();

            iterator = _iterable.iterator();
            assertEquals(true, iterator.hasNext());

            // test remove, hasNext called.
            try {
                iterator.remove();
                throw new AssertionError("IllegalStateException expected.");
            } catch (IllegalStateException ignore) {
            }

            _refill.run();

            iterator = _iterable.iterator();

            // test remove, hasNext NOT called.
            try {
                iterator.remove();
                throw new AssertionError("IllegalStateException expected.");
            } catch (IllegalStateException ignore) {
            }
        }
        catch (UnsupportedOperationException ignore) {}
    }

}
