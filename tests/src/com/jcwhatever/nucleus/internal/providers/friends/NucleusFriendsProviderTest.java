package com.jcwhatever.nucleus.internal.providers.friends;

import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.providers.friends.IFriendsProviderTest;

public class NucleusFriendsProviderTest extends IFriendsProviderTest {

    @Override
    protected IFriendsProvider createProvider() {
        return new NucleusFriendsProvider();
    }
}