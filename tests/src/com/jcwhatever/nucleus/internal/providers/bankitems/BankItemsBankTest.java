package com.jcwhatever.nucleus.internal.providers.bankitems;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.bukkit.v1_8_R1.BukkitTester;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsBank;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsBankTest;
import com.jcwhatever.nucleus.storage.MemoryDataNode;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.UUID;

/**
 * Tests {@link BankItemsBank}
 */
public class BankItemsBankTest extends IBankItemsBankTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");

    @Override
    protected IBankItemsBank getBank(String name, UUID ownerId) {
        return new BankItemsBank(name, ownerId, new MemoryDataNode(_plugin).getNode("banks"));
    }

    /**
     * Make sure {@code IDisposable} interface works correctly.
     */
    @Test
    public void testDispose() {

        Player player1 = BukkitTester.login("player1");

        BankItemsBank bank = new BankItemsBank("Dummy", null, new MemoryDataNode(_plugin).getNode("banks"));

        assertEquals(false, bank.isDisposed());

        // dispose bank
        bank.dispose();

        assertEquals(true, bank.isDisposed());

        try {
            bank.createAccount(player1.getUniqueId());
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}

        try {
            bank.deleteAccount(player1.getUniqueId());
            throw new AssertionError("RuntimeException expected.");
        }
        catch (RuntimeException ignore) {}
    }

}