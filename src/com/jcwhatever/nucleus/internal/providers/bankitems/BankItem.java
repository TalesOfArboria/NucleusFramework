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

import com.jcwhatever.nucleus.collections.wrap.IteratorWrapper;
import com.jcwhatever.nucleus.providers.bankitems.IBankItem;
import com.jcwhatever.nucleus.providers.bankitems.InsufficientItemsException;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IBankItem}.
 */
public class BankItem implements IBankItem {

    private final UUID _id;
    private final BankItem _root;
    private final ItemStack _item;
    private final PageStartIndex _start = PageStartIndex.ONE;
    private final IDataNode _dataNode;
    private final Object _sync;

    private int _itemsPerPage = 6 * 9;
    private volatile int _amount;

    /**
     * Constructor.
     *
     * @param id             The unique ID of the bank item.
     * @param matchingStack  An item stack that is represented by the {@code BankItem}.
     * @param amount         The quantity available.
     * @param dataNode       The items data node.
     */
    public BankItem(UUID id, ItemStack matchingStack, int amount, IDataNode dataNode) {
        this(id, null, matchingStack, amount, dataNode);
    }

    private BankItem(UUID id, @Nullable BankItem root, ItemStack matchingStack, int amount, IDataNode dataNode) {
        PreCon.notNull(matchingStack);

        _id = id;
        _root = root != null ? root : this;
        _sync = isRootItem() ? new Object() : null;
        _item = matchingStack;
        _amount = amount;
        _dataNode = dataNode;
    }

    @Override
    public UUID getId() {
        return _id;
    }

    @Override
    public boolean isRootItem() {
        return _root == this;
    }

    @Override
    public IBankItem getRootItem() {
        return _root;
    }

    @Override
    public Material getType() {
        return _item.getType();
    }

    @Override
    public int getAmount() {
        return _amount;
    }

    @Override
    public int getRootAmount() {
        return _root._amount;
    }

    @Override
    public int getMaxStackSize() {
        return getType().getMaxStackSize();
    }

    @Override
    public int getTotalStacks() {
        return (int)Math.ceil((double)_amount / getMaxStackSize());
    }

    public boolean deposit(int amount) {
        PreCon.positiveNumber(amount);

        if (!isRootItem())
            throw new RuntimeException("Only the root BankItem can accept deposits.");

        if (amount == 0)
            return true;

        synchronized (_root._sync) {

            if (isRootItem()) {
                _amount += amount;
            } else {

                int result = _amount + amount;

                if (result > getMaxStackSize())
                    return false;

                _amount += amount;
                _root._amount += amount;
            }

            _dataNode.set("amount", _root._amount);
            _dataNode.save();

            return true;
        }
    }

    public void withdraw(int amount) throws InsufficientItemsException {
        PreCon.positiveNumber(amount);

        if (!isRootItem())
            throw new RuntimeException("Only the root BankItem can be withdrawn from.");

        if (amount == 0)
            return;

        if (amount > _amount)
            throw new InsufficientItemsException();

        synchronized (_root._sync) {
            if (isRootItem()) {
                _amount -= amount;
            } else {
                _amount -= amount;
                _root._amount -= amount;
            }

            _dataNode.set("amount", _root._amount);
        }
    }

    @Override
    public ItemStack toItemStack(int amount) {
        ItemStack itemStack = _item.clone();
        itemStack.setAmount(amount);
        return itemStack;
    }

    @Override
    public PageStartIndex getPageStartIndex() {
        return _start;
    }

    @Override
    public int size() {
        return getTotalStacks();
    }

    @Override
    public int getTotalPages() {
        return (int)Math.ceil(getTotalStacks() / (double)_itemsPerPage);
    }

    @Override
    public int getItemsPerPage() {
        return _itemsPerPage;
    }

    @Override
    public void setItemsPerPage(int itemsPerPage) {
        _itemsPerPage = itemsPerPage;
    }

    @Override
    public List<IBankItem> getPage(int page) {

        if (_amount == 0)
            return CollectionUtils.unmodifiableList();

        synchronized (_root._sync) {

            int start = getStartIndex(page);
            int end = getEndIndex(page);

            int totalStacks = end - start;

            if (totalStacks == 0)
                return CollectionUtils.unmodifiableList();

            List<IBankItem> result = new ArrayList<>(totalStacks);

            int lastStackSize = end == getTotalStacks()
                    ? getLastStackSize()
                    : getMaxStackSize();

            for (int i = 0; i < totalStacks; i++) {

                int size = i < totalStacks - 1 || lastStackSize == 0
                        ? getMaxStackSize()
                        : lastStackSize;

                result.add(new BankItem(_id, _root, _item, size, _dataNode));
            }

            return result;
        }
    }

    @Override
    public List<IBankItem> getItems() {
        if (_amount == 0)
            return CollectionUtils.unmodifiableList();

        synchronized (_root._sync) {

            int totalStacks = getTotalStacks();
            int lastStackSize = getLastStackSize();

            if (totalStacks == 0)
                return CollectionUtils.unmodifiableList();

            List<IBankItem> result = new ArrayList<>(totalStacks);

            for (int i = 0; i < totalStacks; i++) {

                int size = i < totalStacks - 1 || lastStackSize == 0
                        ? getMaxStackSize()
                        : lastStackSize;

                result.add(new BankItem(_id, _root, _item, size, _dataNode));
            }

            return result;
        }
    }

    @Override
    public Iterator<IBankItem> iterator(final int page) {
        return new IteratorWrapper<IBankItem>() {

            Iterator<IBankItem> iterator = getPage(page).iterator();

            @Override
            protected Iterator<IBankItem> iterator() {
                return iterator;
            }

            @Override
            public boolean onRemove(IBankItem remove) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Iterator<IBankItem> iterator() {

        return new IteratorWrapper<IBankItem>() {

            Iterator<IBankItem> iterator = getItems().iterator();

            @Override
            protected Iterator<IBankItem> iterator() {
                return iterator;
            }

            @Override
            public boolean onRemove(IBankItem remove) {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected int getStartIndex(int page) {
        return (page - _start.getStartIndex()) * _itemsPerPage;
    }

    protected int getEndIndex(int page) {
        return Math.min(getTotalStacks(), getStartIndex(page) + _itemsPerPage);
    }

    protected int getLastStackSize() {
        if (_amount == 0)
            return 0;

        return _amount % getMaxStackSize();
    }
}
