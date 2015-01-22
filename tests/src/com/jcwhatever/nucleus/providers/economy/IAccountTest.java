package com.jcwhatever.nucleus.providers.economy;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;

@Ignore
public class IAccountTest {

    private IAccount _account;
    private IBank _bank;
    private UUID _ownerId;

    public IAccountTest(IAccount account, IBank bank, UUID ownerId) {
        _account = account;
        _bank = bank;
        _ownerId = ownerId;
    }

    public void run() throws Exception {
        testGetPlayerId();
        testGetBank();
        testGetBalance();
        testDeposit();
        testWithdraw();
    }

    @Test
    public void testGetPlayerId() throws Exception {

        UUID playerId = _account.getPlayerId();

        Assert.assertEquals(_ownerId, playerId);
    }

    @Test
    public void testGetBank() throws Exception {

        IBank bank = _account.getBank();

        Assert.assertEquals(_bank, bank);
    }

    @Test
    public void testGetBalance() throws Exception {

        double balance = _account.getBalance();

        _account.deposit(10);

        Assert.assertEquals(balance + 10, _account.getBalance(), 0.0D);

    }

    @Test
    public void testDeposit() throws Exception {

        double balance = _account.getBalance();

        boolean result = _account.deposit(10);

        Assert.assertEquals(true, result);

        Assert.assertEquals(balance + 10, _account.getBalance(), 0.0D);
    }

    @Test
    public void testWithdraw() throws Exception {

        double balance = _account.getBalance();

        boolean result = _account.deposit(10);
        Assert.assertEquals(true, result);
        Assert.assertEquals(balance + 10, _account.getBalance(), 0.0D);

        result = _account.withdraw(5);
        Assert.assertEquals(true, result);
        Assert.assertEquals(balance + 5, _account.getBalance(), 0.0D);
    }

}