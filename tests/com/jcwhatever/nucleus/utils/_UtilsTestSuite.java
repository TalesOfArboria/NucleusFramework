package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.utils.file._FileTestSuite;
import com.jcwhatever.nucleus.utils.inventory._InventoryTestSuite;
import com.jcwhatever.nucleus.utils.items._ItemsTestSuite;
import com.jcwhatever.nucleus.utils.observer._ObserverTestSuite;
import com.jcwhatever.nucleus.utils.text._TextTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ArrayUtilsTest.class,
        DateUtilsTest.class,
        EnumUtilsTest.class,
        PreConTest.class,

        _FileTestSuite.class,
        _InventoryTestSuite.class,
        _ItemsTestSuite.class,
        _ObserverTestSuite.class,
        _TextTestSuite.class
})
public class _UtilsTestSuite {
}
