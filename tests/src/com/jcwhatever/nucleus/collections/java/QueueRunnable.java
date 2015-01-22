package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.ArrayUtils;

import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Test a {@link Queue} implementation.
 *
 * <p>Also runs {@link CollectionRunnable} on the queue.</p>
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class QueueRunnable<E> implements Runnable {

    final Queue<E> _queue;
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
    public QueueRunnable(Queue<E> queue, E value1, E value2, E value3) {
        this._queue = queue;
        this._value1 = value1;
        this._value2 = value2;
        this._value3 = value3;
    }

    @Override
    public void run() {

        CollectionRunnable<E> test = new CollectionRunnable<E>(_queue, _value1, _value2, _value3);
        test.run();

        _queue.clear();

        // test queue empty
        assertEquals(0, _queue.size());
        assertEquals(true, _queue.isEmpty());

        // test queue add
        assertEquals(true, _queue.add(_value1));
        assertEquals(1, _queue.size());
        assertEquals(false, _queue.isEmpty());

        // add all
        assertEquals(true, _queue.addAll(ArrayUtils.asList(_value2, _value3)));
        assertEquals(3, _queue.size());

        // test clear
        _queue.clear();
        assertEquals(0, _queue.size());
        assertEquals(true, _queue.isEmpty());

        // test offer
        assertEquals(true, _queue.offer(_value1));
        assertEquals(1, _queue.size());
        assertEquals(false, _queue.isEmpty());

        // test remove
        assertEquals(_value1, _queue.remove());
        assertEquals(0, _queue.size());
        assertEquals(true, _queue.isEmpty());

        // test element
        _queue.add(_value3);
        assertEquals(_value3, _queue.element());

        // test peek
        assertEquals(_value3, _queue.peek());

        // test poll
        assertEquals(_value3, _queue.poll());
        assertEquals(0, _queue.size());

        // test poll against empty queue
        assertEquals(null, _queue.poll());

        // test remove against empty queue
        try {
            _queue.remove();
            throw new AssertionError("NoSuchElementException expected.");
        }
        catch (NoSuchElementException ignore) {}
    }
}
