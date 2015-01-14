package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

/*
 * 
 */
@Ignore
public class CollectionTest<E> implements Runnable {

    Collection<E> _collection;
    E _value1;
    E _value2;
    E _value3;

    public CollectionTest(Collection<E> collection, E value1, E value2, E value3) {
        _collection = collection;
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

        assertEquals(true, _collection.isEmpty());
        assertEquals(0, _collection.size());
        assertNotNull(_collection.toString());

        assertEquals(true, _collection.add(_value1));
        assertEquals(false, _collection.isEmpty());
        assertEquals(1, _collection.size());
        assertEquals(true, _collection.contains(_value1));

        assertEquals(true, _collection.remove(_value1));
        assertEquals(true, _collection.isEmpty());
        assertEquals(0, _collection.size());
        assertEquals(false, _collection.contains(_value1));


        assertEquals(true, _collection.add(_value1));
        assertEquals(true, _collection.add(_value2));
        assertEquals(2, _collection.size());
        assertEquals(false, _collection.isEmpty());
        assertEquals(true, _collection.contains(_value1));
        assertEquals(true, _collection.contains(_value2));

        Iterator<E> iterator = _collection.iterator();

        assertEquals(true, iterator.hasNext());
        assertNotNull(iterator.next());

        int size = 0;

        try {
            iterator.remove();
            assertEquals(1,_collection.size());
        }
        catch (UnsupportedOperationException ignore) { size++; }

        assertEquals(true, iterator.hasNext());
        assertNotNull(iterator.next());

        try {
            iterator.remove();
            assertEquals(0,_collection.size());
        }
        catch (UnsupportedOperationException ignore) { size ++; }

        assertEquals(false, iterator.hasNext());


        assertEquals(size, _collection.size());
        assertEquals(false, _collection.contains(_value1));
        assertEquals(false, _collection.contains(_value2));

        if (size == 0) {
            _collection.add(_value1);
            _collection.add(_value2);
            assertEquals(2, _collection.size());
        }

        _collection.clear();
        assertEquals(true, _collection.isEmpty());
        assertEquals(0, _collection.size());
    }
}
