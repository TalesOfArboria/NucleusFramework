package com.jcwhatever.bukkit.generic.items.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.items.ItemWrapper;
import com.jcwhatever.bukkit.generic.storage.IDataNode;

/**
 * Represents a single item type in a players item bank account.
 */
public class BankItem {
    
    private UUID _itemId;
    private ItemBankAccount _account;
    private ItemStack _itemStack;
    private int _qty;
    private IDataNode _dataNode;
    private ItemWrapper _wrapper;

    /**
     * Constructor.
     *
     * @param itemId     The id of the item.
     * @param account    The account the item is for.
     * @param itemStack  The item stack that represents the stored items.
     * @param qty        The number of items stored.
     * @param dataNode   The data storage node to save settings to.
     */
    BankItem(UUID itemId, ItemBankAccount account, ItemStack itemStack, int qty, IDataNode dataNode) {
        PreCon.notNull(itemId);
        PreCon.notNull(account);
        PreCon.notNull(itemStack);
        PreCon.greaterThanZero(qty);
        PreCon.notNull(dataNode);

        _itemId = itemId;
        _account = account;
        _itemStack = itemStack;
        _qty = qty;
        _dataNode = dataNode;
        
        _itemStack.setAmount(1);
        _wrapper = new ItemWrapper(_itemStack, ItemStackComparer.getDurability());
    }

    /**
     * Get the id of the bank item.
     */
    public UUID getItemId() {
        return _itemId;
    }

    /**
     * Get a singleton instance of an ItemWrapper for the item.
     */
    public ItemWrapper getItemWrapper() {
        return _wrapper;
    }

    /**
     * Get the item type.
     */
    public Material getType() {
        return _itemStack.getType();
    }

    /**
     * Get the quantity held.
     */
    public int getQty() {
        return _qty;
    }

    /**
     * Get the maximum stack size of the item.
     */
    public int getMaxStackSize () {
        return _itemStack.getMaxStackSize();
    }

    /**
     * Withdraw a quantity of the items.
     *
     * @param qty  The quantity to withdraw.
     *
     * @return  An {@code ItemStack} array of withdrawn items.
     *
     * @throws InsufficientItemsException
     */
    public ItemStack[] withdraw(int qty) throws InsufficientItemsException {
        return _account.withdraw(this, qty);
    }

    /**
     * Get an {@code ItemStack} array of the items without withdrawing them.
     */
    public ItemStack[] getItems() {
        try {
            return getItems(_qty);
        }
        catch (InsufficientItemsException e) {
            // should not be possible to reach this point
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    /**
     * Get an {@code ItemStack} array of the items without withdrawing them.
     *
     * @param qty  The quantity to get

     * @throws InsufficientItemsException if quantity is greater than available quantity.
     */
    public ItemStack[] getItems(int qty) throws InsufficientItemsException {
        
        if (qty > _qty)
            throw new InsufficientItemsException(_account.getPlayerId(), _itemStack.clone(), _qty);
        
        int maxStackSize = _itemStack.getMaxStackSize();
        int totalStacks = (int)Math.ceil((double)qty / maxStackSize);
        int qtyLeft = qty;
        
        ItemStack[] result = new ItemStack[totalStacks];
        
        for (int i = 0; i < totalStacks; i++) {
            ItemStack itemStack = _itemStack.clone();
            
            if (qtyLeft > maxStackSize) {
                itemStack.setAmount(maxStackSize);
                qtyLeft -= maxStackSize;
            }
            else {
                itemStack.setAmount(qtyLeft);
                qtyLeft = 0;
            }
            
            result[i] = itemStack;
        }
        
        return result;
    }

    /**
     * Get a list of {@code BankItemStack} where each represents
     * a stack to be placed in an inventory slot.
     */
    public List<BankItemStack> getBankItems() {
        
        int maxStackSize = _itemStack.getMaxStackSize();
        int totalStacks = (int)Math.ceil((double)_qty / maxStackSize);
        int qtyLeft = _qty;
        
        List<BankItemStack> result = new ArrayList<BankItemStack>(totalStacks);
        
        for (int i = 0; i < totalStacks; i++) {
            
            int stackqty;
                        
            if (qtyLeft > maxStackSize) {
                stackqty = maxStackSize;
                qtyLeft -= maxStackSize;
            }
            else {
                stackqty = qtyLeft;
                qtyLeft = 0;
            }
            
            if (stackqty > 0)
                result.add(new BankItemStack(stackqty));
        }
        
        return result;
    }
    
    IDataNode getDataNode() {
        return _dataNode;
    }
    
    void increment(int amount) {
        _qty += amount;
        
        if (_dataNode != null) {
            _dataNode.set("qty", _qty);
            _dataNode.saveAsync(null);
        }
    }

    /**
     * Represents a bank item in a quantity up to
     * the items maximum stack size.
     */
    public class BankItemStack {
        
        private int _stackQty;
        
        BankItemStack(int qty) {
            _stackQty = qty;
        }
        
        /**
         * Returns clone of the item stack used to represent the 
         * bank item.
         */
        public ItemStack getItemStack() {
            ItemStack item = _itemStack.clone();
            item.setAmount(_stackQty);
            return item;
        }

        /**
         * Get the quantity of the stack.
         */
        public int getQty() {
            return _stackQty;                        
        }

        /**
         * Get the total quantity available from the account.
         */
        public int getTotalAvailable() {
            return _qty;
        }
    }

}
