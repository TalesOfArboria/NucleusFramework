package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.collections.java.DequeRunnable;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CircularQueueTest {

    @Test
    public void testDequeInterface() {

        CircularQueue<String> queue = new CircularQueue<>();

        DequeRunnable<String> dequeTest = new DequeRunnable<>(queue, "a", "b", "c");
        dequeTest.run();
    }

    @Test
    public void testNext() throws Exception {

        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.element());

        Assert.assertEquals("2", queue.next());
        Assert.assertEquals("2", queue.element());
    }

    @Test
    public void testPrev() throws Exception {

        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.element());

        Assert.assertEquals("3", queue.prev());
        Assert.assertEquals("3", queue.element());
    }

    @Test
    public void testAddFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.addFirst("test");

        Assert.assertEquals("test", queue.getFirst());
    }

    @Test
    public void testAddLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.addLast("test");

        Assert.assertEquals("test", queue.getLast());
    }

    @Test
    public void testOfferFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.offerFirst("test");

        Assert.assertEquals("test", queue.getFirst());
    }

    @Test
    public void testOfferLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.offerLast("test");

        Assert.assertEquals("test", queue.getLast());
    }

    @Test
    public void testRemoveFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.removeFirst();

        Assert.assertEquals("2", queue.getFirst());
    }

    @Test
    public void testRemoveLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.removeLast();

        Assert.assertEquals("2", queue.getLast());
    }

    @Test
    public void testPollFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.pollFirst();

        Assert.assertEquals("2", queue.getFirst());
    }

    @Test
    public void testPollLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.pollLast();

        Assert.assertEquals("2", queue.getLast());
    }

    @Test
    public void testGetFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.getFirst());
    }

    @Test
    public void testGetLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("3", queue.getLast());
    }

    @Test
    public void testPeekFirst() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.peekFirst());
    }

    @Test
    public void testPeekLast() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("3", queue.peekLast());
    }

    @Test
    public void testRemoveFirstOccurrence() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("1");

        queue.removeFirstOccurrence("1");

        Assert.assertEquals("2", queue.peekFirst());
        Assert.assertEquals("1", queue.peekLast());
    }

    @Test
    public void testRemoveLastOccurrence() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("1");

        queue.removeLastOccurrence("1");

        Assert.assertEquals("1", queue.peekFirst());
        Assert.assertEquals("3", queue.peekLast());
    }

    @Test
    public void testAdd() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");

        Assert.assertEquals("1", queue.peekFirst());
    }

    @Test
    public void testOffer() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.offer("1");

        Assert.assertEquals("1", queue.peekFirst());
    }

    @Test
    public void testRemove() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("1");

        queue.remove("1");

        Assert.assertEquals("2", queue.peekFirst());
        Assert.assertEquals("1", queue.peekLast());
    }

    @Test
    public void testPoll() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("1");

        queue.poll();

        Assert.assertEquals("2", queue.peekFirst());
        Assert.assertEquals("1", queue.peekLast());
    }

    @Test
    public void testElement() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.element());
    }

    @Test
    public void testPeek() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        Assert.assertEquals("1", queue.peek());
    }

    @Test
    public void testPush() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.push("0");

        Assert.assertEquals("0", queue.peekFirst());
    }

    @Test
    public void testPop() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.push("0");

        Assert.assertEquals("0", queue.pop());
    }

    @Test
    public void testRemove1() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.push("0");

        Assert.assertEquals("0", queue.remove());
    }

    @Test
    public void testContainsAll() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Assert.assertEquals(true, queue.containsAll(ArrayUtils.asList("2", "4")));
        Assert.assertEquals(false, queue.containsAll(ArrayUtils.asList("2", "4", "7")));
    }

    @Test
    public void testAddAll() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");

        queue.addAll(ArrayUtils.asList("4", "5"));

        Assert.assertEquals("5", queue.peekLast());

        queue.prev();

        Assert.assertEquals("4", queue.peekLast());
    }

    @Test
    public void testRemoveAll() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        queue.removeAll(ArrayUtils.asList("4", "5"));

        Assert.assertEquals(3, queue.size());

        Assert.assertEquals("3", queue.peekLast());
    }

    @Test
    public void testRetainAll() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        queue.retainAll(ArrayUtils.asList("4", "5"));

        Assert.assertEquals(2, queue.size());

        Assert.assertEquals("5", queue.peekLast());
    }

    @Test
    public void testClear() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        queue.clear();

        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testContains() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Assert.assertEquals(true, queue.contains("1"));
        Assert.assertEquals(true, queue.contains("2"));
        Assert.assertEquals(true, queue.contains("3"));
        Assert.assertEquals(true, queue.contains("4"));
        Assert.assertEquals(true, queue.contains("5"));
        Assert.assertEquals(false, queue.contains("6"));
    }

    @Test
    public void testSize() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Assert.assertEquals(5, queue.size());
    }

    @Test
    public void testIsEmpty() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Assert.assertEquals(false, queue.isEmpty());

        queue.clear();

        Assert.assertEquals(true, queue.isEmpty());
    }

    @Test
    public void testIterator() throws Exception {

        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Iterator<String> iterator = queue.iterator();

        List<String> copy = new ArrayList<>(5);
        while (iterator.hasNext()) {
            copy.add(iterator.next());
        }

        Assert.assertEquals(5, copy.size());
        Assert.assertEquals("1", copy.get(0));
        Assert.assertEquals("2", copy.get(1));
        Assert.assertEquals("3", copy.get(2));
        Assert.assertEquals("4", copy.get(3));
        Assert.assertEquals("5", copy.get(4));



        iterator = queue.iterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testToArray() throws Exception {
        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Object[] array = queue.toArray();
        Object[] expected = new Object[] { "1", "2", "3", "4", "5" };

        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void testToArray1() throws Exception {

        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        String[] array = queue.toArray(new String[queue.size()]);
        String[] expected = new String[] { "1", "2", "3", "4", "5" };

        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void testDescendingIterator() throws Exception {

        CircularQueue<String> queue = new CircularQueue<>();

        queue.add("1");
        queue.add("2");
        queue.add("3");
        queue.add("4");
        queue.add("5");

        Iterator<String> iterator = queue.descendingIterator();

        List<String> copy = new ArrayList<>(5);
        while (iterator.hasNext()) {
            copy.add(iterator.next());
        }

        Assert.assertEquals(5, copy.size());
        Assert.assertEquals("5", copy.get(0));
        Assert.assertEquals("4", copy.get(1));
        Assert.assertEquals("3", copy.get(2));
        Assert.assertEquals("2", copy.get(3));
        Assert.assertEquals("1", copy.get(4));

        iterator = queue.descendingIterator();

        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

        Assert.assertEquals(0, queue.size());

        iterator = queue.descendingIterator();
        Assert.assertEquals(false, iterator.hasNext());

        try {
            iterator.next();
            throw new AssertionError("NoSuchElementException expected.");
        }
        catch (NoSuchElementException ignore){}


        queue.add("1");
        iterator = queue.descendingIterator();
        Assert.assertEquals(true, iterator.hasNext());

        try {
            iterator.remove();
            throw new AssertionError("IllegalStateException expected.");
        }
        catch (IllegalStateException ignore) {}
    }
}