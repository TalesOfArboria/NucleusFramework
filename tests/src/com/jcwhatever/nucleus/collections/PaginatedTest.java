package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;
import com.jcwhatever.nucleus.mixins.IPaginatorTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PaginatedTest {

    @Test
    public void test() throws Exception {

        List<String> list = IPaginatorTest.initForTest(new ArrayList<String>(10));

        Paginated<String> zero =
                new Paginated<>(PageStartIndex.ZERO, 2, list);

        Paginated<String> one =
                new Paginated<>(PageStartIndex.ONE, 2, list);

        IPaginatorTest.run(zero, one);
    }

}