package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.SetRunnable;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.junit.Test;

public class TimedHashSetTest {

    @Test
    public void testSetInterface() {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashSet<String> set = new TimedHashSet<String>(plugin);

        SetRunnable<String> setTest = new SetRunnable<String>(set, "a", "b", "c");

        setTest.run();
    }


    /**
     * Make sure element is removed.
     * Tests the scheduled janitor task.
     */
    @Test
    public void testElementLifespan() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashSet<String> set = new TimedHashSet<String>(plugin);

        set.add("a", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            assertEquals(set.contains("a"), System.currentTimeMillis() < expires);

            Thread.sleep(5);
        }
    }

    /**
     * Make sure element is removed without a direct check.
     */
    @Test
    public void testElementLifespan1() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashSet<String> set = new TimedHashSet<String>(plugin);

        set.add("a", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 500) {

            NucleusInit.heartBeat();

            Thread.sleep(5);
        }

        assertEquals(0, set.size());
    }


}