package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Test a {@link Deque} implementation.
 *
 * <p>Also runs {@link QueueTest}, which runs {@link CollectionTest} and
 * {@link IterableTest}.</p>
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class DequeTest<E> implements Runnable {

    final Deque<E> _queue;
    final E _value1;
    final E _value2;
    final E _value3;

    /**
     * Constructor.
     *
     * @param queue   The queue to test.
     * @param value1  A value to use for testing.
     * @param value2  A value to use for testing.
     * @param value3  A value to use for testing.
     */
    public DequeTest(Deque<E> queue, E value1, E value2, E value3) {
        this._queue = queue;
        this._value1 = value1;
        this._value2 = value2;
        this._value3 = value3;
    }

    @Override
    public void run() {
        QueueTest<E> test = new QueueTest<>(_queue, _value1, _value2, _value3);
        test.run();

        _queue.clear();
        assertEquals(true, _queue.isEmpty());


        // test addFirst
        _queue.addFirst(_value1);
        assertEquals(1, _queue.size());
        assertEquals(false, _queue.isEmpty());
        assertEquals(true, _queue.contains(_value1));

        // test addFirst (2nd element)
        _queue.addFirst(_value2);
        assertEquals(2, _queue.size());
        assertEquals(true, _queue.contains(_value2));

        // test pollFirst
        assertEquals(_value2, _queue.pollFirst());
        assertEquals(true, _queue.contains(_value1));
        assertEquals(false, _queue.contains(_value2));

        // test removeFirst
        assertEquals(_value1, _queue.removeFirst());
        assertEquals(false, _queue.contains(_value1));
        assertEquals(false, _queue.contains(_value2));

        assertEquals(0, _queue.size());

        // test addLast
        _queue.addLast(_value1);
        assertEquals(1, _queue.size());
        assertEquals(true, _queue.contains(_value1));

        // test addLast
        _queue.addLast(_value2);
        assertEquals(2, _queue.size());
        assertEquals(true, _queue.contains(_value2));

        // test pollLast
        assertEquals(_value2, _queue.pollLast());
        assertEquals(1, _queue.size());
        assertEquals(true, _queue.contains(_value1));
        assertEquals(false, _queue.contains(_value2));

        // test removeLast
        assertEquals(_value1, _queue.removeLast());
        assertEquals(0, _queue.size());
        assertEquals(false, _queue.contains(_value1));
        assertEquals(false, _queue.contains(_value2));


        // test pollFirst on empty deque
        assertEquals(null, _queue.pollFirst());

        // test removeFirst on empty deque
        try {
            _queue.removeFirst();
            throw new AssertionError("NoSuchElementException expected.");
        }
        catch (NoSuchElementException ignore) {}

        // test pollLast on empty deque
        assertEquals(null, _queue.pollLast());

        // test removeLast on empty deque
        try {
            _queue.removeLast();
            throw new AssertionError("NoSuchElementException expected.");
        }
        catch (NoSuchElementException ignore) {}
    }
}
