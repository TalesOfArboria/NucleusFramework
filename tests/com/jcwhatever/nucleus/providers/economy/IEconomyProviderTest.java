package com.jcwhatever.nucleus.providers.economy;

import com.jcwhatever.nucleus.NucleusTest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

@Ignore
public class IEconomyProviderTest {

    IEconomyProvider _provider;
    UUID _playerId1;
    UUID _playerId2;

    public IEconomyProviderTest(IEconomyProvider provider, UUID testPlayer1Id, UUID testPlayer2Id) {
        _provider = provider;
        _playerId1 = testPlayer1Id;
        _playerId2 = testPlayer2Id;
    }

    public static void run(IEconomyProvider provider, UUID testPlayer1Id, UUID testPlayer2Id) throws Exception {

        NucleusTest.init();

        IEconomyProviderTest test = new IEconomyProviderTest(provider, testPlayer1Id, testPlayer2Id);

        test.testGetCurrency();
        test.testGetAccount();
        test.testCreateTransaction();

        if (provider instanceof IBankEconomyProvider) {

            new IBankEconomyProviderTest((IBankEconomyProvider)provider, testPlayer1Id).run();
        }
    }


    @Test
    public void testGetCurrency() throws Exception {

        ICurrency currency = _provider.getCurrency();
        Assert.assertNotNull(currency);

        // the provider currency factor must be 1.0
        Assert.assertEquals(1.0D, currency.getConversionFactor(), 0.0D);

        new ICurrencyTest(currency).run();
    }

    @Test
    public void testGetAccount() throws Exception {

        IAccount account = _provider.getAccount(_playerId1);

        Assert.assertNotNull(account);

        new IAccountTest(account, null, _playerId1).run();
    }

    @Test
    public void testCreateTransaction() throws Exception {

        IEconomyTransaction transaction = _provider.createTransaction();

        Assert.assertNotNull(transaction);
    }
}