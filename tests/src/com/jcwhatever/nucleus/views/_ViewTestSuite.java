package com.jcwhatever.nucleus.views;

import com.jcwhatever.nucleus.views.anvil._AnvilTestSuite;
import com.jcwhatever.nucleus.views.chest._ChestTestSuite;
import com.jcwhatever.nucleus.views.menu._MenuTestSuite;
import com.jcwhatever.nucleus.views.workbench._WorkbenchTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ViewSessionTest.class,
        ViewTest.class,

        _AnvilTestSuite.class,
        _ChestTestSuite.class,
        _MenuTestSuite.class,
        _WorkbenchTestSuite.class
})
public class _ViewTestSuite {
}
