package com.jcwhatever.nucleus.internal.providers.economy;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusTest;
import com.jcwhatever.nucleus.providers.economy.IEconomyProviderTest;

import org.junit.Test;

import java.util.UUID;

public class NucleusEconomyProviderTest {

    @Test
    public void test() throws Exception {

        NucleusTest.init();

        NucleusEconomyProvider provider = new NucleusEconomyProvider(Nucleus.getPlugin());

        IEconomyProviderTest.run(provider, UUID.randomUUID(), UUID.randomUUID());
    }
}