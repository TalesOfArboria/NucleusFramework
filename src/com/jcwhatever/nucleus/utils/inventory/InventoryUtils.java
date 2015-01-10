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


package com.jcwhatever.nucleus.utils.inventory;

import com.jcwhatever.nucleus.extended.ArmorType;
import com.jcwhatever.nucleus.extended.MaterialExt;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackComparer;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Provides static methods to help with inventories of {@code ItemStack}'s.
 */
public final class InventoryUtils {

    private InventoryUtils() {}

    /**
     * Add and merge item stacks into an {@code ItemStack} array.
     *
     * <p>Merges matching item stacks if there is room.</p>
     *
     * @param array       The array to add items stacks to.
     * @param itemStacks  The item stacks to add.
     *
     * @return  A list of {@code ItemStack}'s that could not be added.
     */
    public static List<ItemStack> add(ItemStack[] array, ItemStack... itemStacks) {

        List<ItemStack> toAdd = new ArrayList<>(itemStacks.length);
        Collections.addAll(toAdd, itemStacks);
        ItemStackComparer comparer = ItemStackComparer.getDurability();

        for (int i=0; i < array.length; i++) {
            ItemStack item = array[i];

            Iterator<ItemStack> iterator = toAdd.iterator();
            while (iterator.hasNext()) {
                ItemStack newItem = iterator.next();

                if (newItem == null || newItem.getType() == Material.AIR) {
                    iterator.remove();
                    continue;
                }

                if (item == null || item.getType() == Material.AIR) {
                    array[i] = newItem;
                    iterator.remove();
                    continue;
                }

                int maxStackSize = item.getType().getMaxStackSize();

                // make sure item is not already full
                if (item.getAmount() == maxStackSize)
                    break;

                if (!comparer.isSame(item, newItem))
                    continue;

                // merge
                int space = maxStackSize - item.getAmount();
                int add = Math.min(newItem.getAmount(), space);

                item.setAmount(item.getAmount() + add);

                if (newItem.getAmount() >= add) {
                    iterator.remove();
                }
                else {
                    newItem.setAmount(newItem.getAmount() - add);
                }

                // check no more room in item
                if (add == space)
                    break;
            }

            // check no more items to add
            if (toAdd.size() == 0)
                break;
        }

        return toAdd;
    }

