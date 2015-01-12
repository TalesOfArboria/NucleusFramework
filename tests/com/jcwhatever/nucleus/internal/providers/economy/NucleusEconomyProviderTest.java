package com.jcwhatever.nucleus.internal.providers.economy;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusInit;
import com.jcwhatever.nucleus.providers.economy.IEconomyProviderTest;

import org.junit.Test;

import java.util.UUID;

public class NucleusEconomyProviderTest {

    @Test
    public void test() throws Exception {

        NucleusInit.init();

        NucleusEconomyProvider provider = new NucleusEconomyProvider(Nucleus.getPlugin());

        IEconomyProviderTest.run(provider, UUID.randomUUID(), UUID.randomUUID());
    }
}