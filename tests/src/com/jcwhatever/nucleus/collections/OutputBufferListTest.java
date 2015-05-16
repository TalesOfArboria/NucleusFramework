package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.collections.java.ListRunnable;

import org.junit.Test;

/**
 * Tests {@link OutputBufferList}.
 */
public class OutputBufferListTest {

    @Test
    public void test() throws Exception {

        OutputBufferList<String> testList = new OutputBufferList<>();

        new ListRunnable<>(testList, "test1", "test2", "test3").run();
    }
}