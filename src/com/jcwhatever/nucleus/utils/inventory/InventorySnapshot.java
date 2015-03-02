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
import com.jcwhatever.nucleus.utils.items.MatchableItem;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Takes a snapshot of an {@link org.bukkit.inventory.Inventory}.
 */
public class InventorySnapshot {

    private final Map<MatchableItem, Item> _itemMap = new HashMap<MatchableItem, Item>(6 * 9);
    private final ItemStack[] _items;
    private final ItemStack[] _snapshot;
    private final ItemStackMatcher _comparer;

    /**
     * Constructor.
     *
     * <p>Uses default {@link ItemStackMatcher}</p>
     *
     * @param inventory  The chest to snapshot.
     */
    public InventorySnapshot(Inventory inventory) {
        this(inventory, ItemStackMatcher.getDefault());
    }

    /**
     * Constructor.
     *
     * @param inventory  The chest to snapshot.
     * @param matcher    The item stack matcher  to use internally.
     */
    public InventorySnapshot(Inventory inventory, ItemStackMatcher matcher ) {
        PreCon.notNull(inventory);
        PreCon.notNull(matcher );

        ItemStack[] itemStacks = inventory.getContents();
        List<ItemStack> items = new ArrayList<ItemStack>(itemStacks.length);
        _snapshot = new ItemStack[itemStacks.length];
        _comparer = matcher ;

        for (int i=0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];

            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            ItemStack clone = itemStack.clone();
            _snapshot[i] = clone;

            Item item = _itemMap.get(new Item(clone).matchableItem);
            if (item != null) {
                item.qty += clone.getAmount();
            }
            else {
                item = new Item(clone);
                _itemMap.put(item.matchableItem, item);
            }

            items.add(clone);
        }

        _items = items.toArray(new ItemStack[items.size()]);
    }

    /**
     * Get the item stack in the specified snapshot slot.
     *
     * @param slot  The slot index.
     */
    @Nullable
    public ItemStack getSlot(int slot) {
        PreCon.positiveNumber(slot);
        PreCon.lessThan(slot, _snapshot.length);

        return _snapshot[slot];
    }

    /**
     * Get the amount of items in the snapshot that
     * match the specified item.
     *
     * @param itemStack  The item stack to check.
     */
    public int getAmount (ItemStack itemStack) {
        PreCon.notNull(itemStack);

        Item item = _itemMap.get(new Item(itemStack).matchableItem);

        if (item == null)
            return 0;

        return item.qty;
    }

    /**
     * Get the amount of items in the snapshot that
     * match the specified wrapped item.
     *
     * @param matchable  The wrapper with the item to check.
     */
    public int getAmount(MatchableItem matchable) {
        PreCon.notNull(matchable);

        Item item = _itemMap.get(matchable);

        if (item == null)
            return 0;

        return item.qty;
    }

    /**
     * Get all {@link MatchableItem}'s created from the snapshot.
     */
    public List<MatchableItem> getMatchable() {
        return new ArrayList<MatchableItem>(_itemMap.keySet());
    }

    /**
     * Get all items in the snapshot.
     *
     * <p>Does not return the snapshot, just the consolidated items
     * found in it.</p>
     */
    public ItemStack[] getItemStacks() {
        return _items.clone();
    }

    /**
     * Get the snapshot {@link org.bukkit.inventory.ItemStack} array.
     */
    public ItemStack[] getSnapshot() {
        return _snapshot.clone();
    }

    private class Item {

        final MatchableItem matchableItem;
        int qty;

        public Item(ItemStack itemStack) {
            PreCon.notNull(itemStack);

            this.matchableItem = new MatchableItem(itemStack, _comparer);
            this.qty = itemStack.getAmount();
        }
    }
}
