package com.jcwhatever.nucleus.internal.providers.bankitems;

import com.jcwhatever.bukkit.v1_8_R2.BukkitTester;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccount;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccountTest;
import com.jcwhatever.nucleus.storage.MemoryDataNode;

import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * Tests {@link BankItemsAccount}.
 */
public class BankItemAccountTest extends IBankItemsAccountTest {

    private Plugin _plugin = BukkitTester.mockPlugin("dummy");

    @Override
    protected IBankItemsAccount getAccount(UUID ownerId) {
        return new BankItemsAccount(ownerId, null, new MemoryDataNode(_plugin));
    }
}