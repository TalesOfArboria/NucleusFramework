package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.BukkitTest;
import com.jcwhatever.bukkit.MockPlugin;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.collections.java.MapRunnable;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.junit.Test;

public class TimedHashMapTest {


    @Test
    public void testMapInterface() {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedHashMap<String, String> map = new TimedHashMap<String, String>(plugin);

        MapRunnable<String> mapTest = new MapRunnable<String>(map, "a", "b", "c");

        mapTest.run();
    }

    /**
     * Make sure entry is removed.
     */
    @Test
    public void testEntryLifespan() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedHashMap<String, String> map = new TimedHashMap<String, String>(plugin);

        map.put("a", "b", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            assertEquals(map.containsKey("a"), System.currentTimeMillis() < expires);

            Thread.sleep(5);
        }
    }

    /**
     * Make sure entry is removed without a direct check on the entry.
     * Tests the scheduled janitor task.
     */
    @Test
    public void testEntryLifespan1() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedHashMap<String, String> map = new TimedHashMap<String, String>(plugin);

        map.put("a", "b", 20, TimeScale.TICKS);

        BukkitTest.pause(30);

        assertEquals(0, map.size());
    }

}