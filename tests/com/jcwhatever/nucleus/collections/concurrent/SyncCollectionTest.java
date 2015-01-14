package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.collections.java.CollectionTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SyncCollectionTest {


    @Test
    public void basicTest() {

        SyncCollection<String> collection = new SyncCollection<String>() {

            List<String> list = new ArrayList<>(10);

            @Override
            protected Collection<String> collection() {
                return list;
            }
        };


        CollectionTest<String>  test = new CollectionTest<>(collection, "a", "b", "c");
        test.run();
    }

}