    /**
     * Add and merge item stacks into an {@code ItemStack} collection.
     *
     * <p>Merges matching item stacks if there is room.</p>
     *
     * @param collection  The array to add items stacks to.
     * @param itemStacks  The item stacks to add.
     */
    public static void add(Collection<ItemStack> collection, ItemStack... itemStacks) {

        List<ItemStack> list = collection instanceof ArrayList
                ? (ArrayList<ItemStack>)collection
                : new ArrayList<ItemStack>(collection);

        List<ItemStack> toAdd = new ArrayList<>(itemStacks.length);
        Collections.addAll(toAdd, itemStacks);

        ItemStackComparer comparer = ItemStackComparer.getDurability();

        for (int i=0; i < list.size(); i++) {
            ItemStack item = list.get(i);

            Iterator<ItemStack> iterator = toAdd.iterator();
            while (iterator.hasNext()) {
                ItemStack newItem = iterator.next();

                if (newItem == null || newItem.getType() == Material.AIR) {
                    iterator.remove();
                    continue;
                }

                if (item == null || item.getType() == Material.AIR) {
                    list.set(i, newItem);
                    iterator.remove();
                    continue;
                }

                int maxStackSize = item.getType().getMaxStackSize();

                // make sure item is not already full
                if (item.getAmount() == maxStackSize)
                    break;

                if (!comparer.isSame(item, newItem))
                    continue;

                // merge
                int space = maxStackSize - item.getAmount();
                int add = Math.min(newItem.getAmount(), space);

                item.setAmount(item.getAmount() + add);

                if (newItem.getAmount() >= add) {
                    iterator.remove();
                }
                else {
                    newItem.setAmount(newItem.getAmount() - add);
                }

                // check no more room in item
                if (add == space)
                    break;
            }

            // check no more items to add
            if (toAdd.size() == 0)
                break;
        }

        list.addAll(toAdd);

        if (!(collection instanceof ArrayList)) {
            collection.clear();
            collection.addAll(list);
        }
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static int getMax(Inventory inventory, ItemStack itemStack) {
        return getMax(inventory.getContents(), itemStack, ItemStackComparer.getDurability(), -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static int getMax(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
        return getMax(inventory.getContents(), itemStack, comparer, -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static int getMax(ItemStack[] contents, ItemStack itemStack) {
        return getMax(contents, itemStack, ItemStackComparer.getDurability(), -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
        return getMax(contents, itemStack, comparer, -1);
    }

    /**
     * Determine if there is enough room in the specified inventory
     * for the specified stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack) {
        return hasRoom(inventory, itemStack, itemStack.getAmount());
    }

    /**
     * Determine if there is enough room in the specified inventory
     * for the specified stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
        return hasRoom(inventory, itemStack, comparer, itemStack.getAmount());
    }

    /**
     * Determine if there is enough room in the specified inventory for
     * items of the same type as the specified stack in the amount of
     * the specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The amount of space needed.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, int qty) {
        return getMax(inventory.getContents(), itemStack, ItemStackComparer.getDurability(), qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified inventory for
     * items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {
        return getMax(inventory.getContents(), itemStack, comparer, qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified {@code ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack) {
        return getMax(contents, itemStack, ItemStackComparer.getDurability(), itemStack.getAmount())
                >= itemStack.getAmount();
    }

    /**
     * Determine if there is enough room in the specified {@code ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack, int qty) {
        return getMax(contents, itemStack, ItemStackComparer.getDurability(), qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified {@code ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
        return getMax(contents, itemStack, comparer, qty) >= qty;
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static int count (Inventory inventory, ItemStack itemStack) {
        return count(inventory, itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static int count (Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
        return count(inventory.getContents(), itemStack, comparer, -1);
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static int count (ItemStack[] contents, ItemStack itemStack) {
        return count(contents, itemStack, ItemStackComparer.getDurability(), -1);
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static int count (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
        return count(contents, itemStack, comparer, -1);
    }

    /**
     * Determine if the specified inventory contains an item stack
     * that matches the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static boolean has(Inventory inventory, ItemStack itemStack) {
        return has (inventory, itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Determine if the specified inventory contains an item stack
     * that matches the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static boolean has(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
        PreCon.notNull(inventory);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);

        ItemStack[] contents = inventory.getContents();

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item))
                return true;
        }

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            ArmorType type = ArmorType.getType(itemStack);

            if (type == ArmorType.HELMET && comparer.isSame(playerInventory.getHelmet(), itemStack))
                return true;

            if (type == ArmorType.CHESTPLATE && comparer.isSame(playerInventory.getChestplate(), itemStack))
                return true;

            if (type == ArmorType.LEGGINGS && comparer.isSame(playerInventory.getLeggings(), itemStack))
                return true;

            if (type == ArmorType.BOOTS && comparer.isSame(playerInventory.getBoots(), itemStack))
                return true;
        }

        return false;
    }

    /**
     * Determine if the specified inventory contains the specified quantity
     * of items that match the specified {@code ItemStack}.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean has (Inventory inventory, ItemStack itemStack, int qty) {
        return has(inventory, itemStack, ItemStackComparer.getDurability(), qty);
    }

    /**
     * Determine if the specified inventory contains the specified quantity
     * of items that match the specified {@code ItemStack}.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static boolean has (Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {
        return count(inventory, itemStack, comparer, qty) >= qty;
    }

    /**
     * Determine if the specified {@code ItemStack} array contains an item stack
     * that matches the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static boolean has(ItemStack[] contents, ItemStack itemStack) {
        return has(contents, itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Determine if the specified {@code ItemStack} array contains an item stack
     * that matches the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static boolean has(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item))
                return true;
        }

        return false;
    }

    /**
     * Determine if the specified {@code ItemStack} array contains the specified quantity
     * of items that match the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean has (ItemStack[] contents, ItemStack itemStack, int qty) {
        return has(contents, itemStack, ItemStackComparer.getDurability(), qty);
    }

    /**
     * Determine if the specified {@code ItemStack} array contains the specified quantity
     * of items that match the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static boolean has (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);
        PreCon.positiveNumber(qty);

        int count = count(contents, itemStack, comparer, qty);

        return count >= qty;
    }

    /**
     * Get all {@code ItemStack}'s that match the specified {@code ItemStack} from
     * the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static ItemStack[] getAll (Inventory inventory, ItemStack itemStack) {
        return getAll(inventory, itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Get all {@code ItemStack}'s that match the specified {@code ItemStack} from
     * the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static ItemStack[] getAll (Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
        return getAll(inventory.getContents(), itemStack, comparer);
    }

    /**
     * Get all {@code ItemStack}'s that match the specified {@code ItemStack} from
     * the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     */
    public static ItemStack[] getAll (ItemStack[] contents, ItemStack itemStack) {
        return getAll(contents, itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Get all {@code ItemStack}'s that match the specified {@code ItemStack} from
     * the specified {@code ItemStack} array.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static ItemStack[] getAll (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);

        List<ItemStack> items = new ArrayList<ItemStack>(contents.length);

        for (ItemStack item : contents) {

            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item))
                items.add(item);
        }

        return items.toArray(new ItemStack[items.size()]);
    }


    /**
     * Remove specified items from the the {@code ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStack... itemStacks) {

        ItemStackComparer comparer = ItemStackComparer.getDurability();

        return remove(contents, comparer, itemStacks);
    }

    /**
     * Remove specified items from the the {@code ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param comparer    The comparer to use.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStackComparer comparer,
                                         ItemStack... itemStacks) {
        PreCon.notNull(contents);
        PreCon.notNull(comparer);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.length);

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(contents, item, comparer, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the {@code ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, Collection<ItemStack> itemStacks) {

        ItemStackComparer comparer = ItemStackComparer.getDurability();

        return remove(contents, comparer, itemStacks);
    }

    /**
     * Remove specified items from the the {@code ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param comparer    The comparer to use.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStackComparer comparer,
                                         Collection<ItemStack> itemStacks) {
        PreCon.notNull(contents);
        PreCon.notNull(comparer);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.size());

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(contents, item, comparer, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStack... itemStacks) {

        ItemStackComparer comparer = ItemStackComparer.getDurability();

        return remove(inventory, comparer, itemStacks);
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param comparer    The comparer to use.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStackComparer comparer,
                                         ItemStack... itemStacks) {
        PreCon.notNull(inventory);
        PreCon.notNull(comparer);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.length);

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(inventory, item, comparer, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, Collection<ItemStack> itemStacks) {

        ItemStackComparer comparer = ItemStackComparer.getDurability();

        return remove(inventory, comparer, itemStacks);
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param comparer    The comparer to use.
     * @param itemStacks  The {@code ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStackComparer comparer,
                                         Collection<ItemStack> itemStacks) {
        PreCon.notNull(inventory);
        PreCon.notNull(comparer);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.size());

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(inventory, item, comparer, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove a specified quantity of {@code ItemStack}'s from the specified inventory
     * that match the specified {@code ItemStack} array.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(Inventory inventory,
                                               ItemStack itemStack,
                                               int qty) {

        return removeAmount(inventory, itemStack, ItemStackComparer.getDurability(), qty);
    }

    /**
     * Remove a specified quantity of {@code ItemStack}'s from the specified inventory
     * that match the specified {@code ItemStack} array.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(Inventory inventory,
                                               ItemStack itemStack,
                                               ItemStackComparer comparer, int qty) {
        PreCon.notNull(inventory);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);
        PreCon.positiveNumber(qty);

        int size = inventory.getSize();

        List<ItemStack> results = new ArrayList<ItemStack>(size);

        int qtyLeft = qty;

        for (int i=0; i < size ; i++) {

            if (qtyLeft <= 0)
                return results;

            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item)) {

                ItemStack clone = item.clone();

                if (item.getAmount() > qtyLeft) {

                    int newAmount = item.getAmount() - qtyLeft;
                    item.setAmount(newAmount);
                    clone.setAmount(qtyLeft);
                    results.add(clone);

                    inventory.setItem(i, item);

                    break;
                }
                else {

                    qtyLeft -= item.getAmount();

                    clone.setAmount(item.getAmount());
                    results.add(clone);

                    inventory.setItem(i, ItemStackUtils.AIR);
                }
            }
        }

        if (qtyLeft > 0 && inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            ArmorType armorType = ArmorType.getType(itemStack);

            if (armorType == ArmorType.HELMET && comparer.isSame(playerInventory.getHelmet(), itemStack)) {
                results.add(playerInventory.getHelmet());
                playerInventory.setHelmet(null);
            }

            if (armorType == ArmorType.CHESTPLATE && comparer.isSame(playerInventory.getChestplate(), itemStack)) {
                results.add(playerInventory.getChestplate());
                playerInventory.setChestplate(null);
            }

            if (armorType == ArmorType.LEGGINGS && comparer.isSame(playerInventory.getLeggings(), itemStack)) {
                results.add(playerInventory.getLeggings());
                playerInventory.setLeggings(null);
            }

            if (armorType == ArmorType.BOOTS && comparer.isSame(playerInventory.getBoots(), itemStack)) {
                results.add(playerInventory.getBoots());
                playerInventory.setLeggings(null);
            }
        }

        return results;
    }

    /**
     * Remove a specified quantity of {@code ItemStack}'s from the specified
     * {@code ItemStack} array that match the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(ItemStack[] contents,
                                               ItemStack itemStack,
                                               int qty) {

        return removeAmount(contents, itemStack, ItemStackComparer.getDurability(), qty);
    }

    /**
     * Remove a specified quantity of {@code ItemStack}'s from the specified
     * {@code ItemStack} array that match the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(ItemStack[] contents,
                                               ItemStack itemStack,
                                               ItemStackComparer comparer, int qty) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);
        PreCon.positiveNumber(qty);

        List<ItemStack> results = new ArrayList<ItemStack>(contents.length);

        int qtyLeft = qty;

        for (int i=0; i < contents.length; i++) {

            if (qtyLeft <= 0)
                return results;

            ItemStack item = contents[i];

            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item)) {

                ItemStack clone = item.clone();

                if (item.getAmount() > qtyLeft) {

                    int newAmount = item.getAmount() - qtyLeft;
                    item.setAmount(newAmount);
                    clone.setAmount(qtyLeft);
                    results.add(clone);

                    contents[i] = item;

                    return results;
                }
                else {

                    qtyLeft -= item.getAmount();

                    clone.setAmount(item.getAmount());
                    results.add(clone);

                    contents[i] = ItemStackUtils.AIR;
                }
            }
        }

        return results;
    }

    /**
     * Clear an inventory. If the inventory is a {@code PlayerInventory},
     * the armor contents are also cleared.
     *
     * @param inventory  The inventory to clear.
     */
    public static void clearAll(Inventory inventory) {
        PreCon.notNull(inventory);

        inventory.clear();
        inventory.setContents(new ItemStack[inventory.getSize()]); // 36

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;
            playerInventory.setHelmet(null);
            playerInventory.setChestplate(null);
            playerInventory.setLeggings(null);
            playerInventory.setBoots(null);
            playerInventory.setItemInHand(null);
        }
    }

    /**
     * Clear an inventory. If the inventory is a {@code PlayerInventory},
     * the armor contents are also cleared.
     *
     * @param contents  The inventory contents.
     */
    public static void clearAll(ItemStack[] contents) {
        PreCon.notNull(contents);

        for (int i=0; i < contents.length;i++) {
            contents[i] = null;
        }
    }

    /**
     * Repair all repairable items in an inventory. If the inventory
     * is a {@code PlayerInventory}, the armor contents are also
     * repaired.
     *
     * @param inventory  The inventory with items to repair.
     */
    public static void repairAll(Inventory inventory) {
        PreCon.notNull(inventory);

        ItemStack[] contents = inventory.getContents();
        repairAll(contents);

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            ItemStack[] armor = playerInventory.getArmorContents();
            repairAll(armor);
        }
    }

    /**
     * Repair all repairable items in an {@code ItemStack} array.
     *
     * @param contents  The inventory contents.
     */
    public static void repairAll(ItemStack[] contents) {
        PreCon.notNull(contents);

        for (ItemStack stack : contents) {
            if (stack == null || !ItemStackUtils.isRepairable(stack))
                continue;

            stack.setDurability((short) -32768);
        }
    }

    /**
     * Determine if an inventory is empty. If the inventory is a
     * {code PlayerInventory}, the armor contents are included
     * in the check.
     *
     * @param inventory  The inventory to check.
     */
    public static boolean isEmpty(Inventory inventory) {
        PreCon.notNull(inventory);

        boolean isContentsEmpty = isEmpty(inventory.getContents());

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            return isContentsEmpty && isEmpty(playerInventory.getArmorContents());
        }
        return isContentsEmpty;
    }

    /**
     * Determine if an {code ItemStack} array is empty.
     *
     * @param contents  The inventory contents.
     */
    public static boolean isEmpty(ItemStack[] contents) {
        PreCon.notNull(contents);

        for (ItemStack stack : contents) {
            if (stack == null || stack.getType() == Material.AIR)
                continue;

            return false;
        }

        return true;
    }

    private static int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int totalRequired) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);

