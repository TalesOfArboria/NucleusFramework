package com.jcwhatever.nucleus.collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.collections.java.IterableRunnable;
import com.jcwhatever.nucleus.collections.java.QueueRunnable;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* 
 * 
 */
public class ArrayQueueTest {


    private static void fillQueue(ArrayQueue<String> queue) {
        queue.add("test");
        queue.add("test2");
        queue.add("test3");
        queue.add("test4");
        queue.add(null);
    }

    @Test
    public void testSize() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        assertEquals(0, queue.size());

        queue.add("test");

        assertEquals(1, queue.size());

    }

    @Test
    public void testContains() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        queue.add("test");

        assertEquals(true, queue.contains("test"));
        assertEquals(false, queue.contains("test2"));
        assertEquals(false, queue.contains(null));

        queue.add(null);

        assertEquals(true, queue.contains("test"));
        assertEquals(false, queue.contains("test2"));
        assertEquals(true, queue.contains(null));
    }

    @Test
    public void testIterator() throws Exception {

        final ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        IterableRunnable<String> runner = new IterableRunnable<>(queue, 5, new Runnable() {
            @Override
            public void run() {
                fillQueue(queue);
            }
        });

        runner.run();
    }

    @Test
    public void testToArray() throws Exception {
        final ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        Object[] array = queue.toArray();

        assertEquals(5, array.length);
        assertArrayEquals(new Object[]{
                "test", "test2", "test3", "test4", null

        }, array);
    }

    @Test
    public void testToArray1() throws Exception {

        final ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        String[] array = new String[5];

        queue.toArray(array);

        assertEquals(5, array.length);
        assertArrayEquals(new Object[]{
                "test", "test2", "test3", "test4", null

        }, array);
    }

    @Test
    public void testAdd() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        assertEquals(0, queue.size());

        queue.add("test");

        assertEquals(1, queue.size());

        queue.add("test2");
        queue.add("test3");

        assertEquals(3, queue.size());
    }

    @Test
    public void testRemove() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        assertEquals(5, queue.size());

        queue.remove("n");

        assertEquals(5, queue.size());

        queue.remove(null);
        queue.remove("test");

        assertEquals(3, queue.size());
    }

    @Test
    public void testRemove1() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        assertEquals(5, queue.size());

        assertEquals("test", queue.remove());
        assertEquals(4, queue.size());

        assertEquals("test2", queue.remove());
        assertEquals(3, queue.size());

        assertEquals("test3", queue.remove());
        assertEquals(2, queue.size());

        assertEquals("test4", queue.remove());
        assertEquals(1, queue.size());

        assertEquals(null, queue.remove());
        assertEquals(0, queue.size());
    }

    @Test
    public void testContainsAll() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);
        fillQueue(queue);

        assertEquals(true, queue.containsAll(ArrayUtils.asList("test", "test2", null)));

        assertEquals(false, queue.containsAll(ArrayUtils.asList("test", "test2", "a")));
    }

    @Test
    public void testAddAll() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        assertEquals(0, queue.size());

        assertEquals(true, queue.addAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9", "test10", "test11", "test12", "test13", "test14"
        )));

        assertEquals(14, queue.size());
    }

    @Test
    public void testRemoveAll() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        assertEquals(0, queue.size());

        queue.addAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9", "test10", "test11", "test12", "test13", "test14"
        ));

        assertEquals(14, queue.size());

        assertEquals(true, queue.removeAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7"
        )));

        assertEquals(7, queue.size());
    }

    @Test
    public void testRetainAll() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        assertEquals(0, queue.size());

        queue.addAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9", "test10", "test11", "test12", "test13", "test14"
        ));

        assertEquals(14, queue.size());

        assertEquals(true, queue.retainAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7"
        )));

        assertEquals(7, queue.size());

        assertEquals(true, queue.containsAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7"
        )));
    }

    @Test
    public void testClear() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        queue.addAll(ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9", "test10", "test11", "test12", "test13", "test14"
        ));

        queue.clear();

        assertEquals(true, queue.isEmpty());
    }

    @Test
    public void testQueue() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(1);

        QueueRunnable<String> runner = new QueueRunnable<>(queue, "test1", "test2", "test3");
        runner.run();
    }

    @Test
    public void testExpansionAddAll() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(10);
        List<String> list = ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9"
        );

        queue.addAll(new ArrayList<String>(list));

        Iterator<String> iterator = queue.iterator();
        int total = 0;
        int current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current), val);
            current++;
            total++;
        }

        assertEquals(list.size(), total);

        queue.addAll(list);

        iterator = queue.iterator();
        total = 0;
        current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current % list.size()), val);
            current++;
            total++;
        }

        assertEquals(list.size() * 2, total);

        for (String listVal : list) {
            String val = queue.remove();
            assertEquals(listVal, val);
        }

        iterator = queue.iterator();
        total = 0;
        current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current), val);
            current++;
            total++;
        }

        assertEquals(list.size(), total);
    }


    @Test
    public void testExpansionAdd() throws Exception {

        ArrayQueue<String> queue = new ArrayQueue<>(10);
        List<String> list = ArrayUtils.asList(
                "test", "test2", "test3", "test4", "test5", "test6", "test7",
                "test8", "test9"
        );

        for (String listVal : list)
            queue.add(listVal);

        Iterator<String> iterator = queue.iterator();
        int total = 0;
        int current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current), val);
            current++;
            total++;
        }

        assertEquals(list.size(), total);

        for (String listVal : list)
            queue.add(listVal);

        iterator = queue.iterator();
        total = 0;
        current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current % list.size()), val);
            current++;
            total++;
        }

        assertEquals(list.size() * 2, total);

        for (String listVal : list) {
            String val = queue.remove();
            assertEquals(listVal, val);
        }

        iterator = queue.iterator();
        total = 0;
        current = 0;
        while (iterator.hasNext()) {
            String val = iterator.next();
            assertEquals(list.get(current), val);
            current++;
            total++;
        }

        assertEquals(list.size(), total);
    }
}