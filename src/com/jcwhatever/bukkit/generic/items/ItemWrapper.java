package com.jcwhatever.bukkit.generic.items;

import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.utils.PreCon;

/**
 * An {@code ItemStack} wrapper.
 *
 * <p>
 *     Provides built in {@code ItemStackComparer} support in the
 *     {@code equals} method making the wrapper ideal for use as a {@code Map} key.
 * </p>
 *
 * <p>
 *     {@code hashCode} method returns the hash of the encapsulated {@code ItemStack}'s
 *     {@code Material} type so that different {@code ItemStackComparer} compare operations
 *     can be used to find an {@code ItemStack} by key or in a hash set.
 * </p>
 *
 */
public class ItemWrapper {
    
    private ItemStack _itemStack;
    private MaterialExt _materialExt;
    private ItemStackComparer _comparer;
    private int _hash = -1;

    /**
     * Constructor.
     *
     * <p>
     *     Uses the default {@code ItemStackComparer}.
     * </p>
     *
     * @param itemStack  The item stack to encapsulate.
     */
    public ItemWrapper(ItemStack itemStack) {
        PreCon.notNull(itemStack);
        
        _itemStack = itemStack;
        _comparer = ItemStackComparer.getDefault();
    }

    /**
     * Constructor.
     *
     * @param itemStack  The item stack to encapsulate.
     * @param comparer   The comparer to use.
     */
    public ItemWrapper(ItemStack itemStack, ItemStackComparer comparer) {
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);
        
        _itemStack = itemStack;
        _comparer = comparer;
    }

    /**
     * Get the encapsulated {@code ItemStack}.
     */
    public ItemStack getItem() {
        return _itemStack;
    }

    /**
     * Get the extended material type.
     */
    public MaterialExt getMaterialExt() {
        if (_materialExt == null) {
            _materialExt = MaterialExt.from(_itemStack.getType());
        }
        
        return _materialExt;
    }

    /**
     * Get the compare operations of the {@code ItemStackComparer}.
     */
    public byte getCompareOperations() {
        return _comparer.getCompareOperations();
    }

    /**
     * Get the {@code ItemStackComparer}.
     */
    public ItemStackComparer getItemStackComparer() {
        return _comparer;
    }

    @Override
    public int hashCode() {
        if (_hash == -1) {
            _hash = 1;
            _hash = _hash * 31 + _itemStack.getTypeId();
        }

        return _hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemStack) {
            return _comparer.isSame(_itemStack, (ItemStack)o);
        }
        else if (o instanceof ItemWrapper) {
            ItemWrapper wrapper = (ItemWrapper)o;

            return _comparer.isSame(_itemStack, wrapper.getItem());
        }

        return false;
    }
}
