package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

public class ArrayListPaginatorTest {

    private ArrayListPaginator<String> getPaginator(PageStartIndex startIndex, int itemsPerPage) {
        ArrayListPaginator<String> paginator = new ArrayListPaginator<String>(startIndex, itemsPerPage);

        paginator.add("1");
        paginator.add("2");
        paginator.add("3");
        paginator.add("4");
        paginator.add("5");
        paginator.add("6");
        paginator.add("7");
        paginator.add("8");
        paginator.add("9");
        paginator.add("10");

        return paginator;
    }

    @Test
    public void testGetPageStartIndex() throws Exception {

        ArrayListPaginator<String> pagin = getPaginator(PageStartIndex.ZERO, 2);

        Assert.assertEquals(PageStartIndex.ZERO, pagin.getPageStartIndex());


        pagin = getPaginator(PageStartIndex.ONE, 2);

        Assert.assertEquals(PageStartIndex.ONE, pagin.getPageStartIndex());
    }

    @Test
    public void testGetTotalPages() throws Exception {

        ArrayListPaginator<String> pagin = getPaginator(PageStartIndex.ZERO, 2);

        Assert.assertEquals(5, pagin.getTotalPages());


        pagin.setItemsPerPage(3);

        Assert.assertEquals(4, pagin.getTotalPages());

        pagin.setItemsPerPage(5);

        Assert.assertEquals(2, pagin.getTotalPages());

        pagin.setItemsPerPage(20);

        Assert.assertEquals(1, pagin.getTotalPages());
    }

    @Test
    public void testGetItemsPerPage() throws Exception {

        ArrayListPaginator<String> pagin = getPaginator(PageStartIndex.ZERO, 2);

        Assert.assertEquals(2, pagin.getItemsPerPage());


        pagin.setItemsPerPage(5);

        Assert.assertEquals(5, pagin.getItemsPerPage());

    }

    @Test
    public void testGetPage() throws Exception {

        ArrayListPaginator<String> pagin = getPaginator(PageStartIndex.ZERO, 2);

        List<String> page = pagin.getPage(0);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("1", page.get(0));
        Assert.assertEquals("2", page.get(1));

        page = pagin.getPage(4);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("9", page.get(0));
        Assert.assertEquals("10", page.get(1));


        pagin = getPaginator(PageStartIndex.ONE, 2);

        page = pagin.getPage(1);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("1", page.get(0));
        Assert.assertEquals("2", page.get(1));

        page = pagin.getPage(5);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("9", page.get(0));
        Assert.assertEquals("10", page.get(1));
    }

    @Test
    public void testIterator() throws Exception {

        ArrayListPaginator<String> pagin = getPaginator(PageStartIndex.ZERO, 2);

        Iterator<String> iterator = pagin.iterator(4);

        Assert.assertEquals("9", iterator.next());
        Assert.assertEquals("10", iterator.next());
        Assert.assertEquals(false, iterator.hasNext());


        pagin = getPaginator(PageStartIndex.ONE, 2);

        iterator = pagin.iterator(5);

        Assert.assertEquals("9", iterator.next());
        Assert.assertEquals("10", iterator.next());
        Assert.assertEquals(false, iterator.hasNext());
    }
}