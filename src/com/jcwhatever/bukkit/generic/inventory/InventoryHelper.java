package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.extended.ArmorType;
import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides static methods to help with inventories of {@code ItemStack}'s.
 */
public class InventoryHelper {

    private InventoryHelper() {}
	
	/**
	 * Gets the number of items of the specified stack that 
	 * can be stored in the specified inventory.
     *
	 * @param inventory  The inventory to check.
	 * @param itemStack  The {@code ItemStack} to check.
	 * @return
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
     * @param comparer   The {@code ItemStackComparer} to use.
     */
    public static int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
        return getMax(contents, itemStack, comparer, -1);
    }
	
	/**
	 * Determine if there is enough room in the specified inventory
	 * for the specified stack.
	 * @param inventory  The inventory to check.
	 * @param itemStack  The {@code ItemStack} to check.
	 * @return
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
	 * @return
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
     * @param contents   The inventory contents.
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
     * @return
     */
	public static boolean has(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {

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
     * Determine if the specified {@code ItemStack} array contains an item stack
     * that matches the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
	public static boolean has(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
		
		for (ItemStack item : contents) {
			if (item == null || item.getType() == Material.AIR)
				continue;
			
			if (comparer.isSame(itemStack, item))
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
     * @return
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
        return count(inventory, itemStack, comparer, qty) == qty;
    }

    /**
     * Determine if the specified {@code ItemStack} array contains the specified quantity
     * of items that match the specified {@code ItemStack}.
     *
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     * @return
     */
	public static boolean has (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
        
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
     * @param contents   The inventory contents.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     */
	public static ItemStack[] getAll (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
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
     * Remove a specified quantity of {@code ItemStack}'s from the specified inventory
     * that match the specified {@code ItemStack}.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The {@code ItemStack} to check.
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> remove (Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {

        ItemStack[] contents = inventory.getContents();

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

                    break;
                }
                else {

                    qtyLeft -= item.getAmount();

                    clone.setAmount(item.getAmount());
                    results.add(clone);

                    contents[i] = ItemStackHelper.AIR;
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
     * @param comparer   The {@code ItemStackComparer} to use.
     * @param qty        The quantity.
     */
    public static List<ItemStack> remove (ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {

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
                    
                    contents[i] = ItemStackHelper.AIR;
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


        inventory.clear();
        inventory.setContents(new ItemStack[inventory.getSize()]); // 36

        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory)inventory;
            playerInventory.setHelmet(null);
            playerInventory.setChestplate(null);
            playerInventory.setLeggings(null);
            playerInventory.setBoots(null);
            playerInventory.setItemInHand(null);

            /*
            InventoryView view = _player.getOpenInventory();

            if (view == null)
                return;

            view.setCursor(null);
            Inventory i = view.getTopInventory();

            if (i == null)
                return;

            i.clear();
            */
        }
    }

    /**
     * Repair all repairable items in an inventory. If the inventory
     * is a {@code PlayerInventory}, the armor contents are also
     * repaired.
     *
     * @param inventory  The inventory to repair.
     */
    public static void repairAll(Inventory inventory) {

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

        for (ItemStack stack : contents) {
            if (stack == null || !ItemStackHelper.isRepairable(stack))
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
