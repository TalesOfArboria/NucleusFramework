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

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.providers.bankitems.IBankItem;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsAccount;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsBank;
import com.jcwhatever.nucleus.providers.bankitems.InsufficientItemsException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackBuilder;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.items.MatchableItem;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IBankItemsAccount}.
 */
public class BankItemsAccount implements IBankItemsAccount {

    private final UUID _ownerId;
    private final BankItemsBank _bank;
    private final IDataNode _dataNode;
    private final Map<MatchableItem, BankItem> _items = new HashMap<>(10);
    private final Object _sync = new Object();
    private final Date _created;

    volatile long _lastAccess;

    /**
     * Constructor.
     *
     * @param ownerId   The ID of the account player owner.
     * @param bank      Optional owning bank.
     * @param dataNode  The data node.
     */
    BankItemsAccount(UUID ownerId, @Nullable BankItemsBank bank, IDataNode dataNode) {
        PreCon.notNull(ownerId);
        PreCon.notNull(dataNode);

        _ownerId = ownerId;
        _bank = bank;
        _dataNode = dataNode;

        long created = dataNode.getLong("created", System.currentTimeMillis());
        _created = new Date(created);

        _lastAccess = dataNode.getLong("last-access", created);

        load();
    }

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

    @Nullable
    @Override
    public IBankItemsBank getBank() {
        return _bank;
    }

    @Override
    public int getBalance() {
        int balance = 0;
        updateLastAccess();

        synchronized (_sync) {
            for (BankItem item : _items.values())
                balance += item.getAmount();
        }

        return balance;
    }

    @Override
    public int getBalance(Material material) {
        PreCon.notNull(material);

        ItemStack itemStack = new ItemStack(material);
        return getBalance(itemStack);
    }

    @Override
    public int getBalance(MaterialData materialData) {
        PreCon.notNull(materialData);

        ItemStack itemStack = new ItemStackBuilder(materialData).build();
        return getBalance(itemStack);
    }

    @Override
    public int getBalance(ItemStack matchingStack) {
        PreCon.notNull(matchingStack);
        updateLastAccess();

        synchronized (_sync) {
            BankItem item = _items.get(
                    new MatchableItem(matchingStack.clone(), ItemStackMatcher.getTypeMetaDurability()));

            return item != null ? item.getAmount() : 0;
        }
    }

    @Override
    public int deposit(Material material, int amount) {
        PreCon.notNull(material);

        ItemStack itemStack = new ItemStack(material);
        return deposit(itemStack, amount);
    }

    @Override
    public int deposit(MaterialData materialData, int amount) {
        PreCon.notNull(materialData);

        ItemStack itemStack = new ItemStackBuilder(materialData).build();

        return deposit(itemStack, amount);
    }

    @Override
    public int deposit(ItemStack itemStack) {
        return deposit(itemStack, itemStack.getAmount());
    }

    @Override
    public int deposit(ItemStack itemStack, int amount) {
        PreCon.notNull(itemStack);
        PreCon.positiveNumber(amount);

        checkDisposed();
        updateLastAccess();

        MatchableItem wrapper = new MatchableItem(itemStack, ItemStackMatcher.getTypeMetaDurability());

        synchronized (_sync) {
            BankItem item = _items.get(wrapper);

            if (amount == 0)
                return item != null ? item.getAmount() : 0;

            if (item == null) {
                UUID id = UUID.randomUUID();
                IDataNode itemNode = _dataNode.getNode(id.toString());
                item = new BankItem(id, itemStack, 0, itemNode);
                _items.put(wrapper, item);
            }

            item.deposit(amount);

            return item.getAmount();
        }
    }

    @Override
    public List<ItemStack> withdraw() {

        checkDisposed();
        updateLastAccess();

        synchronized (_sync) {
            List<ItemStack> result = new ArrayList<>(_items.size() * 10);

            for (BankItem item : _items.values()) {
                for (IBankItem stack : item.getItems()) {
                    result.add(stack.toItemStack(stack.getAmount()));
                }
            }

            _items.clear();
            return result;
        }
    }

    @Override
    public List<ItemStack> withdraw(Material material) throws InsufficientItemsException {
        PreCon.notNull(material);

        ItemStack itemStack = new ItemStack(material);

        synchronized (_sync) {
            int balance = getBalance(itemStack);
            return withdraw(itemStack, balance);
        }
    }

