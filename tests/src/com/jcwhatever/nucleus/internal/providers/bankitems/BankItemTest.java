package com.jcwhatever.nucleus.internal.providers.bankitems;

import static org.junit.Assert.assertEquals;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.providers.bankitems.IBankItem;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemTest;
import com.jcwhatever.nucleus.providers.bankitems.InsufficientItemsException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.MemoryDataNode;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.UUID;

/**
 * Tests {@link BankItem}.
 */
public class BankItemTest extends IBankItemTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");

    @Override
    protected IBankItem getBankItem(UUID id, ItemStack itemStack, int amount) {
        IDataNode dataNode = new MemoryDataNode(_plugin);
        return new BankItem(id, itemStack, amount, dataNode);
    }

    /**
     * Make sure {@link #deposit} works correctly.
     */
    @Test
    public void testDeposit() {

        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);
        IDataNode dataNode = new MemoryDataNode(_plugin);
        BankItem bankItem = new BankItem(id, stack, 130, dataNode);

        bankItem.deposit(20);

        assertEquals(150, bankItem.getAmount());
        assertEquals(150, dataNode.getInteger("amount"));
    }

    /**
     * Make sure {@link #withdraw} works correctly.
     */
    @Test
    public void testWithdraw() throws InsufficientItemsException {
        UUID id = UUID.randomUUID();
        ItemStack stack = new ItemStack(Material.WOOD);
        IDataNode dataNode = new MemoryDataNode(_plugin);
        BankItem bankItem = new BankItem(id, stack, 130, dataNode);

        bankItem.withdraw(20);

        assertEquals(110, bankItem.getAmount());
        assertEquals(110, dataNode.getInteger("amount"));

        try {
            bankItem.withdraw(200);
            throw new AssertionError("InsufficientItemsException expected.");
        }
        catch(InsufficientItemsException ignore) {}

        assertEquals(110, bankItem.getAmount());
        assertEquals(110, dataNode.getInteger("amount"));
    }
}