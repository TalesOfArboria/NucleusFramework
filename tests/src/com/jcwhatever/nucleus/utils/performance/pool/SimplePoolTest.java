package com.jcwhatever.nucleus.utils.performance.pool;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link SimplePool}.
 */
public class SimplePoolTest {

    static class PoolElement {

    }

    private SimplePool<PoolElement> getPool(int size) {
        return new SimplePool<PoolElement>(PoolElement.class, size,
                new IPoolElementFactory<PoolElement>() {
                    @Override
                    public PoolElement create() {
                        return new PoolElement();
                    }
                });
    }

    @Test
    public void testSize() throws Exception {

        SimplePool<PoolElement> pool = getPool(10);

        assertEquals(0, pool.size());

        PoolElement element = pool.retrieve();

        assertEquals(0, pool.size());

        pool.recycle(element);

        assertEquals(1, pool.size());
    }

    @Test
    public void testMaxSize() throws Exception {

        SimplePool<PoolElement> pool = getPool(10);

        pool.setMaxSize(2);

        PoolElement elm1 = pool.retrieve();

        PoolElement elm2 = pool.retrieve();

        PoolElement elm3 = pool.retrieve();

        pool.recycle(elm1);
        pool.recycle(elm2);
        pool.recycle(elm3);

        assertEquals(2, pool.size());
    }

    @Test
    public void testRecycleAll() throws Exception {

        SimplePool<PoolElement> pool = getPool(5);

        PoolElement[] array = {
                pool.retrieve(),
                pool.retrieve(),
                pool.retrieve(),
                pool.retrieve()
        };

        assertEquals(0, pool.size());

        pool.recycleAll(array, 0, 4);

        assertEquals(4, pool.size());
    }

    @Test
    public void testPoolExpand() throws Exception {

        SimplePool<PoolElement> pool = getPool(2);

        assertEquals(2, pool.pool().length);

        PoolElement elm1 = pool.retrieve();

        PoolElement elm2 = pool.retrieve();

        PoolElement elm3 = pool.retrieve();

        PoolElement elm4 = pool.retrieve();

        pool.recycle(elm1);
        pool.recycle(elm2);
        pool.recycle(elm3);
        pool.recycle(elm4);

        assertEquals(12, pool.pool().length);
    }
}