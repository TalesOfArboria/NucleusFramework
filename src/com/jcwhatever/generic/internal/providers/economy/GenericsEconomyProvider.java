/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.generic.internal.providers.economy;

import com.google.common.collect.MapMaker;
import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.providers.economy.IAccount;
import com.jcwhatever.generic.providers.economy.IBank;
import com.jcwhatever.generic.providers.economy.IEconomyProvider;
import com.jcwhatever.generic.storage.DataPath;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.storage.YamlDataStorage;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.utils.text.TextUtils;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * GenericsLib's simple economy provider
 */
public final class GenericsEconomyProvider implements IEconomyProvider {

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final IDataNode _globalAccountNode;

    private final Map<UUID, GenericsAccount> _accounts =
            new MapMaker().weakValues().concurrencyLevel(1).initialCapacity(100).makeMap();

    private final Map<String, IBank> _banks = new HashMap<>(25);

    private String _currencyNameSingular = "Dollar";
    private String _currencyNamePlural = "Dollars";
    private String _currencyFormat = "{0: amount} {1: currencyName}";
    private DecimalFormat _formatter;

    public GenericsEconomyProvider(Plugin plugin) {
        PreCon.notNull(plugin);

        _dataNode = new YamlDataStorage(plugin, new DataPath("economy.config"));
        _globalAccountNode = new YamlDataStorage(plugin, new DataPath("economy.global"));
        _plugin = plugin;

        loadSettings();
    }

    @Override
    public String formatAmount(double amount) {

        String formattedDecimal = _formatter.format(amount);
        return TextUtils.format(_currencyFormat,
                formattedDecimal, (amount > 1 ? _currencyNamePlural : _currencyNameSingular));
    }

    @Override
    public String getCurrencyName(CurrencyNoun noun) {
        PreCon.notNull(noun);

        switch (noun) {
            case SINGULAR:
                return _currencyNameSingular;

            case PLURAL:
                return _currencyNamePlural;

            default:
                throw new AssertionError();
        }
    }

    @Override
    public IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        GenericsAccount account = _accounts.get(playerId);
        if (account == null) {
            account = new GenericsAccount(playerId, null, _globalAccountNode.getNode(playerId.toString()));
            _accounts.put(playerId, account);
        }

        return account;
    }

    @Override
    public boolean hasBankSupport() {
        return true;
    }

    @Override
    public List<IBank> getBanks() {
        return new ArrayList<>(_banks.values());
    }

    @Nullable
    @Override
    public IBank getBank(String bankName) {
        return _banks.get(bankName);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName) {
        return createBank(bankName, null);
    }

    @Nullable
    @Override
    public IBank createBank(String bankName, @Nullable UUID playerId) {
        PreCon.notNullOrEmpty(bankName);

        if (_banks.containsKey(bankName)) {
            return null;
        }

        IDataNode node = new YamlDataStorage(GenericsLib.getPlugin(), new DataPath("economy.accounts." + bankName));

        GenericsBank bank = new GenericsBank(bankName, null, node);

        _banks.put(bankName, bank);

        return bank;
    }

    @Override
    public boolean deleteBank(String bankName) {
        PreCon.notNullOrEmpty(bankName);

        IBank bank = _banks.remove(bankName);
        if (bank == null)
            return false;

        File economyFolder = new File(_plugin.getDataFolder(), "economy");
        File accountsFolder = new File(economyFolder, "accounts");
        File accountFile = new File(accountsFolder, bank.getName() + ".yml");

        return accountFile.delete();
    }

    private void loadSettings() {
        _currencyNameSingular = _dataNode.getString("currency-singular", _currencyNameSingular);
        _currencyNamePlural = _dataNode.getString("currency-plural", _currencyNamePlural);
        _currencyFormat = _dataNode.getString("currency-format", _currencyFormat);
        String decimalFormat = _dataNode.getString("decimal-format", "###,###,###,###.00");

        assert decimalFormat != null;
        _formatter = new DecimalFormat(decimalFormat);
    }
}
