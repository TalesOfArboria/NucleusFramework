package com.jcwhatever.bukkit.generic.items;

import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.utils.PreCon;


public class ItemWrapper {
    
    private ItemStack _itemStack;
    private MaterialExt _materialExt;
    private ItemStackComparer _comparer;
    private int _hash = -1;

    public ItemWrapper(ItemStack itemStack) {
        PreCon.notNull(itemStack);
        
        _itemStack = itemStack;
        _comparer = ItemStackComparer.getDefault();
    }
    
    public ItemWrapper(ItemStack itemStack, ItemStackComparer comparer) {
        PreCon.notNull(itemStack);
        PreCon.notNull(comparer);
        
        _itemStack = itemStack;
        _comparer = comparer;
    }
    
    public ItemStack getItem() {
        return _itemStack;
    }

    public MaterialExt getMaterialExt() {
        if (_materialExt == null) {
            _materialExt = MaterialExt.from(_itemStack.getType());
        }
        
        return _materialExt;
    }
        
    public byte getCompareOperations() {
        return _comparer.getCompareOperations();
    }
    
    public ItemStackComparer getItemStackComparer() {
        return _comparer;
    }

    @Override
    public int hashCode() {
        if (_hash == -1) {
            _hash = 1;
            _hash = _hash * 31 + _itemStack.getTypeId();
            /*
            if (compareType)
                hash = hash * 31 + itemStack.getTypeId();

            if (compareAmount)
                hash = hash * 31 + itemStack.getAmount();

            if (compareDurability)
                hash = hash * 31 + (itemStack.getDurability() & 0xffff);

            if (compareMeta)
                hash = hash * 31 + (itemStack.hasItemMeta() ? itemStack.getItemMeta().hashCode() : 0);
            */
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
