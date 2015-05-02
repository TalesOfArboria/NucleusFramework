package com.jcwhatever.nucleus.providers.bankitems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

/**
 * Abstract test for {@link IBankItemsBank} implementations.
 */
public abstract class IBankItemsBankTest {

    protected abstract IBankItemsBank getBank(String bankName, UUID ownerId);

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure {@link #getName} and {@link #getSearchName} returns the correct value.
     */
    @Test
    public void testGetName() {

        IBankItemsBank bank = getBank("Dummy", null);

        assertEquals("Dummy", bank.getName());
        assertEquals("dummy", bank.getSearchName());
    }

    /**
     * Make sure {@link getOwnerId} returns the correct value.
     */
    @Test
    public void testGetOwnerId() {

        IBankItemsBank bank = getBank("dummy", null);

        assertEquals(null, bank.getOwnerId());

        UUID id = UUID.randomUUID();
        bank = getBank("dummy", id);
        assertEquals(id, bank.getOwnerId());
    }

    /**
     * Make sure basic account operations are working correctly.
     */
    @Test
    public void testAccounts() {

        Player player1 = BukkitTester.login("player1");
        Player player2 = BukkitTester.login("player2");

        IBankItemsBank bank = getBank("dummy", null);

        assertEquals(false, bank.hasAccount(player1.getUniqueId()));
        assertEquals(false, bank.hasAccount(player2.getUniqueId()));

        assertEquals(null, bank.getAccount(player1.getUniqueId()));
        assertEquals(null, bank.getAccount(player2.getUniqueId()));

        // create accounts
        IBankItemsAccount account1 = bank.createAccount(player1.getUniqueId());
        IBankItemsAccount account2 = bank.createAccount(player2.getUniqueId());

        assertTrue(account1 != null);
        assertEquals(player1.getUniqueId(), account1.getOwnerId());

        assertTrue(account2 != null);
        assertEquals(player2.getUniqueId(), account2.getOwnerId());


        assertEquals(true, bank.hasAccount(player1.getUniqueId()));
        assertEquals(true, bank.hasAccount(player2.getUniqueId()));

        assertEquals(account1, bank.getAccount(player1.getUniqueId()));
        assertEquals(account2, bank.getAccount(player2.getUniqueId()));

        // delete account
        assertEquals(true, bank.deleteAccount(player1.getUniqueId()));
        assertEquals(false, bank.deleteAccount(player1.getUniqueId()));

        assertEquals(false, bank.hasAccount(player1.getUniqueId()));
        assertEquals(true, bank.hasAccount(player2.getUniqueId()));

        assertEquals(null, bank.getAccount(player1.getUniqueId()));
        assertEquals(account2, bank.getAccount(player2.getUniqueId()));
    }

}