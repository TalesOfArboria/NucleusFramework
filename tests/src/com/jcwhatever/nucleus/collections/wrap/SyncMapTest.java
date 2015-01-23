package com.jcwhatever.nucleus.collections.wrap;

import com.jcwhatever.nucleus.collections.java.MapRunnable;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SyncMapTest {

    @Test
    public void test() {

        MapWrapper<String, Integer> map = new MapWrapper<String, Integer>() {

            Map<String, Integer> internalMap = new HashMap<>(10);

            @Override
            protected Map<String, Integer> map() {
                return internalMap;
            }
        };

        MapRunnable<String, Integer> test = new MapRunnable<>(map, "a", "b", "c", 1, 2, 3);
        test.run();
    }
}