package com.jcwhatever.nucleus.providers.economy;

import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void testGetPlayerId() throws Exception {

        UUID playerId = _account.getPlayerId();

        assertEquals(_ownerId, playerId);
    }

    @Test
    public void testGetBank() throws Exception {

        IBank bank = _account.getBank();

        assertEquals(_bank, bank);
    }
}