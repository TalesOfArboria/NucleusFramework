/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.internal.providers.bankitems;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.timed.TimedHashMap;
import com.jcwhatever.nucleus.internal.providers.InternalProviderInfo;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccount;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsBank;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.IDataNode.AutoSaveMode;
import com.jcwhatever.nucleus.storage.MemoryDataNode;
import com.jcwhatever.nucleus.storage.YamlDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nucleus implementation of {@link IBankItemsProvider}.
 */
public class BankItemsProvider extends Provider implements IBankItemsProvider {

    public static final String NAME = "NucleusBankItems";
    private static BankItemsProvider _instance;

    private final TimedHashMap<UUID, BankItemsAccount> _globalAccounts =
            new TimedHashMap<>(Nucleus.getPlugin(), 25, 10, TimeScale.MINUTES);

    private final Map<String, BankItemsBank> _banks = new HashMap<>(25);
    private IDataNode _bankNode;

    /**
     * Constructor.
     */
    public BankItemsProvider() {

        _instance = this;

        setInfo(new InternalProviderInfo(this.getClass(),
                NAME, "Default bank items provider."));

        _bankNode = Nucleus.getPlugin().isTesting()
                ? new MemoryDataNode(Nucleus.getPlugin())
                : new YamlDataNode(Nucleus.getPlugin(), getDataPath("banks"));
        _bankNode.load();

        load();
    }

    @Override
    public IBankItemsAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        BankItemsAccount account = _globalAccounts.get(playerId);
        if (account == null) {

            account = loadAccountFromFile(playerId);
            if (account == null)
                throw new RuntimeException("Failed to load players Bank Item Account data.");

            _globalAccounts.put(playerId, account);
        }

        return account;
    }

    @Override
    public boolean hasBank(String bankName) {
        PreCon.notNull(bankName);

        return _banks.containsKey(bankName.toLowerCase());
    }

    @Override
    @Nullable
    public IBankItemsBank getBank(String bankName) {
        PreCon.notNull(bankName);

        return _banks.get(bankName.toLowerCase());
    }

    @Override
    public IBankItemsBank createBank(String bankName) {
        return createBank(bankName, null);
    }

    @Override
    public IBankItemsBank createBank(String bankName, @Nullable UUID ownerId) {
        PreCon.notNullOrEmpty(bankName);

        BankItemsBank bank = _banks.get(bankName.toLowerCase());
        if (bank == null) {
            IDataNode node = _bankNode.getNode(bankName);
            bank = new BankItemsBank(bankName, ownerId, node);
            _banks.put(bank.getSearchName(), bank);
        }

        return bank;
    }

    @Override
    public boolean deleteBank(String bankName) {
        PreCon.notNull(bankName);

        BankItemsBank bank = _banks.remove(bankName.toLowerCase());
        if (bank == null)
            return false;

        bank.dispose();

        return true;
    }

    private void load() {

        for (IDataNode bankNode : _bankNode) {

            UUID ownerId = bankNode.getUUID("owner-id");

            BankItemsBank bank = new BankItemsBank(bankNode.getName(), ownerId, bankNode);

            _banks.put(bank.getSearchName(), bank);
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        _instance = null;
    }

    @Nullable
    static BankItemsAccount loadAccountFromFile (UUID playerId) {

        DataPath path = _instance.getDataPath(
                (Bukkit.getOnlineMode() ? "global." : ".global-offline.")
                        + playerId);

        IDataNode dataNode = DataStorage.get(Nucleus.getPlugin(), path);
        if (!dataNode.load())
            return null;

        dataNode.setAutoSaveMode(AutoSaveMode.ENABLED);

        return new BankItemsAccount(playerId, null, dataNode);
    }
}
