package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.internal.providers.bankitems._InternalBankItemsTestSuite;
import com.jcwhatever.nucleus.internal.providers.economy._InternalEconomyTestSuite;
import com.jcwhatever.nucleus.internal.providers.friends._InternalFriendsTestSuite;
import com.jcwhatever.nucleus.internal.managed.reflection._ReflectionTestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        _ReflectionTestSuite.class,
        _InternalBankItemsTestSuite.class,
        _InternalEconomyTestSuite.class,
        _InternalFriendsTestSuite.class
})
public class _InternalTestSuite {
}
