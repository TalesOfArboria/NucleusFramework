package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class ElementCounterTest {

    @Test
    public void testAddAll() throws Exception {

        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.addAll(ArrayUtils.asList("1", "2", "3"));

        Assert.assertEquals(1, counter.count("1"));
        Assert.assertEquals(1, counter.count("2"));
        Assert.assertEquals(1, counter.count("3"));
        Assert.assertEquals(0, counter.count("4"));

        Assert.assertEquals(3, counter.size());
    }

    @Test
    public void testAdd() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");

        Assert.assertEquals(2, counter.size());
    }

    @Test
    public void testSubtractAll() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtractAll(ArrayUtils.asList("1", "2"));

        Assert.assertEquals(1, counter.size());

        Assert.assertEquals(true, counter.contains("2"));
    }

    @Test
    public void testSubtractAll1() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.BOTTOM_OUT);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtractAll(ArrayUtils.asList("1", "2"));

        Assert.assertEquals(2, counter.size());

        Assert.assertEquals(true, counter.contains("2"));

        Assert.assertEquals(true, counter.contains("1"));

        Assert.assertEquals(0, counter.count("1"));
    }

    @Test
    public void testSubtractAll2() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.KEEP_COUNTING);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtractAll(ArrayUtils.asList("1", "2"));

        Assert.assertEquals(2, counter.size());

        Assert.assertEquals(true, counter.contains("2"));

        Assert.assertEquals(true, counter.contains("1"));

        Assert.assertEquals(0, counter.count("1"));
    }

    @Test
    public void testSubtract() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtract("2");

        Assert.assertEquals(2, counter.size());

        Assert.assertEquals(true, counter.contains("1"));
        Assert.assertEquals(true, counter.contains("2"));

        Assert.assertEquals(1, counter.count("2"));
    }

    @Test
    public void testSubtract1() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.BOTTOM_OUT);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtract("1");

        Assert.assertEquals(2, counter.size());

        Assert.assertEquals(true, counter.contains("1"));
        Assert.assertEquals(true, counter.contains("2"));

        Assert.assertEquals(0, counter.count("1"));

        counter.subtract("1");

        Assert.assertEquals(0, counter.count("1"));
    }

    @Test
    public void testSubtract2() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.KEEP_COUNTING);

        counter.add("1");
        counter.add("2");
        counter.add("2");

        counter.subtract("1");

        Assert.assertEquals(2, counter.size());

        Assert.assertEquals(true, counter.contains("1"));
        Assert.assertEquals(true, counter.contains("2"));

        Assert.assertEquals(0, counter.count("1"));

        counter.subtract("1");

        Assert.assertEquals(-1, counter.count("1"));
    }

    @Test
    public void testGetCount() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");
        counter.add("3");
        counter.add("3");
        counter.add("3");

        Assert.assertEquals(1, counter.count("1"));
        Assert.assertEquals(2, counter.count("2"));
        Assert.assertEquals(3, counter.count("3"));
    }

    @Test
    public void testSize() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");
        counter.add("3");
        counter.add("3");
        counter.add("3");

        Assert.assertEquals(3, counter.size());
    }

    @Test
    public void testContains() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");
        counter.add("3");
        counter.add("3");
        counter.add("3");

        Assert.assertEquals(true, counter.contains("2"));
        Assert.assertEquals(false, counter.contains("4"));
    }

    @Test
    public void testGetElements() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");
        counter.add("3");
        counter.add("3");
        counter.add("3");

        Set<String> result = counter.getElements();

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(true, result.containsAll(ArrayUtils.asList("1", "2", "3")));
    }

    @Test
    public void testReset() throws Exception {
        ElementCounter<String> counter = new ElementCounter<String>(RemovalPolicy.REMOVE);

        counter.add("1");
        counter.add("2");
        counter.add("2");
        counter.add("3");
        counter.add("3");
        counter.add("3");

        counter.reset();

        Assert.assertEquals(0, counter.size());
    }
}