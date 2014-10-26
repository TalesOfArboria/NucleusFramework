/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.items.bank;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.collections.TimedMap;
import com.jcwhatever.bukkit.generic.performance.SingleCache;
import com.jcwhatever.bukkit.generic.storage.DataStorage;
import com.jcwhatever.bukkit.generic.storage.DataStorage.DataPath;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Static methods for managing player Item Bank Accounts.
 */
public class ItemBankManager {

    private static final int CACHE_DURATION = 20 * 60 * 5; // 5 minutes
    private static TimedMap<UUID, ItemBankAccount> _recentAccounts = new TimedMap<UUID, ItemBankAccount>();
    private static SingleCache<UUID, ItemBankAccount> _accountCache = new SingleCache<UUID, ItemBankAccount>();

    private ItemBankManager() {}


    /**
     * Get a list of bank items in the specified players account.
     *
     * @param p  The player
     */
    @Nullable
    public static List<BankItem> getBankItems (Player p) {

        return getBankItems(p.getUniqueId());
    }

    /**
     * Get a list of bank items in the specified players account.
     *
     * @param playerId  The id of the player.
     */
    @Nullable
    public static List<BankItem> getBankItems (UUID playerId) {
        PreCon.notNull(playerId);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.getBankItems();
    }

    /**
     * Get a bank item from the specified players account by item id.
     *
     * @param p       The player
     * @param itemId  The id of the bank item.
     */
    @Nullable
    public static BankItem getBankItem (Player p, UUID itemId) {
        PreCon.notNull(p);
        PreCon.notNull(itemId);

        return getBankItem(p.getUniqueId(), itemId);
    }

    /**
     * Get a bank item from the specified players account by item id.
     *
     * @param playerId  The id of the player.
     * @param itemId    The id of the bank item.
     */
    @Nullable
    public static BankItem getBankItem (UUID playerId, UUID itemId) {
        PreCon.notNull(playerId);
        PreCon.notNull(itemId);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.getBankItem(itemId);
    }

    /**
     * Get a bank item from the specified players account using an item stack
     * as a search term.
     *
     * @param p          The player
     * @param itemStack  The item stack that represents the bank item to get.
     */
    @Nullable
    public static BankItem getBankItem (Player p, ItemStack itemStack) {
        return getBankItem(p.getUniqueId(), itemStack);
    }

    /**
     * Get a bank item from the specified players account using an item stack
     * as a search term.
     *
     * @param playerId   The id of the player.
     * @param itemStack  The item stack that represents the bank item to get.
     */
    @Nullable
    public static BankItem getBankItem (UUID playerId, ItemStack itemStack) {
        PreCon.notNull(playerId);
        PreCon.notNull(itemStack);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.getBankItem(itemStack);
    }

    /**
     * Withdraw items from the specified players account using an item stack
     * as a search term.
     *
     * @param p          The player
     * @param itemStack  The item stack that represents the bank item to get.
     * @param qty        The number of items to withdraw.
     *
     * @return An array of withdrawn {@code ItemStacks}. Each array element holds up to the items maximum stack size.
     *
     * @throws InsufficientItemsException if there are not enough items in the account to withdraw the quantity
     */
    public static ItemStack[] withdraw (Player p, ItemStack itemStack, int qty) throws InsufficientItemsException {
        PreCon.notNull(p);

        return withdraw(p.getUniqueId(), itemStack, qty);
    }

    /**
     * Withdraw items from the specified players account using an item stack
     * as a search term.
     *
     * @param playerId   The id of the player.
     * @param itemStack  The item stack that represents the bank item to get.
     * @param qty        The number of items to withdraw.
     *
     * @return An array of withdrawn {@code ItemStacks}. Each array element holds up to the items maximum stack size.
     *
     * @throws InsufficientItemsException if there are not enough items in the account to withdraw the quantity
     */
    public static ItemStack[] withdraw (UUID playerId, ItemStack itemStack, int qty) throws InsufficientItemsException {
        PreCon.notNull(playerId);
        PreCon.notNull(itemStack);
        PreCon.greaterThanZero(qty);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.withdraw(itemStack, qty);
    }

