package com.jcwhatever.nucleus.storage;

import com.jcwhatever.nucleus.storage.serialize.DataFieldSerializerTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MemoryDataNodeTest.class,
        YamlDataNodeTest.class,
        JsonDataNodeTest.class,
        DataFieldSerializerTest.class
})
public class _StorageTestSuite {
}
