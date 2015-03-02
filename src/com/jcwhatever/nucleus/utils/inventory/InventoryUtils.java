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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.materials.MaterialProperty;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Provides static methods to help with inventories of {@link org.bukkit.inventory.ItemStack}'s.
 */
public final class InventoryUtils {

    private InventoryUtils() {}

    /**
     * Add and merge item stacks into an {@link org.bukkit.inventory.ItemStack} array.
     *
     * <p>Merges matching item stacks if there is room.</p>
     *
     * @param array       The array to add items stacks to.
     * @param itemStacks  The item stacks to add.
     *
     * @return  A list of {@link org.bukkit.inventory.ItemStack}'s that could not be added.
     */
    public static List<ItemStack> add(ItemStack[] array, ItemStack... itemStacks) {

        List<ItemStack> toAdd = new ArrayList<>(itemStacks.length);
        Collections.addAll(toAdd, itemStacks);
        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

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

                if (!matcher.isMatch(item, newItem))
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
     * Add and merge item stacks into an {@link org.bukkit.inventory.ItemStack} collection.
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

        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

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

                if (!matcher.isMatch(item, newItem))
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
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static int getMax(Inventory inventory, ItemStack itemStack) {
        return getMax(inventory.getContents(), itemStack, ItemStackMatcher.getTypeMetaDurability(), -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static int getMax(Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher) {
        return getMax(inventory.getContents(), itemStack, matcher, -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static int getMax(ItemStack[] contents, ItemStack itemStack) {
        return getMax(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), -1);
    }

    /**
     * Gets the number of items of the specified stack that
     * can be stored in the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher) {
        return getMax(contents, itemStack, matcher, -1);
    }

    /**
     * Determine if there is enough room in the specified inventory
     * for the specified stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack) {
        return hasRoom(inventory, itemStack, itemStack.getAmount());
    }

    /**
     * Determine if there is enough room in the specified inventory
     * for the specified stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher) {
        return hasRoom(inventory, itemStack, matcher, itemStack.getAmount());
    }

    /**
     * Determine if there is enough room in the specified inventory for
     * items of the same type as the specified stack in the amount of
     * the specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The amount of space needed.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, int qty) {
        return getMax(inventory.getContents(), itemStack, ItemStackMatcher.getTypeMetaDurability(), qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified inventory for
     * items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher, int qty) {
        return getMax(inventory.getContents(), itemStack, matcher, qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified {@link ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack) {
        return getMax(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), itemStack.getAmount())
                >= itemStack.getAmount();
    }

    /**
     * Determine if there is enough room in the specified {@link org.bukkit.inventory.ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack, int qty) {
        return getMax(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), qty) >= qty;
    }

    /**
     * Determine if there is enough room in the specified {@link ItemStack} array
     * for items of the same type as the specified stack in the amount of the
     * specified quantity.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static boolean hasRoom(ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher, int qty) {
        return getMax(contents, itemStack, matcher, qty) >= qty;
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static int count (Inventory inventory, ItemStack itemStack) {
        return count(inventory, itemStack, ItemStackMatcher.getTypeMetaDurability());
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static int count (Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher) {
        return count(inventory.getContents(), itemStack, matcher, -1);
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static int count (ItemStack[] contents, ItemStack itemStack) {
        return count(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), -1);
    }

    /**
     * Count the number of items of the same type as the specified item stack
     * in the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static int count (ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher) {
        return count(contents, itemStack, matcher, -1);
    }

    /**
     * Determine if the specified inventory contains an item stack
     * that matches the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static boolean has(Inventory inventory, ItemStack itemStack) {
        return has (inventory, itemStack, ItemStackMatcher.getTypeMetaDurability());
    }

    /**
     * Determine if the specified inventory contains an item stack
     * that matches the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static boolean has(Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(inventory);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);

        ItemStack[] contents = inventory.getContents();

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (matcher.isMatch(itemStack, item))
                return true;
        }

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            Set<MaterialProperty> properties = Materials.getProperties(itemStack.getType());

            if (properties.contains(MaterialProperty.ARMOR)) {

                if (properties.contains(MaterialProperty.HELMET) &&
                        matcher.isMatch(playerInventory.getHelmet(), itemStack)) {
                    return true;
                }
                else if (properties.contains(MaterialProperty.CHESTPLATE) &&
                        matcher.isMatch(playerInventory.getChestplate(), itemStack)) {
                    return true;
                }
                else if (properties.contains(MaterialProperty.LEGGINGS) &&
                        matcher.isMatch(playerInventory.getLeggings(), itemStack)) {
                    return true;
                }
                else if (properties.contains(MaterialProperty.BOOTS) &&
                        matcher.isMatch(playerInventory.getBoots(), itemStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Determine if the specified inventory contains the specified quantity
     * of items that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean has (Inventory inventory, ItemStack itemStack, int qty) {
        return has(inventory, itemStack, ItemStackMatcher.getTypeMetaDurability(), qty);
    }

    /**
     * Determine if the specified inventory contains the specified quantity
     * of items that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static boolean has (Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher, int qty) {
        return count(inventory, itemStack, matcher, qty) >= qty;
    }

    /**
     * Determine if the specified {@link org.bukkit.inventory.ItemStack} array contains an item stack
     * that matches the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static boolean has(ItemStack[] contents, ItemStack itemStack) {
        return has(contents, itemStack, ItemStackMatcher.getTypeMetaDurability());
    }

    /**
     * Determine if the specified {@link org.bukkit.inventory.ItemStack} array contains an item stack
     * that matches the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static boolean has(ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (matcher.isMatch(itemStack, item))
                return true;
        }

        return false;
    }

    /**
     * Determine if the specified {@link org.bukkit.inventory.ItemStack} array contains the specified quantity
     * of items that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The quantity.
     */
    public static boolean has (ItemStack[] contents, ItemStack itemStack, int qty) {
        return has(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), qty);
    }

    /**
     * Determine if the specified {@link org.bukkit.inventory.ItemStack} array contains the specified quantity
     * of items that match the specified {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static boolean has (ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher, int qty) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);
        PreCon.positiveNumber(qty);

        int count = count(contents, itemStack, matcher, qty);

        return count >= qty;
    }

    /**
     * Get all {@link org.bukkit.inventory.ItemStack}'s that match the specified
     * {@link org.bukkit.inventory.ItemStack} from the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static ItemStack[] getAll (Inventory inventory, ItemStack itemStack) {
        return getAll(inventory, itemStack, ItemStackMatcher.getTypeMetaDurability());
    }

    /**
     * Get all {@link org.bukkit.inventory.ItemStack}'s that match the specified
     * {@link org.bukkit.inventory.ItemStack} from the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static ItemStack[] getAll (Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher) {
        return getAll(inventory.getContents(), itemStack, matcher);
    }

    /**
     * Get all {@link org.bukkit.inventory.ItemStack}'s that match the specified
     * {@link org.bukkit.inventory.ItemStack} from the specified {@link ItemStack} array.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     */
    public static ItemStack[] getAll (ItemStack[] contents, ItemStack itemStack) {
        return getAll(contents, itemStack, ItemStackMatcher.getTypeMetaDurability());
    }

    /**
     * Get all {@link org.bukkit.inventory.ItemStack}'s that match the specified
     * {@link org.bukkit.inventory.ItemStack} from the specified
     * {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param contents   The inventory contents to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     */
    public static ItemStack[] getAll (ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);

        List<ItemStack> items = new ArrayList<ItemStack>(contents.length);

        for (ItemStack item : contents) {

            if (item == null || item.getType() == Material.AIR)
                continue;

            if (matcher.isMatch(itemStack, item))
                items.add(item);
        }

        return items.toArray(new ItemStack[items.size()]);
    }


    /**
     * Remove specified items from the the {@link org.bukkit.inventory.ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStack... itemStacks) {

        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

        return remove(contents, matcher, itemStacks);
    }

    /**
     * Remove specified items from the the {@link org.bukkit.inventory.ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param matcher     The matcher to use.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStackMatcher matcher,
                                         ItemStack... itemStacks) {
        PreCon.notNull(contents);
        PreCon.notNull(matcher);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.length);

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(contents, item, matcher, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the {@link org.bukkit.inventory.ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, Collection<ItemStack> itemStacks) {

        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

        return remove(contents, matcher, itemStacks);
    }

    /**
     * Remove specified items from the the {@link org.bukkit.inventory.ItemStack} array.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param contents    The array to remove items from.
     * @param matcher     The matcher to use.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(ItemStack[] contents, ItemStackMatcher matcher,
                                         Collection<ItemStack> itemStacks) {
        PreCon.notNull(contents);
        PreCon.notNull(matcher);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.size());

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(contents, item, matcher, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStack... itemStacks) {

        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

        return remove(inventory, matcher, itemStacks);
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param matcher     The matcher to use.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStackMatcher matcher,
                                         ItemStack... itemStacks) {
        PreCon.notNull(inventory);
        PreCon.notNull(matcher);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.length);

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(inventory, item, matcher, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, Collection<ItemStack> itemStacks) {

        ItemStackMatcher matcher = ItemStackMatcher.getTypeMetaDurability();

        return remove(inventory, matcher, itemStacks);
    }

    /**
     * Remove specified items from the the inventory.
     *
     * <p>Removes the quantity of the stack, not all matching items.</p>
     *
     * @param inventory   The inventory to remove items from.
     * @param matcher     The matcher to use.
     * @param itemStacks  The {@link org.bukkit.inventory.ItemStack}'s to remove.
     *
     * @return  The removed items.
     */
    public static List<ItemStack> remove(Inventory inventory, ItemStackMatcher matcher,
                                         Collection<ItemStack> itemStacks) {
        PreCon.notNull(inventory);
        PreCon.notNull(matcher);
        PreCon.notNull(itemStacks);


        List<ItemStack> result = new ArrayList<>(itemStacks.size());

        for (ItemStack item : itemStacks) {
            result.addAll(removeAmount(inventory, item, matcher, item.getAmount()));
        }

        return result;
    }

    /**
     * Remove a specified quantity of {@link org.bukkit.inventory.ItemStack}'s from the specified inventory
     * that match the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(Inventory inventory,
                                               ItemStack itemStack,
                                               int qty) {

        return removeAmount(inventory, itemStack, ItemStackMatcher.getTypeMetaDurability(), qty);
    }

    /**
     * Remove a specified quantity of {@link org.bukkit.inventory.ItemStack}'s from the
     * specified inventory that match the specified {@link org.bukkit.inventory.ItemStack} array.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(Inventory inventory,
                                               ItemStack itemStack,
                                               ItemStackMatcher matcher, int qty) {
        PreCon.notNull(inventory);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);
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

            if (matcher.isMatch(itemStack, item)) {

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

            Set<MaterialProperty> properties = Materials.getProperties(itemStack.getType());

            if (properties.contains(MaterialProperty.ARMOR)) {

                if (properties.contains(MaterialProperty.HELMET) &&
                        matcher.isMatch(playerInventory.getHelmet(), itemStack)) {

                    results.add(playerInventory.getHelmet());
                    playerInventory.setHelmet(null);
                }
                else if (properties.contains(MaterialProperty.CHESTPLATE) &&
                        matcher.isMatch(playerInventory.getChestplate(), itemStack)) {

                    results.add(playerInventory.getChestplate());
                    playerInventory.setChestplate(null);
                }
                else if (properties.contains(MaterialProperty.LEGGINGS) &&
                        matcher.isMatch(playerInventory.getLeggings(), itemStack)) {

                    results.add(playerInventory.getLeggings());
                    playerInventory.setLeggings(null);
                }
                else if (properties.contains(MaterialProperty.BOOTS) &&
                        matcher.isMatch(playerInventory.getBoots(), itemStack)) {

                    results.add(playerInventory.getBoots());
                    playerInventory.setLeggings(null);
                }
            }
        }

        return results;
    }

    /**
     * Remove a specified quantity of {@link org.bukkit.inventory.ItemStack}'s from the
     * specified {@link org.bukkit.inventory.ItemStack} array that match the specified
     * {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(ItemStack[] contents,
                                               ItemStack itemStack,
                                               int qty) {

        return removeAmount(contents, itemStack, ItemStackMatcher.getTypeMetaDurability(), qty);
    }

    /**
     * Remove a specified quantity of {@link org.bukkit.inventory.ItemStack}'s from the
     * specified {@link org.bukkit.inventory.ItemStack} array that match the specified
     * {@link org.bukkit.inventory.ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} to check.
     * @param matcher    The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> removeAmount(ItemStack[] contents,
                                               ItemStack itemStack,
                                               ItemStackMatcher matcher, int qty) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);
        PreCon.positiveNumber(qty);

        List<ItemStack> results = new ArrayList<ItemStack>(contents.length);

        int qtyLeft = qty;

        for (int i=0; i < contents.length; i++) {

            if (qtyLeft <= 0)
                return results;

            ItemStack item = contents[i];

            if (item == null || item.getType() == Material.AIR)
                continue;

            if (matcher.isMatch(itemStack, item)) {

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
     * Clear an inventory. If the inventory is a {@link org.bukkit.inventory.PlayerInventory},
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
     * Clear an inventory. If the inventory is a {@link org.bukkit.inventory.PlayerInventory},
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
     * is a {@link org.bukkit.inventory.PlayerInventory}, the armor contents are also
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
     * Repair all repairable items in an {@link org.bukkit.inventory.ItemStack} array.
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
     * {@link org.bukkit.inventory.PlayerInventory}, the armor contents are included
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
     * Determine if an {@link org.bukkit.inventory.ItemStack} array is empty.
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

    private static int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher, int totalRequired) {
        PreCon.notNull(contents);
        PreCon.notNull(itemStack);

        int totalSpace = 0;
        int maxStackSize = itemStack.getType().getMaxStackSize();

        if (maxStackSize == 0)
            return 0;

        for (ItemStack slotStack : contents) {
            if (slotStack == null || slotStack.getType() == Material.AIR) {
                totalSpace += maxStackSize;
            }
            else if (matcher.isMatch(slotStack, itemStack)) {

                if (slotStack.getAmount() <= maxStackSize)
                    totalSpace += (maxStackSize - slotStack.getAmount());
            }

            if (totalRequired > 0 && totalSpace >= totalRequired)
                return totalRequired;
        }

        return totalSpace;
    }

    private static int count (ItemStack[] contents, ItemStack itemStack, ItemStackMatcher matcher, int qty) {

        int count = 0;

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            if (matcher.isMatch(itemStack, item))
                count += item.getAmount();

            if (qty >= 0 && count >= qty)
                return count;
        }

        return count;
    }

    private static int count (Inventory inventory, ItemStack itemStack, ItemStackMatcher matcher, int qty) {

        ItemStack[] contents = inventory.getContents();

        int count = count(contents, itemStack, matcher, qty);

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;

            Set<MaterialProperty> properties = Materials.getProperties(itemStack.getType());

            if (properties.contains(MaterialProperty.ARMOR)) {

                if (properties.contains(MaterialProperty.HELMET) &&
                        matcher.isMatch(playerInventory.getHelmet(), itemStack)) {
                    count++;
                }
                else if (properties.contains(MaterialProperty.CHESTPLATE) &&
                        matcher.isMatch(playerInventory.getChestplate(), itemStack)) {
                    count++;
                }
                else if (properties.contains(MaterialProperty.LEGGINGS) &&
                        matcher.isMatch(playerInventory.getLeggings(), itemStack)) {
                    count++;
                }
                else if (properties.contains(MaterialProperty.BOOTS) &&
                        matcher.isMatch(playerInventory.getBoots(), itemStack)) {
                    count++;
                }
            }
        }

        return count;
    }
}
