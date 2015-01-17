package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.collections.java.MapRunnable;

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

        MapRunnable<Integer> test = new MapRunnable<>(map, 1, 2, 3);

        test.run();
    }
}