        MaterialExt ext = MaterialExt.from(itemStack.getType());
        if (ext.getMaxStackSize() == 0)
            return 0;

        int totalSpace = 0;
        int maxStackSize = ext.getMaxStackSize();

        for (ItemStack slotStack : contents) {
            if (slotStack == null || slotStack.getType() == Material.AIR) {
                totalSpace += maxStackSize;
            }
            else if (comparer.isSame(slotStack, itemStack)) {

                if (slotStack.getAmount() <= maxStackSize)
                    totalSpace += (maxStackSize - slotStack.getAmount());
            }

            if (totalRequired > 0 && totalSpace >= totalRequired)
                return totalRequired;
        }

        return totalSpace;
    }

    private static int count (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {

        int count = 0;

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (comparer.isSame(itemStack, item))
                count += item.getAmount();

            if (qty >= 0 && count >= qty)
                return count;
        }

        return count;
    }

    private static int count (Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {

        ItemStack[] contents = inventory.getContents();

        int count = count(contents, itemStack, comparer, qty);

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            ArmorType type = ArmorType.getType(itemStack);

            if (type == ArmorType.HELMET && comparer.isSame(playerInventory.getHelmet(), itemStack))
                count++;

            if (type == ArmorType.CHESTPLATE && comparer.isSame(playerInventory.getChestplate(), itemStack))
                count++;

            if (type == ArmorType.LEGGINGS && comparer.isSame(playerInventory.getLeggings(), itemStack))
                count++;

            if (type == ArmorType.BOOTS && comparer.isSame(playerInventory.getBoots(), itemStack))
                count++;
        }

        return count;
    }
}
