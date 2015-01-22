package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;
import com.jcwhatever.nucleus.mixins.IPaginatorTest;

import org.junit.Test;

public class ArrayListPaginatorTest {

    @Test
    public void test() throws Exception {

        ArrayListPaginator<String> zero =
                IPaginatorTest.initForTest(new ArrayListPaginator<String>(PageStartIndex.ZERO, 2));

        ArrayListPaginator<String> one =
                IPaginatorTest.initForTest(new ArrayListPaginator<String>(PageStartIndex.ONE, 2));

        IPaginatorTest.run(zero, one);
    }

}