    @Override
    public List<ItemStack> withdraw(Material material, int amount) throws InsufficientItemsException {
        PreCon.notNull(material);

        ItemStack itemStack = new ItemStack(material);
        return withdraw(itemStack, amount);
    }

    @Override
    public List<ItemStack> withdraw(MaterialData materialData) throws InsufficientItemsException {
        PreCon.notNull(materialData);

        ItemStack itemStack = new ItemStackBuilder(materialData).build();

        synchronized (_sync) {
            int balance = getBalance(itemStack);
            return withdraw(itemStack, balance);
        }
    }

    @Override
    public List<ItemStack> withdraw(MaterialData materialData, int amount) throws InsufficientItemsException {
        PreCon.notNull(materialData);

        ItemStack itemStack = new ItemStackBuilder(materialData).build();
        return withdraw(itemStack, amount);
    }

    @Override
    public List<ItemStack> withdraw(ItemStack matchingStack) throws InsufficientItemsException {
        return withdraw(matchingStack, matchingStack.getAmount());
    }

    @Override
    public List<ItemStack> withdraw(ItemStack matchingStack, int amount) throws InsufficientItemsException {
        PreCon.notNull(matchingStack);
        PreCon.positiveNumber(amount);

        checkDisposed();
        updateLastAccess();

        if (amount == 0)
            return CollectionUtils.unmodifiableList();

        MatchableItem wrapper = new MatchableItem(matchingStack, ItemStackMatcher.getTypeMetaDurability());

        synchronized (_sync) {
            BankItem item = _items.get(wrapper);
            if (item == null || amount > item.getAmount())
                throw new InsufficientItemsException();

            item.withdraw(amount);

            int stackSize = matchingStack.getType().getMaxStackSize();
            int totalStacks = (int) Math.ceil((double) amount / stackSize);
            int lastStackSize = amount % stackSize;

            List<ItemStack> result = new ArrayList<>(totalStacks);

            for (int i = 0; i < totalStacks; i++) {

                int size = i < totalStacks - 1 || lastStackSize == 0
                        ? stackSize
                        : lastStackSize;

                result.add(item.toItemStack(size));
            }

            return Collections.unmodifiableList(result);
        }
    }

    @Nullable
    @Override
    public IBankItem getItem(ItemStack matchingStack) {
        PreCon.notNull(matchingStack);

        updateLastAccess();

        MatchableItem wrapper = new MatchableItem(matchingStack, ItemStackMatcher.getTypeMetaDurability());

        synchronized (_sync) {
            return _items.get(wrapper);
        }
    }

    @Override
    public List<IBankItem> getItems() {

        updateLastAccess();

        LinkedList<IBankItem> result = new LinkedList<>();

        synchronized (_sync) {
            for (BankItem item : _items.values()) {
                result.addAll(item.getItems());
            }
            return new ArrayList<>(result);
        }
    }

    void checkDisposed() {
        if (_bank == null)
            return;

        if (_bank.isDisposed())
            throw new RuntimeException("Cannot use an account from a disposed item bank.");
    }

    private void load() {
        for (IDataNode itemNode : _dataNode) {

            UUID id = TextUtils.parseUUID(itemNode.getName());
            if (id == null) {
                NucMsg.debug("An account bank item's data node has " +
                        "an invalid name (should be a UUID): {0}", itemNode.getName());
                continue;
            }

            ItemStack[] items = itemNode.getItemStacks("item");
            if (items == null || items.length == 0) {
                NucMsg.debug("An account bank item's data node has no item specified.");
                continue;
            }

            int amount = itemNode.getInteger("amount");
            if (amount <= 0) {
                NucMsg.debug("An account bank item's data node has an amount less " +
                        "than or equal to zero: {0}", amount);
                continue;
            }

            BankItem item = new BankItem(id, items[0], amount, itemNode);
            _items.put(new MatchableItem(items[0]), item);
        }
    }

    private void updateLastAccess() {
        _lastAccess = System.currentTimeMillis();
        _dataNode.set("last-access", _lastAccess);

        if (_bank != null) {
            _bank.updateLastAccess();
        }
    }
}
