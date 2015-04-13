package com.jcwhatever.nucleus.utils.performance.pool;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.nucleus.utils.performance.pool.SimpleCheckoutPool.CheckedOutElements;

import org.junit.Test;

/**
 * Tests {@link SimpleCheckoutPool}.
 */
public class SimpleCheckoutPoolTest {

    static class PoolElement {

    }

    private SimpleCheckoutPool<PoolElement> getPool(int size) {
        return new SimpleCheckoutPool<>(PoolElement.class, size,
                new IPoolElementFactory<PoolElement>() {
                    @Override
                    public PoolElement create() {
                        return new PoolElement();
                    }
                });
    }

    @Test
    public void testSize() throws Exception {

        SimpleCheckoutPool<PoolElement> pool = getPool(10);

        assertEquals(0, pool.size());

        pool.checkout();

        assertEquals(0, pool.size());

        pool.getCheckedOut().recycle();

        assertEquals(1, pool.size());
    }

    @Test
    public void testMaxSize() throws Exception {

        SimpleCheckoutPool<PoolElement> pool = getPool(10);

        pool.setMaxSize(2);

        pool.checkout();

        pool.checkout();

        pool.checkout();

        pool.getCheckedOut().recycle();

        assertEquals(2, pool.size());
    }

    @Test
    public void testClear() throws Exception {

        SimpleCheckoutPool<PoolElement> pool = getPool(10);

        pool.checkout();

        pool.checkout();

        pool.checkout();

        pool.getCheckedOut().recycle();

        assertEquals(3, pool.size());

        pool.clear();

        assertEquals(0, pool.size());
    }

    @Test
    public void testGetCheckedOut() throws Exception {


        SimpleCheckoutPool<PoolElement> pool = getPool(10);

        CheckedOutElements<PoolElement> checked = pool.getCheckedOut();

        pool.checkout();

        pool.checkout();

        pool.checkout();

        assertEquals(3, checked.size());

        assertEquals(0, pool.size());

        checked.recycle();

        assertEquals(0, checked.size());

        assertEquals(3, pool.size());
    }

    @Test
    public void testPoolExpand() throws Exception {

        SimpleCheckoutPool<PoolElement> pool = getPool(2);

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