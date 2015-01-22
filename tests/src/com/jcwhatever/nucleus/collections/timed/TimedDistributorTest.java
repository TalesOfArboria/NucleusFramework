package com.jcwhatever.nucleus.collections.timed;

import com.jcwhatever.nucleus.collections.java.CollectionRunnable;

import org.junit.Test;

public class TimedDistributorTest {

    @Test
    public void testCollectionInterface() {

        TimedDistributor<String> distributor = new TimedDistributor<>();

        CollectionRunnable<String> test = new CollectionRunnable<>(distributor, "a", "b", "c");
        test.run();

    }

}