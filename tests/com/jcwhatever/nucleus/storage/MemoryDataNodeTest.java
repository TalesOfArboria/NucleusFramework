package com.jcwhatever.nucleus.storage;

import com.jcwhatever.dummy.DummyPlugin;

public class MemoryDataNodeTest extends IDataNodeTest {

    public MemoryDataNodeTest() {
        setNodeGenerator(new IDataNodeGenerator() {
            @Override
            public IDataNode generateRoot() {
                return new MemoryDataNode(new DummyPlugin("dummy"));
            }
        });
    }

}