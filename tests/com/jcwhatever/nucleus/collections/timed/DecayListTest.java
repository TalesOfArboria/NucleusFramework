package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.BukkitTest;
import com.jcwhatever.bukkit.MockPlugin;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.collections.java.DequeRunnable;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class DecayListTest {

    @Test
    public synchronized void testDequeInterface() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        DecayList<String> list = new DecayList<String>(plugin);

        DequeRunnable<String> test = new DequeRunnable<>(list, "a", "b", "c");
        test.run();
    }

    @Test
    public synchronized void testPreventDecay() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        DecayList<String> list = new DecayList<String>(plugin);

        list.clear();
        // check decay prevented by adding before 1 seconds has elapsed (blocking should not matter)
        for (int i=0; i < 20; i++) {

            BukkitTest.pause(10); // 10 ticks

            list.add(String.valueOf(i));
            assertEquals(i + 1, list.size());
        }
    }

    @Test
    public synchronized void testAllowDecay() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
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

            BukkitTest.heartBeat();

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