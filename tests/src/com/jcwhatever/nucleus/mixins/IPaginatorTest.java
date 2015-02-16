package com.jcwhatever.nucleus.mixins;

import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Ignore
public class IPaginatorTest {

    /**
     * Run the test from within another test, providing implementation
     * of an {@link IPaginator}.
     *
     * <p>The collection of the paginator needs to be initialized for
     * the test first using {@link #initforTest}.</p>
     *
     * @param zero  An instance ith its page index set to 0.
     * @param one   An instance with its page index set to 1.
     */
    public static void run(IPaginator<String> zero, IPaginator<String> one) throws Exception {

        IPaginatorTest test = new IPaginatorTest(zero, one);

        test.testGetItemsPerPage();

        test.testGetPage();

        test.testGetPageStartIndex();

        test.testGetTotalPages();

        test.testIterator();
    }

    /**
     * Initialize a paginator collection for the test.
     */
    public static <T extends Collection<String>> T initForTest(T paginator) {
        paginator.clear();
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

    private IPaginator<String> _paginZero;
    private IPaginator<String> _paginOne;

    public IPaginatorTest(IPaginator<String> zero, IPaginator<String> one) {
        _paginZero = zero;
        _paginOne = one;
    }

    @Test
    public void testGetPageStartIndex() throws Exception {

        Assert.assertEquals(PageStartIndex.ZERO, _paginZero.getPageStartIndex());

        Assert.assertEquals(PageStartIndex.ONE, _paginOne.getPageStartIndex());
    }

    @Test
    public void testGetTotalPages() throws Exception {

        Assert.assertEquals(5, _paginZero.getTotalPages());
        Assert.assertEquals(5, _paginOne.getTotalPages());


        _paginZero.setItemsPerPage(3);
        _paginOne.setItemsPerPage(3);

        Assert.assertEquals(4, _paginZero.getTotalPages());
        Assert.assertEquals(4, _paginOne.getTotalPages());

        _paginZero.setItemsPerPage(5);
        _paginOne.setItemsPerPage(5);

        Assert.assertEquals(2, _paginZero.getTotalPages());
        Assert.assertEquals(2, _paginOne.getTotalPages());

        _paginZero.setItemsPerPage(20);
        _paginOne.setItemsPerPage(20);

        Assert.assertEquals(1, _paginZero.getTotalPages());
        Assert.assertEquals(1, _paginOne.getTotalPages());
    }

    @Test
    public void testGetItemsPerPage() throws Exception {

        _paginZero.setItemsPerPage(2);
        _paginOne.setItemsPerPage(2);

        Assert.assertEquals(2, _paginZero.getItemsPerPage());
        Assert.assertEquals(2, _paginOne.getItemsPerPage());

        _paginZero.setItemsPerPage(5);
        _paginOne.setItemsPerPage(5);

        Assert.assertEquals(5, _paginZero.getItemsPerPage());
        Assert.assertEquals(5, _paginOne.getItemsPerPage());
    }

    @Test
    public void testGetPage() throws Exception {

        _paginZero.setItemsPerPage(2);
        _paginOne.setItemsPerPage(2);

        List<String> page = _paginZero.getPage(0);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("1", page.get(0));
        Assert.assertEquals("2", page.get(1));

        page = _paginZero.getPage(4);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("9", page.get(0));
        Assert.assertEquals("10", page.get(1));


        page = _paginOne.getPage(1);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("1", page.get(0));
        Assert.assertEquals("2", page.get(1));

        page = _paginOne.getPage(5);

        Assert.assertEquals(2, page.size());
        Assert.assertEquals("9", page.get(0));
        Assert.assertEquals("10", page.get(1));
    }

    @Test
    public void testIterator() throws Exception {

        _paginZero.setItemsPerPage(2);
        _paginOne.setItemsPerPage(2);

        Iterator<String> iterator = _paginZero.iterator(4);

        Assert.assertEquals("9", iterator.next());
        Assert.assertEquals("10", iterator.next());
        Assert.assertEquals(false, iterator.hasNext());


        iterator = _paginOne.iterator(5);

        Assert.assertEquals("9", iterator.next());
        Assert.assertEquals("10", iterator.next());
        Assert.assertEquals(false, iterator.hasNext());
    }
}