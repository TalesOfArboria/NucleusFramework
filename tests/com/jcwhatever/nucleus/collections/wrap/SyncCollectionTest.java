package com.jcwhatever.nucleus.collections.wrap;

import com.jcwhatever.nucleus.collections.java.CollectionRunnable;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncCollectionTest {


    @Test
    public void basicTest() {

        CollectionWrapper<String> collection = new CollectionWrapper<String>() {

            List<String> list = new ArrayList<>(10);

            @Override
            protected Collection<String> collection() {
                return list;
            }
        };


        CollectionRunnable<String> test = new CollectionRunnable<>(collection, "a", "b", "c");
        test.run();
    }

}