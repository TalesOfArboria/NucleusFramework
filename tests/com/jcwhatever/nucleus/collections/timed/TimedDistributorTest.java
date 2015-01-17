package com.jcwhatever.nucleus.collections.timed;

import com.jcwhatever.nucleus.collections.java.CollectionTest;

import org.junit.Test;

public class TimedDistributorTest {

    @Test
    public void testCollectionInterface() {

        TimedDistributor<String> distributor = new TimedDistributor<>();

        CollectionTest<String> test = new CollectionTest<>(distributor, "a", "b", "c");
        test.run();

    }

}