    /**
     * Withdraw items from the specified players account.
     *
     * @param p          The player
     * @param bankItem   The bank item representing an item from the players account.
     * @param qty        The number of items to withdraw.
     *
     * @return An array of withdrawn {@code ItemStacks}. Each array element holds up to the items maximum stack size.
     *
     * @throws InsufficientItemsException if there are not enough items in the account to withdraw the quantity
     */
    public static ItemStack[] withdraw (Player p, BankItem bankItem, int qty) throws InsufficientItemsException {
        PreCon.notNull(p);

        return withdraw(p.getUniqueId(), bankItem, qty);
    }

    /**
     * Withdraw items from the specified players account.
     *
     * @param playerId   The id of the player.
     * @param bankItem   The bank item representing an item from the players account.
     * @param qty        The number of items to withdraw.
     *
     * @return An array of withdrawn {@code ItemStacks}. Each array element holds up to the items maximum stack size.
     *
     * @throws InsufficientItemsException if there are not enough items in the account to withdraw the quantity
     */
    public static ItemStack[] withdraw (UUID playerId, BankItem bankItem, int qty) throws InsufficientItemsException {
        PreCon.notNull(playerId);
        PreCon.notNull(bankItem);
        PreCon.greaterThanZero(qty);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.withdraw(bankItem, qty);

    }

    /**
     * Deposit an {@code ItemStack} into the specified players item account.
     * Quantity deposited is the specified item stacks amount.
     *
     * @param p          The player
     * @param itemStack  The item stack to deposit.
     *
     * @return  The {@code BankItem} that represents the deposited item.
     */
    @Nullable
    public static BankItem deposit (Player p, ItemStack itemStack) {
        PreCon.notNull(p);

        return deposit(p.getUniqueId(), itemStack);
    }

    /**
     * Deposit an {@code ItemStack} into the specified players item account.
     * Quantity deposited is the specified item stacks amount.
     *
     * @param playerId   The id of the player.
     * @param itemStack  The item stack to deposit.
     *
     * @return  The {@code BankItem} that represents the deposited item.
     */
    @Nullable
    public static BankItem deposit (UUID playerId, ItemStack itemStack) {
        PreCon.notNull(playerId);
        PreCon.notNull(itemStack);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.deposit(itemStack);
    }

    /**
     * Deposit an {@code ItemStack} into the specified players item account.
     * Specify the quantity deposited.
     *
     * @param p          The player
     * @param itemStack  The item stack to deposit.
     * @param qty        The quantity to deposit.
     *
     * @return  The {@code BankItem} that represents the deposited item.
     */
    @Nullable
    public static BankItem deposit (Player p, ItemStack itemStack, int qty) {
        PreCon.notNull(p);

        return deposit(p.getUniqueId(), itemStack, qty);
    }

    /**
     * Deposit an {@code ItemStack} into the specified players item account.
     * Specify the quantity deposited.
     *
     * @param playerId   The id of the player.
     * @param itemStack  The item stack to deposit.
     * @param qty        The quantity to deposit.
     *
     * @return  The {@code BankItem} that represents the deposited item.
     */
    @Nullable
    public static BankItem deposit (UUID playerId, ItemStack itemStack, int qty) {
        PreCon.notNull(playerId);
        PreCon.notNull(itemStack);
        PreCon.greaterThanZero(qty);

        ItemBankAccount account = getAccount(playerId);
        if (account == null)
            return null;

        return account.deposit(itemStack, qty);
    }

    // Internal : ItemBankAccount kept private to prevent memory leaks
    private static ItemBankAccount getAccount (UUID playerId) {

        if (_accountCache.keyEquals(playerId))
            return _accountCache.getValue();

        ItemBankAccount account = _recentAccounts.get(playerId);

        if (account == null) {
            account = loadAccountFromFile(playerId);

            _recentAccounts.put(playerId, account, CACHE_DURATION);
        }

        _accountCache.set(playerId, account);

        return account;
    }

    private static ItemBankAccount loadAccountFromFile (UUID playerId) {

        IDataNode dataNode = DataStorage.getStorage(GenericsLib.getPlugin(), new DataPath("bank." + playerId));

        if (!dataNode.load())
            return null;

        return new ItemBankAccount(playerId, dataNode);
    }


    
}
