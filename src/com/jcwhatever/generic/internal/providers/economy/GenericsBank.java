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

import com.jcwhatever.generic.providers.economy.IAccount;
import com.jcwhatever.generic.providers.economy.IBank;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * GenericsLib bank
 */
public final class GenericsBank implements IBank {

    private final String _name;
    private final UUID _ownerId;
    private final IDataNode _dataNode;
    private final Map<UUID, IAccount> _accounts = new HashMap<>(5);

    double _balance;

    GenericsBank (String name, @Nullable UUID ownerId, IDataNode dataNode) {
        _name = name;
        _ownerId = ownerId;
        _dataNode = dataNode;
    }

    public IDataNode getDataNode() {
        return _dataNode;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Nullable
    @Override
    public UUID getOwnerId() {
        return _ownerId;
    }

    @Override
    public double getBalance() {
        return _balance;
    }

    @Override
    public boolean hasAccount(UUID playerId) {
        PreCon.notNull(playerId);

        return _accounts.containsKey(playerId);
    }

    @Nullable
    @Override
    public IAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        return _accounts.get(playerId);
    }

    @Override
    public List<IAccount> getAccounts() {
        return new ArrayList<>(_accounts.values());
    }

    @Nullable
    @Override
    public IAccount createAccount(UUID playerId) {
        PreCon.notNull(playerId);

        if (hasAccount(playerId))
            return null;

        GenericsAccount account = new GenericsAccount(playerId, this, _dataNode.getNode(playerId.toString()));

        _accounts.put(playerId, account);

        return account;
    }

    @Override
    public boolean deleteAccount(UUID playerId) {
        PreCon.notNull(playerId);

        IAccount account = _accounts.remove(playerId);
        if (account == null)
            return false;

        _balance -= account.getBalance();

        IDataNode node = _dataNode.getNode(playerId.toString());
        node.remove();
        node.saveAsync(null);

        return true;
    }

    private void loadAccounts() {
        Set<String> accountIds = _dataNode.getSubNodeNames();

        for (String rawAccountId : accountIds) {
            UUID playerId = Utils.getId(rawAccountId);
            if (playerId == null)
                continue;

            IDataNode dataNode = _dataNode.getNode(rawAccountId);

            GenericsAccount account = new GenericsAccount(playerId, this, dataNode);

            _balance += account.getBalance();

            _accounts.put(playerId, account);
        }
    }

    @Override
    public int hashCode() {
        return _name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GenericsBank) {
            GenericsBank bank = (GenericsBank)obj;

            return bank._name.equals(_name);
        }
        return false;
    }
}
