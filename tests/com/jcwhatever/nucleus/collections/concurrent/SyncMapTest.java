package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.collections.java.MapTest;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SyncMapTest {

    @Test
    public void test() {

        SyncMap<String, Integer> map = new SyncMap<String, Integer>() {

            Map<String, Integer> internalMap = new HashMap<>(10);

            @Override
            protected Map<String, Integer> map() {
                return internalMap;
            }
        };

        MapTest<Integer> test = new MapTest<>(map, 1, 2, 3);

        test.run();
    }
}