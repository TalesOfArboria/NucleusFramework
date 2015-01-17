package com.jcwhatever.nucleus.utils.observer;

import com.jcwhatever.nucleus.utils.observer.event._EventTestSuite;
import com.jcwhatever.nucleus.utils.observer.result._ResultTestSuite;
import com.jcwhatever.nucleus.utils.observer.update._UpdateTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        _EventTestSuite.class,
        _ResultTestSuite.class,
        _UpdateTestSuite.class
})
public class _ObserverTestSuite {
}
