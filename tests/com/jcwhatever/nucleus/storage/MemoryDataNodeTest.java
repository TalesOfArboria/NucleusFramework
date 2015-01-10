package com.jcwhatever.nucleus.storage;

import com.jcwhatever.dummy.DummyPlugin;

public class MemoryDataNodeTest extends IDataNodeTest {

    public MemoryDataNodeTest() {
        _dataNode = new MemoryDataNode(new DummyPlugin("dummy"));
        initNode(_dataNode);
    }

}