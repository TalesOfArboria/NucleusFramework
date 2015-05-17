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
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccount;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsBank;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.IDataNode.AutoSaveMode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.TimeScale;

import org.bukkit.Bukkit;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IBankItemsBank}.
 */
class BankItemsBank implements IBankItemsBank, IDisposable {

    private final String _name;
    private final String _searchName;
    private final UUID _ownerId;
    private final IDataNode _dataNode;
    private final IDataNode _accountsNode;
    private final Object _sync = new Object();
    private final Date _created;
    private final TimedHashMap<UUID, BankItemsAccount> _accounts
            = new TimedHashMap<>(Nucleus.getPlugin(), 10, 20, TimeScale.MINUTES);

    volatile long _lastAccess;
    private volatile boolean _isDisposed;


    /**
     * Constructor.
     *
     * @param name      The name of the bank.
     * @param ownerId   The optional ID of the player owner.
     * @param dataNode  The banks data node.
     */
    BankItemsBank(String name, @Nullable UUID ownerId, IDataNode dataNode) {

        _name = name;
        _searchName = name.toLowerCase();
        _ownerId = ownerId;
        _dataNode = dataNode;

        String fileName = Bukkit.getOnlineMode() ? name : "offline-" + name;
        _accountsNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("bankitems.banks." + fileName));
        _accountsNode.load();
        _accountsNode.setAutoSaveMode(AutoSaveMode.ENABLED);

        long created = _dataNode.getLong("created");
        if (created == 0) {
            created = System.currentTimeMillis();
            _dataNode.set("created", created);
            _dataNode.save();
        }
        _created = new Date(created);

        _lastAccess = _dataNode.getLong("last-access", created);
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Nullable
    @Override
    public UUID getOwnerId() {
        return _ownerId;
    }

    @Override
    public Date getCreatedDate() {
        return _created;
    }

    @Override
    public Date getLastAccess() {
        return new Date(_lastAccess);
    }

    @Override
    public boolean hasAccount(UUID playerId) {
        return getAccount(playerId) != null;
    }

    @Nullable
    @Override
    public IBankItemsAccount getAccount(UUID playerId) {
        PreCon.notNull(playerId);

        synchronized (_sync) {
            BankItemsAccount account = _accounts.get(playerId);
            if (account == null) {
                account = loadAccount(playerId);

                if (account == null)
                    return null;

                _accounts.put(playerId, account);
            }
            return account;
        }
    }

    @Override
    public IBankItemsAccount createAccount(UUID playerId) {
        PreCon.notNull(playerId);

        if (_isDisposed)
            throw new IllegalStateException("Cannot use a disposed item bank.");

        synchronized (_sync) {
            BankItemsAccount account = _accounts.get(playerId);
            if (account == null) {
                IDataNode node = _dataNode.getNode(playerId.toString());

                account = new BankItemsAccount(playerId, this, node);

                node.set("created", System.currentTimeMillis());
                node.save();

                _accounts.put(playerId, account);
            }

            return account;
        }
    }

    @Override
    public boolean deleteAccount(UUID playerId) {
        PreCon.notNull(playerId);

        if (_isDisposed)
            throw new IllegalStateException("Cannot use a disposed item bank.");

        synchronized (_sync) {

            IBankItemsAccount account = _accounts.remove(playerId);
            if (account == null)
                return false;

            _dataNode.getNode(playerId.toString()).remove();
            _dataNode.save();

            return true;
        }
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        if (_isDisposed)
            return;

        synchronized (_sync) {

            if (_isDisposed)
                return;

            String fileName = Bukkit.getOnlineMode() ? _name : "offline-" + _name;

            DataStorage.remove(Nucleus.getPlugin(), new DataPath("bankitems.banks." + fileName));

            _dataNode.remove();
            _dataNode.save();
            _isDisposed = true;
        }
    }

    @Nullable
    BankItemsAccount loadAccount (UUID playerId) {

        if (!_accountsNode.hasNode(playerId.toString()))
            return null;

        return new BankItemsAccount(playerId, null, _accountsNode.getNode(playerId.toString()));
    }

    void updateLastAccess() {
        _lastAccess = System.currentTimeMillis();
        _dataNode.set("last-access", _lastAccess);
    }
}
