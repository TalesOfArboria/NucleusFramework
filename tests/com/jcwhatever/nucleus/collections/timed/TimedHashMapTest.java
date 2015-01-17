package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.MapTest;

import org.junit.Test;

public class TimedHashMapTest {


    @Test
    public void testMapInterface() {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashMap<String, String> map = new TimedHashMap<String, String>(plugin);

        MapTest<String> mapTest = new MapTest<String>(map, "a", "b", "c");

        mapTest.run();
    }

    /**
     * Make sure entry is removed.
     */
    @Test
    public void testEntryLifespan() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
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

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashMap<String, String> map = new TimedHashMap<String, String>(plugin);

        map.put("a", "b", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 500) {

            NucleusInit.heartBeat();

            Thread.sleep(5);
        }

        assertEquals(0, map.size());
    }

}