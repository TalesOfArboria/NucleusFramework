package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.SetTest;

import org.junit.Test;

public class TimedHashSetTest {

    @Test
    public void testSetInterface() {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedHashSet<String> set = new TimedHashSet<String>(plugin);

        SetTest<String> setTest = new SetTest<String>(set, "a", "b", "c");

        setTest.run();
    }


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

}