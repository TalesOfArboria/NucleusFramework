package com.jcwhatever.nucleus.storage;

import com.jcwhatever.bukkit.MockPlugin;

public class MemoryDataNodeTest extends IDataNodeTest {

    public MemoryDataNodeTest() {
        setNodeGenerator(new IDataNodeGenerator() {
            @Override
            public IDataNode generateRoot() {
                MockPlugin plugin = new MockPlugin("dummy");
                plugin.onEnable();

                return new MemoryDataNode(new MockPlugin("dummy"));
            }
        });
    }

}