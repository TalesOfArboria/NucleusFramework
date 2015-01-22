package com.jcwhatever.nucleus.providers.economy;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

@Ignore
public class IBankEconomyProviderTest {

    private IBankEconomyProvider _provider;
    private UUID _ownerId;

    public IBankEconomyProviderTest(IBankEconomyProvider provider, UUID testOwnerId) {
        _provider = provider;
        _ownerId = testOwnerId;
    }

    public void run() throws Exception {
        testGetBanks();
        testCreateBank();
        testCreateBank1();
        testGetBank();
        testDeleteBank();
    }

    @Test
    public void testGetBanks() throws Exception {

        List<IBank> banks = _provider.getBanks();

        Assert.assertNotNull(banks);
    }

    @Test
    public void testCreateBank() throws Exception {

        // get number of banks before creating bank
        List<IBank> banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int beforeSize = banks.size();

        // create bank
        IBank bank = _provider.createBank("dummy");
        Assert.assertNotNull(bank);

        new IBankTest(bank, null).run();

        banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int afterSize = banks.size();

        Assert.assertEquals(afterSize, beforeSize + 1);
    }

    @Test
    public void testCreateBank1() throws Exception {

        // get number of banks before creating bank
        List<IBank> banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int beforeSize = banks.size();

        // create bank
        IBank bank = _provider.createBank("dummy2", _ownerId);
        Assert.assertNotNull(bank);

        new IBankTest(bank, _ownerId).run();

        banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int afterSize = banks.size();

        Assert.assertEquals(afterSize, beforeSize + 1);
    }

    @Test
    public void testGetBank() throws Exception {

        IBank bank = _provider.getBank("dummy");
        Assert.assertNotNull(bank);
    }

    @Test
    public void testDeleteBank() throws Exception {

        // get number of banks before creating bank
        List<IBank> banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int beforeSize = banks.size();

        boolean result = _provider.deleteBank("dummy");
        Assert.assertEquals(true, result);

        banks = _provider.getBanks();
        Assert.assertNotNull(banks);

        int afterSize = banks.size();

        Assert.assertEquals(afterSize, beforeSize - 1);
    }
}