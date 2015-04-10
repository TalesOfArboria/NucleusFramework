package com.jcwhatever.nucleus.utils.observer.result;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.observer.future.Result;

import org.junit.Test;

public class ResultTest {


    @Test
    public void basicTest() {

        Result<String> result = new Result<String>(0.0D, 0.0D, "test");

        assertEquals(false, result.isComplete());
        assertEquals(false, result.isCancelled());
        assertEquals(false, result.isSuccess());
        assertEquals("test", result.getMessage());


        Result<String> failed = new Result<String>(1.0D, 0.0D, "test");

        assertEquals(true, failed.isComplete());
        assertEquals(false, failed.isCancelled());
        assertEquals(false, failed.isSuccess());
        assertEquals("test", result.getMessage());

        Result<String> cancelled = new Result<String>(1.0D, -1.0D, "test");

        assertEquals(true, cancelled.isComplete());
        assertEquals(true, cancelled.isCancelled());
        assertEquals(false, cancelled.isSuccess());
        assertEquals("test", cancelled.getMessage());

        Result<String> success = new Result<String>(1.0D, 1.0D, "test");

        assertEquals(true, success.isComplete());
        assertEquals(false, success.isCancelled());
        assertEquals(true, success.isSuccess());
        assertEquals("test", success.getMessage());
    }

}