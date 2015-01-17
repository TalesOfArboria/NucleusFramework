package com.jcwhatever.nucleus.collections.timed;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.dummy.DummyPlugin;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.collections.java.ListRunnable;

import org.junit.Test;

public class TimedArrayListTest {

    @Test
    public void testListInterface() {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedArrayList<String> list = new TimedArrayList<String>(plugin);

        ListRunnable<String> listTest = new ListRunnable<>(list, "a", "b", "c");
        listTest.run();
    }

    @Test
    public void testElementLifespan() throws Exception {

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
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

        NucleusInit.init();

        DummyPlugin plugin = new DummyPlugin("dummy");
        plugin.onEnable();

        TimedArrayList<String> list = new TimedArrayList<String>(plugin);

        list.add("a", 1000, TimeScale.MILLISECONDS);

        long expires = System.currentTimeMillis() + 1000;

        while (System.currentTimeMillis() < expires + 100) {

            NucleusInit.heartBeat();

            Thread.sleep(5);
        }

        assertEquals(0, list.size());
    }
}