package com.jcwhatever.nucleus.storage;

import com.jcwhatever.v1_8_R2.MockPlugin;

/*
 * 
 */
public class JsonDataNodeTest  extends IDataNodeTest {

    private static String TEST_JSON = "{}";

    public JsonDataNodeTest() {
        setNodeGenerator(new IDataNodeGenerator() {
            @Override
            public IDataNode generateRoot() {
                MockPlugin plugin = new MockPlugin("dummy").enable();

                return new JsonDataNode(plugin, TEST_JSON);
            }
        });
    }
}