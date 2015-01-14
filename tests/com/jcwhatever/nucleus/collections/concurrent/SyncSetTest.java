package com.jcwhatever.nucleus.collections.concurrent;

import com.jcwhatever.nucleus.collections.java.SetTest;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/*
 * 
 */
public class SyncSetTest {


    @Test
    public void basicTest() {

        SyncSet<String> set = new SyncSet<String>() {

            Set<String> hashSet = new HashSet<>(10);

            @Override
            protected Set<String> set() {
                return hashSet;
            }
        };


        SetTest<String> test = new SetTest<>(set, "a", "b", "c");
        test.run();
    }

}
