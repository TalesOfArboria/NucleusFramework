package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.bukkit.v1_8_R2.MockPlugin;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.collections.java.ListRunnable;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.junit.Test;

public class TimedArrayListTest {

    @Test
    public void testListInterface() {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedArrayList<String> list = new TimedArrayList<String>(plugin);

        ListRunnable<String> listTest = new ListRunnable<>(list, "a", "b", "c");
        listTest.run();
    }

    @Test
    public void testElementLifespan() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedArrayList<String> list = new TimedArrayList<String>(plugin);

        list.add("a", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            assertEquals(list.contains("a"), System.currentTimeMillis() < expires);

            Thread.sleep(5);
        }
    }

    @Test
    public void testElementLifespan1() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedArrayList<String> list = new TimedArrayList<String>(plugin);

        list.add("a", 20, TimeScale.TICKS);

        BukkitTester.pause(22);

        assertEquals(0, list.size());
    }
}