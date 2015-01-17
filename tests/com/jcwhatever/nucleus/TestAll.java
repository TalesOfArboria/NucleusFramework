package com.jcwhatever.nucleus;

import com.jcwhatever.nucleus.collections._CollectionsTestSuite;
import com.jcwhatever.nucleus.commands._CommandsTestSuite;
import com.jcwhatever.nucleus.events.manager._ManagerTestSuite;
import com.jcwhatever.nucleus.internal.providers.economy._InternalEconomyTestSuite;
import com.jcwhatever.nucleus.storage._StorageTestSuite;
import com.jcwhatever.nucleus.utils._UtilsTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        _CollectionsTestSuite.class,
        _CommandsTestSuite.class,
        _ManagerTestSuite.class,
        _InternalEconomyTestSuite.class,
        _StorageTestSuite.class,
        _UtilsTestSuite.class
})
public class TestAll {
}
