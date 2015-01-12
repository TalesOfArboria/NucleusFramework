package com.jcwhatever.nucleus.providers.economy;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

@Ignore
public class IBankTest {

    private IBank _bank;
    private UUID _bankOwnerId;
    private UUID _accountOwnerId;

    public IBankTest(IBank bank, UUID testOwnerId) {
        _bank = bank;
        _bankOwnerId = testOwnerId;
        _accountOwnerId = UUID.randomUUID();
    }

    public void run() throws Exception {
        testGetOwnerId();
        testGetBalance();
        testHasAccount();
        testGetAccount();
        testCreateAccount();
        testDeleteAccount();
    }

    @Test
    public void testGetOwnerId() throws Exception {

        UUID id = _bank.getOwnerId();

        Assert.assertEquals(_bankOwnerId, id);
    }

    @Test
    public void testGetBalance() throws Exception {

        double balance = _bank.getBalance();

        ICurrency currency = ICurrencyTest.createDummyCurrency(1.0D);

        // should return as economy providers currency
        Assert.assertEquals(balance, _bank.getBalance(currency), 0.0D);


        currency = ICurrencyTest.createDummyCurrency(2.0D);
        double convertedBalance = _bank.getBalance(currency);

        Assert.assertEquals(balance * 2.0D, convertedBalance, 0.0D);
    }

    @Test
    public void testHasAccount() throws Exception {

        boolean hasAccount = _bank.hasAccount(new UUID(0L, 0L));

        Assert.assertEquals(false, hasAccount);
    }

    @Test
    public void testGetAccount() throws Exception {

        // should not throw error
        _bank.getAccount(new UUID(0L, 0L));

    }

    @Test
    public void testCreateAccount() throws Exception {

        IAccount account = _bank.createAccount(_accountOwnerId);
        Assert.assertNotNull(account);

        Assert.assertEquals(_accountOwnerId, account.getPlayerId());

        new IAccountTest(account, _bank, _accountOwnerId).run();
    }

    @Test
    public void testDeleteAccount() throws Exception {

        IAccount account = _bank.getAccount(_accountOwnerId);
        Assert.assertNotNull(account);

        boolean result = _bank.deleteAccount(_accountOwnerId);
        Assert.assertEquals(true, result);

        boolean isDeleted = !_bank.hasAccount(_accountOwnerId);
        Assert.assertEquals(true, isDeleted);

        account = _bank.getAccount(_accountOwnerId);
        Assert.assertNull(account);
    }
}