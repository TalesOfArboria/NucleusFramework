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


package com.jcwhatever.nucleus.itembank;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackComparer;
import com.jcwhatever.nucleus.utils.items.ItemWrapper;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a single players Item Bank Account.
 * Internally used (as opposed to public) to prevent memory leaks.
 */
class ItemBankAccount {

    private UUID _playerId;
    private Map<UUID, BankItem> _idMap = new HashMap<UUID, BankItem>(1000);
    private Map<ItemWrapper, BankItem> _itemMap = new HashMap<ItemWrapper, BankItem>(1000);
    private IDataNode _dataNode;


    /**
     * Constructor.
     *
     * @param playerId  The id of the account holder
     * @param dataNode  The account data storage node
     */
    ItemBankAccount(UUID playerId, IDataNode dataNode) {
        PreCon.notNull(playerId);
        PreCon.notNull(dataNode);

        _playerId = playerId;
        _dataNode = dataNode;

        loadSettings();
    }


    /**
     * Get the account holder id.
     */
    public UUID getPlayerId () {

        return _playerId;
    }


    /**
     * Get the account data storage node.
     */
    public IDataNode getDataNode () {

        return _dataNode;
    }

    /**
     * Get a list of bank items which represent the
     * items in the account.
     */
    public List<BankItem> getBankItems () {

        return new ArrayList<BankItem>(_itemMap.values());
    }


    /**
     * Get a bank item by item id.
     *
     * @param itemId  The id of the bank item.
     */
    @Nullable
    public BankItem getBankItem (UUID itemId) {
        PreCon.notNull(itemId);

        return _idMap.get(itemId);
    }


    /**
     * Get a bank item that matches the provided {@code ItemStack}.
     *
     * @param itemStack  The item stack key
     */
    @Nullable
    public BankItem getBankItem (ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemWrapper wrapper = new ItemWrapper(itemStack, ItemStackComparer.getDurability());

        return _itemMap.get(wrapper);
    }


    /**
     * Withdraw items from the account.
     *
     * @param itemStack  An item stack that represents the items to withdraw.
     * @param qty        The number of items to withdraw.
     *
     * @return An array of the withdrawn items.
     *
     * @throws InsufficientItemsException if the items do no exist in the account or there are not enough
     */
    public ItemStack[] withdraw (ItemStack itemStack, int qty) throws InsufficientItemsException {
        PreCon.notNull(itemStack);
        PreCon.greaterThanZero(qty);

        ItemWrapper wrapper = new ItemWrapper(itemStack, ItemStackComparer.getDurability());

        BankItem bankItem = _itemMap.get(wrapper);
        if (bankItem == null)
            throw new InsufficientItemsException(_playerId, itemStack.clone(), 0);

        return withdraw(bankItem, qty);
    }


    /**
     * Withdraw items from the account.
     *
     * @param bankItem  The bank item to withdraw.
     * @param qty       The number of items to withdraw.
     *
     * @return An array of the withdrawn items.
     *
     * @throws InsufficientItemsException if there are not enough items to withdraw the desired qty.
     */
    public ItemStack[] withdraw (BankItem bankItem, int qty) throws InsufficientItemsException {
        PreCon.notNull(bankItem);
        PreCon.greaterThanZero(qty);

        if (bankItem.getQty() < qty) {
            throw new InsufficientItemsException(_playerId, bankItem.getItemWrapper().getItem().clone(), bankItem.getQty());
        }

        ItemStack[] results = bankItem.getItems(qty);
        bankItem.increment(-qty);

        if (bankItem.getQty() <= 0) {
            _idMap.remove(bankItem.getItemId());
            _itemMap.remove(bankItem.getItemWrapper());

            if (bankItem.getDataNode() != null) {
                bankItem.getDataNode().remove();
                bankItem.getDataNode().saveAsync(null);
            }
        }

        return results;
    }


    /**
     * Deposit an item stack into the item bank account.
     * The item stack amount is used for qty.
     *
     * @param itemStack  The item stack to deposit.
     *
     * @return The bank item representing the item stack.
     */
    public BankItem deposit (ItemStack itemStack) {
        PreCon.notNull(itemStack);

        return deposit(itemStack, itemStack.getAmount() > 0 ? itemStack.getAmount() : 1);
    }


    /**
     * Deposit an item stack into the item bank account.
     * The item stack amount is not used, specify the quantity.
     *
     * @param itemStack  The item stack to deposit.
     * @param qty        The quantity to deposit.
     *
     * @return  The bank item representing the item stack
     */
    public BankItem deposit (ItemStack itemStack, int qty) {
        PreCon.notNull(itemStack);
        PreCon.greaterThanZero(qty);

        ItemWrapper wrapper = new ItemWrapper(itemStack, ItemStackComparer.getDurability());

        BankItem bankItem = _itemMap.get(wrapper);
        IDataNode dataNode;

        if (bankItem != null) {
            bankItem.increment(qty);
            dataNode = _dataNode.getNode("items." + bankItem.getItemId());
        }
        else {
            ItemStack clone = itemStack.clone();
            clone.setAmount(1);

            UUID itemId = UUID.randomUUID();
            while (_idMap.get(itemId) != null) {
                itemId = UUID.randomUUID();
            }

            dataNode = _dataNode.getNode("items." + itemId);
            dataNode.set("item", clone);

            bankItem = new BankItem(itemId, this, clone, qty, dataNode);

            _idMap.put(bankItem.getItemId(), bankItem);
            _itemMap.put(wrapper, bankItem);
        }

        dataNode.set("qty", qty);
        dataNode.saveAsync(null);

        return bankItem;
    }


    private void loadSettings () {

        IDataNode itemsNode = _dataNode.getNode("items");

        for (IDataNode itemNode : itemsNode) {

            UUID itemId = TextUtils.parseUUID(itemNode.getName());
            if (itemId == null)
                continue;

            int qty = itemNode.getInteger("qty");
            if (qty <= 0) {
                itemNode.remove();
                continue;
            }

            ItemStack[] items = itemNode.getItemStacks("item");
            if (items == null || items.length == 0)
                continue;

            ItemStack item = items[0];

            BankItem bankItem = new BankItem(itemId, this, item, qty, itemNode);

            ItemWrapper wrapper = new ItemWrapper(item, ItemStackComparer.getDurability());

            _itemMap.put(wrapper, bankItem);
            _idMap.put(itemId, bankItem);
        }
    }

}
