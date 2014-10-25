package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.items.ItemWrapper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes a snapshot of an inventory.
 */
public class InventorySnapshot {
    
    private Map<ItemWrapper, Item> _itemMap = new HashMap<ItemWrapper, Item>(6 * 9);
    private ItemStack[] _items;
    private ItemStack[] _snapshot;
    private ItemStackComparer _comparer;

    /**
     * Constructor.
     *
     * <p>
     *     Uses default {@code ItemStackComparer}
     * </p>
     *
     * @param inventory  The inventory to snapshot.
     */
    public InventorySnapshot(Inventory inventory) {
        PreCon.notNull(inventory);
        
        init(inventory, ItemStackComparer.getDefault());
    }

    /**
     * Constructor.
     *
     * @param inventory  The inventory to snapshot.
     * @param comparer   The item stack comparer to use internally.
     */
    public InventorySnapshot(Inventory inventory, ItemStackComparer comparer) {
        PreCon.notNull(inventory);
        PreCon.notNull(comparer);
        
        init(inventory, comparer);
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

        Item item = _itemMap.get(new Item(itemStack).itemWrapper);

        if (item == null)
            return 0;

        return item.qty;
    }

    /**
     * Get the amount of items in the snapshot that
     * match the specified wrapped item.
     *
     * @param wrapper  The wrapper with the item to check.
     */
    public int getAmount (ItemWrapper wrapper) {
        PreCon.notNull(wrapper);

        Item item = _itemMap.get(wrapper);

        if (item == null)
            return 0;

        return item.qty;
    }

    /**
     * Get all {@code ItemWrapper}'s created from the snapshot.
     */
    public List<ItemWrapper> getWrappers () {
        return new ArrayList<ItemWrapper>(_itemMap.keySet());
    }

    /**
     * Get all items in the snapshot.
     *
     * <p>Does not return the snapshot, just the consolidated items
     * found in it.</p>
     */
    public ItemStack[] getItemStacks () {
        return _items.clone();
    }

    // init, take snapshot
    private void init(Inventory inventory, ItemStackComparer comparer) {

        ItemStack[] itemStacks = inventory.getContents();
        List<ItemStack> items = new ArrayList<ItemStack>(itemStacks.length);
        _snapshot = new ItemStack[itemStacks.length];
        _comparer = comparer;

        for (int i=0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];

            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            ItemStack clone = itemStack.clone();
            _snapshot[i] = clone;

            Item item = _itemMap.get(new Item(clone).itemWrapper);
            if (item != null) {
                item.qty += clone.getAmount();
            }
            else {
                item = new Item(clone);
                _itemMap.put(item.itemWrapper, item);
            }

            items.add(clone);
        }

        _items = items.toArray(new ItemStack[items.size()]);
    }

    private class Item {

        final ItemWrapper itemWrapper;
        int qty;

        public Item(ItemStack itemStack) {
            PreCon.notNull(itemStack);

            this.itemWrapper = new ItemWrapper(itemStack, _comparer);
            this.qty = itemStack.getAmount();
        }
    }
}
