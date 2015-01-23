package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.bukkit.v1_8_R1.MockPlugin;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.collections.java.MultimapRunnable;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.junit.Test;

public class TimedSetMultimapTest {


    @Test
    public void basicTest() {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedSetMultimap<String, String> map = new TimedSetMultimap<String, String>(plugin);

        MultimapRunnable<String, String> test = new MultimapRunnable<>(map, "ka", "kb", "kc", "va", "vb", "vc");
        test.run();
    }

    @Test
    public void testEntryLifespan() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedSetMultimap<String, String> map = new TimedSetMultimap<String, String>(plugin);

        map.put("a", "b", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            assertEquals(map.containsKey("a"), System.currentTimeMillis() < expires);

            Thread.sleep(5);
        }
    }

    @Test
    public void testEntryLifespan1() throws Exception {

        NucleusTest.init();

        MockPlugin plugin = new MockPlugin("dummy");
        plugin.onEnable();

        TimedSetMultimap<String, String> map = new TimedSetMultimap<String, String>(plugin);

        map.put("a", "b", 20, TimeScale.TICKS);

        BukkitTester.pause(22);

        assertEquals(0, map.size());
    }

}