package com.jcwhatever.nucleus.internal.providers.bankitems;

import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProviderTest;

/**
 * Tests {@link BankItemsProvider}.
 */
public class BankItemsProviderTest extends IBankItemsProviderTest {

    @Override
    protected IBankItemsProvider getProvider() {
        return new BankItemsProvider();
    }
}