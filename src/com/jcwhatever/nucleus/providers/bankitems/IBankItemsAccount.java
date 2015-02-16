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

package com.jcwhatever.nucleus.providers.bankitems;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * A {@link IBankItem} account.
 */
public interface IBankItemsAccount {

    /**
     * Get the unique ID of the player account owner.
     */
    UUID getOwnerId();

    /**
     * Get the date/time the account was created.
     */
    Date getCreatedDate();

    /**
     * Get the date/time the account was last accessed. If the
     * account has never been accessed, the created date is returned.
     */
    Date getLastAccess();

    /**
     * Get the bank the account is from. Returns null if the
     * account is a global account.
     */
    @Nullable
    IBankItemsBank getBank();

    /**
     * Get the sum of the amounts of all items in the account.
     */
    int getBalance();

    /**
     * Get the sum of the amounts of all items in the account
     * that are of the specified material.
     *
     * @param material  The material.
     */
    int getBalance(Material material);

    /**
     * Get the sum of the amounts of all items in the account
     * that are of the specified material data.
     *
     * @param materialData  The material data.
     */
    int getBalance(MaterialData materialData);

    /**
     * Get the sum of the amounts of all items in the account
     * that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param matchingStack  The {@link org.bukkit.inventory.ItemStack}.
     */
    int getBalance(ItemStack matchingStack);


    /**
     * Deposit an amount of an item of the specified material.
     *
     * @param material  The material.
     * @param amount    The amount.
     *
     * @return  The new balance of the material.
     */
    int deposit(Material material, int amount);

    /**
     * Deposit an amount of an item of the specified material data.
     *
     * @param materialData  The material data.
     * @param amount        The amount.
     *
     * @return  The new balance of the material data.
     */
    int deposit(MaterialData materialData, int amount);

    /**
     * Deposit an item, using the items amount.
     *
     * <p>The item is only used to tell the account what kind
     * of item is being deposited. The instance passed into the
     * method is not modified.</p>
     *
     * @param itemStack  The item to deposit.
     *
     * @return  The new balance of the item.
     */
    int deposit(ItemStack itemStack);

    /**
     * Deposit a specified amount of an item.
     *
     * <p>The item is only used to tell the account what kind
     * of item is being deposited. The instance passed into the
     * method is not modified.</p>
     *
     * @param itemStack  The item to deposit.
     * @param amount     The amount to deposit.
     *
     * @return  The new balance of the item.
     */
    int deposit(ItemStack itemStack, int amount);

    /**
     * Withdraw all items from the account.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items withdrawn.
     */
    List<ItemStack> withdraw();

    /**
     * Withdraw all items that are of the specified material
     * from the account.
     *
     * @param material  The material.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items withdrawn.
     */
    List<ItemStack> withdraw(Material material) throws InsufficientItemsException;

    /**
     * Withdraw the specified amount of items that are of the
     * specified material from the account.
     *
     * @param material  The material.
     * @param amount    The amount to withdraw.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items withdrawn.
     *
     * @throws InsufficientItemsException if the amount specified is greater than the balance.
     */
    List<ItemStack> withdraw(Material material, int amount)
            throws InsufficientItemsException;

    /**
     * Withdraw all items that are of the specified material data
     * from the account.
     *
     * @param materialData  The material data.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items withdrawn.
     */
    List<ItemStack> withdraw(MaterialData materialData) throws InsufficientItemsException;

    /**
     * Withdraw the specified amount of items that are of the specified
     * material data from the account.
     *
     * @param materialData  The material data.
     * @param amount        The amount to withdraw.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items
     * withdrawn.
     *
     * @throws InsufficientItemsException if the amount specified is greater than the balance.
     */
    List<ItemStack> withdraw(MaterialData materialData, int amount)
            throws InsufficientItemsException;;

    /**
     * Withdraw all items that match the specified {@link org.bukkit.inventory.ItemStack}
     * from the account.
     *
     * @param matchingStack  The stack used for matching purposes. The amount is not matched.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items
     * withdrawn.
     */
    List<ItemStack> withdraw(ItemStack matchingStack) throws InsufficientItemsException;

    /**
     * Withdraw the specified amount of items that match the specified
     * {@link org.bukkit.inventory.ItemStack} from the account.
     *
     * @param matchingStack  The stack used for matching purposes. The amount is not matched.
     * @param amount         The amount to withdraw.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that are the items
     * withdrawn.
     *
     * @throws InsufficientItemsException if the amount specified is greater than the balance.
     */
    List<ItemStack> withdraw(ItemStack matchingStack, int amount)
            throws InsufficientItemsException;

    /**
     * Get the root {@link IBankItem} instance that represents all stacks in the account
     * that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param matchingStack  The stack used for matching purposes. The amount is not matched.
     *
     * @return  The accounts root {@link IBankItem} or null if the balance of the
     * specified item is 0.
     */
    @Nullable
    IBankItem getItem(ItemStack matchingStack);

    /**
     * Get all root {@link IBankItem}'s contained within the account.
     */
    List<IBankItem> getItems();
}
