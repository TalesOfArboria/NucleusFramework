package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.DequeTest;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class DecayListTest {

    @Test
    public synchronized void testDequeInterface() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        DecayList<String> list = new DecayList<String>(plugin);

        DequeTest<String> test = new DequeTest<>(list, "a", "b", "c");
        test.run();
    }

    @Test
    public synchronized void testPreventDecay() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        DecayList<String> list = new DecayList<String>(plugin);

        list.clear();

        long next = System.currentTimeMillis() + 500; // half second

        // check decay prevented by adding before 1 seconds has elapsed (blocking should not matter)
        for (int i=0; i < 20; i++) {

            while (System.currentTimeMillis() < next) {

                NucleusInit.heartBeat();

                Thread.sleep(10);
            }

            list.add(String.valueOf(i));
            assertEquals(i + 1, list.size());

            next = System.currentTimeMillis() + 500;
        }
    }

    @Test
    public synchronized void testAllowDecay() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        DecayList<String> list = new DecayList<String>(plugin);

        list.clear();

        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals(3, list.size());

        Set<Integer> sizes = new HashSet<>(3);
        Set<String> concatedValues = new HashSet<>(3);

        long timeout = System.currentTimeMillis() + (1000 * 5);// 5 seconds

        while(!list.isEmpty() && System.currentTimeMillis() < timeout) {

            NucleusInit.heartBeat();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sizes.add(list.size());
            concatedValues.add(TextUtils.concat(list, ","));
        }

        //System.out.println(TextUtils.concat(sizes, ", "));
        //System.out.println(TextUtils.concat(concatedValues, " | "));

        assertEquals(4, sizes.size());
        assertEquals(4, concatedValues.size());

        assertEquals(true, sizes.contains(0));
        assertEquals(true, sizes.contains(1));
        assertEquals(true, sizes.contains(2));
        assertEquals(true, sizes.contains(3));
    }
}