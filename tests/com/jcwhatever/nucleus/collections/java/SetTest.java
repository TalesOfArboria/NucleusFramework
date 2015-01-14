package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.util.Set;

/*
 * 
 */
public class SetTest<E> implements Runnable {

    Set<E> _set;
    E _value1;
    E _value2;
    E _value3;

    public SetTest(Set<E> collection, E value1, E value2, E value3) {
        _set = collection;
        _value1 = value1;
        _value2 = value2;
        _value3 = value3;
    }

    @Override
    public void run() {
        basicTest();
    }

    @Test
    public void basicTest() {

        CollectionTest<E> collectionTest = new CollectionTest<E>(_set, _value1, _value2, _value3);
        collectionTest.run();

        _set.clear();
        assertEquals(true, _set.isEmpty());
        assertEquals(0, _set.size());
        assertNotNull(_set.toString());

        _set.add(_value1);
        _set.add(_value1);
        _set.add(_value1);

        assertEquals(1, _set.size());


        _set.add(_value2);
        assertEquals(2, _set.size());


        assertEquals(true, _set.contains(_value1));
        assertEquals(true, _set.contains(_value2));


        _set.remove(_value2);

        assertEquals(1, _set.size());
        assertEquals(false, _set.contains(_value2));

        _set.clear();
        assertEquals(true, _set.isEmpty());
        assertEquals(0, _set.size());

    }
}
