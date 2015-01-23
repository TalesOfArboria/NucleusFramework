package com.jcwhatever.nucleus.providers.bankitems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.NucleusTest;

import org.bukkit.entity.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

/**
 * Abstract test for {@link IBankItemsProvider} implementations.
 */
public abstract class IBankItemsProviderTest {

    private Player _player = BukkitTester.login("dummy");

    protected abstract IBankItemsProvider getProvider();

    /**
     * Make sure Nucleus and Bukkit are initialized.
     */
    @BeforeClass
    public static void init() {
        NucleusTest.init();
    }

    /**
     * Make sure {@code #getAccount} works correctly.
     */
    @Test
    public void testGetAccount() {

        IBankItemsProvider provider = getProvider();

        IBankItemsAccount account = provider.getAccount(_player.getUniqueId());

        // global account should be auto-created if not exists
        assertTrue(account != null);
    }

    /**
     * Make sure {@code #createBank} works correctly.
     */
    @Test
    public void testCreateBank() {

        IBankItemsProvider provider = getProvider();

        IBankItemsBank bank = provider.createBank("bank");

        assertTrue(bank != null);
        assertEquals("bank", bank.getName());
        assertEquals(null, bank.getOwnerId());


        UUID ownerId = UUID.randomUUID();
        bank = provider.createBank("bank2", ownerId);

        assertTrue(bank != null);
        assertEquals("bank2", bank.getName());
        assertEquals(ownerId, bank.getOwnerId());
    }

    /**
     * Make sure {@code #hasBank} works correctly.
     */
    @Test
    public void testHasBank() {

        IBankItemsProvider provider = getProvider();

        assertEquals(false, provider.hasBank("bank"));

        provider.createBank("bank");

        assertEquals(true, provider.hasBank("bank"));
    }

    /**
     * Make sure {@code #getBank} works correctly.
     */
    @Test
    public void testGetBank() {

        IBankItemsProvider provider = getProvider();

        IBankItemsBank bank = provider.getBank("bank");

        assertTrue(bank == null);

        provider.createBank("bank");

        bank = provider.getBank("bank");

        assertTrue(bank != null);
    }

    @Test
    public void testDeleteBank() throws Exception {

        IBankItemsProvider provider = getProvider();

        assertEquals(false, provider.deleteBank("bank"));

        provider.createBank("bank");

        IBankItemsBank bank = provider.getBank("bank");

        assertTrue(bank != null);

        assertEquals(true, provider.deleteBank("bank"));

        bank = provider.getBank("bank");

        assertEquals(null, bank);
    }

}