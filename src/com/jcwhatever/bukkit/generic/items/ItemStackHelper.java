package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.converters.ValueConverters;
import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.items.ItemStackSerializer.SerializerOutputType;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.sun.istack.internal.Nullable;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Item stack utilities
 */
public class ItemStackHelper {

    private ItemStackHelper() {}
	
	public static final ItemStack AIR = new ItemStack(Material.AIR);

    /**
     * Used to specify if a display name for an item
     * stack is required or optional.
     */
    public enum DisplayNameResult {
        /**
         * Name is required and the items material name
         * can be used as a substitute if a display name
         * is not set.
         */
        REQUIRED,

        /**
         * Name is optional and null is expected if
         * the display name is not set.
         */
        OPTIONAL
    }
	
	/**
	 * Set the specified block material and data
	 * to the material and data represented by
	 * the ItemStack.
	 * 
	 * @param block  The block to set.
	 */
	public static void setBlock(Block block, ItemStack stack) {
        PreCon.notNull(block);
        PreCon.notNull(stack);

		if (block.getType() != stack.getType())
			block.setType(stack.getType());
		
		if (block.getData() != stack.getData().getData())
			block.setData(stack.getData().getData());
	}
	
	/**
	 * Set the specified block material and data
	 * to the material and data represented by
	 * the MaterialData.
	 * 
	 * @param block The block to set.
	 */
	public static void setBlock(Block block, MaterialData data) {
        PreCon.notNull(block);
        PreCon.notNull(data);

		BlockState state = block.getState();
		state.setType(data.getItemType());
		state.update(true);
		
		state = block.getState();
		state.setData(data.clone());
		state.update(true);
	}

	/**
	 * Set the lore on an item stack.
     *
	 * @param stack  The item stack.
	 * @param lore   The lore to set.
	 */
	public static void setLore(ItemStack stack, @Nullable List<String> lore) {
        PreCon.notNull(stack);

		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return;
		}
					
		meta.setLore(lore);
		stack.setItemMeta(meta);
	}
	
	/**
	 * Set the lore on an item stack.
     *
	 * @param stack  The item stack.
	 * @param lore   The lore to set.
	 */
	public static void setLore(ItemStack stack, @Nullable String lore) {
        PreCon.notNull(stack);

		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return;
		}
		
		List<String> list = new ArrayList<String>(5);

        if (lore != null)
		    list.add(lore);
					
		meta.setLore(list);
		stack.setItemMeta(meta);
	}
	
	/**
	 * Gets the lore from an item stack.
     *
	 * @param stack  The item stack.
	 */
    @Nullable
	public static List<String> getLore(ItemStack stack) {
        PreCon.notNull(stack);

		ItemMeta meta = stack.getItemMeta();
		if (meta == null)
			return null;
		
		return meta.getLore();
	}

	/**
	 * Determine if an item stack represents a repairable item.
     *
	 * @param stack  The item stack.
	 */
	public static boolean isRepairable(ItemStack stack) {
        PreCon.notNull(stack);

		return isRepairable(stack.getType());
	}
	
	/**
	 * Determine if a material type represents a repairable item.
     *
	 * @param type  The item stack material.
	 */
	public static boolean isRepairable(Material type) {
        PreCon.notNull(type);

		MaterialExt material = MaterialExt.from(type);
        return material != MaterialExt.UNKNOWN && material.isRepairable();
    }
	
	/**
	 * Repair an item stack
     *
	 * @param item  The item stack.
	 */
	public static void repair(ItemStack item) {
        PreCon.notNull(item);

		if (!isRepairable(item))
			return;
		
		item.setDurability((short)0);
	}
	
	/**
	 * Repair an array of items.
     *
	 * @param items The array of items to repair.
	 */
	public static void repair(ItemStack[] items) {
        PreCon.notNull(items);

		if (items.length == 0)
            return;

        for (ItemStack item : items) {
            repair(item);
        }
	}

	/**
	 * Gets the display name of an item stack. Returns empty string
	 * if the item has no display name.
     *
	 * @param stack       The item stack to get a display name from.
	 * @param nameResult  Specify how a missing display name should be returned.
	 */
    @Nullable
	public static String getDisplayName(ItemStack stack, DisplayNameResult nameResult) {
        PreCon.notNull(stack);

		ItemMeta meta = stack.getItemMeta();
		if (meta == null || !meta.hasDisplayName()) {

            switch (nameResult) {
                case REQUIRED:
                    String alternate = stack.getType().name().toLowerCase().replace("_", " ");
                    return TextUtils.titleCase(alternate);

                case OPTIONAL:
                    // fall through

                default:
                    return null;
            }
		}
		
		return meta.getDisplayName();
	}

	/**
	 * Sets the display name of an item stack.
     *
	 * @param stack        The item stack.
	 * @param displayName  The display name.
	 */
	public static void setDisplayName(ItemStack stack, @Nullable String displayName) {
        PreCon.notNull(stack);

		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(displayName);
		stack.setItemMeta(meta);
	}
		
	/**
	 * Sets 32 bit Color of an item, if possible.
     *
	 * @param item   The item stack.
	 * @param red    The red component
	 * @param green  The green component
	 * @param blue   The blue component
     *
	 * @return  True if color changed.
	 */
	public static boolean setColor(ItemStack item, int red, int green, int blue){
        PreCon.notNull(item);

		return setColor(item, Color.fromRGB(red, green, blue));
	}
	
	/**
	 * Sets RGB Color of an item, if possible.
     *
	 * @param item   The item stack.
	 * @param color  The 32 bit RGB integer color.
     *
	 * @return True if color changed.
	 */
	public static boolean setColor(ItemStack item, int color){
        PreCon.notNull(item);

		return setColor(item, Color.fromRGB(color));
	}
	
	/**
	 * Sets Color of an item, if possible.
     *
	 * @param item   The item stack.
	 * @param color  The color to set.
     *
	 * @return  True if color changed.
	 */
	public static boolean setColor(ItemStack item, Color color){
        PreCon.notNull(item);
        PreCon.notNull(color);
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta laMeta = (LeatherArmorMeta)meta;
			laMeta.setColor(color);
			item.setItemMeta(laMeta);
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the 32-bit color of an item if it has any.
     *
	 * @param item  The item stack.
     *
	 * @return Null if item does not have 32-bit color.
	 */
    @Nullable
	public static Color getColor(ItemStack item) {
        PreCon.notNull(item);

		ItemMeta meta = item.getItemMeta();
		
		if (meta instanceof LeatherArmorMeta) {
			LeatherArmorMeta laMeta = (LeatherArmorMeta)meta;
			return laMeta.getColor();
		}
		return null;
	}
	
	/**
	 * Add enchantments to an item.
     *
	 * @param stack         The item stack to add enchantments to.
     * @param enchantments  Enchantments to add
	 */
	public static void addEnchantments(ItemStack stack, Collection<EnchantmentWrapper> enchantments) {
        PreCon.notNull(stack);
        PreCon.notNull(enchantments);
		
		ItemMeta meta = stack.getItemMeta();
		
		// check for enchantment storage items such as enchanted books
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;
			
			for (EnchantmentWrapper enchant : enchantments) {
				storage.addStoredEnchant(enchant.getEnchantment(), enchant.getLevel(), true);
			}
			
			stack.setItemMeta(storage);
		}
		else {
			for (EnchantmentWrapper enchant : enchantments) {
				stack.addUnsafeEnchantment(enchant.getEnchantment(), enchant.getLevel());
			}
		}
	}

    /**
     * Add an enchantment to an item.
     *
     * @param stack    The item stack.
     * @param enchant  The {@code EnchantmentWrapper} containing enchantment info.
     */
    public static void addEnchantment(ItemStack stack, EnchantmentWrapper enchant) {
        PreCon.notNull(stack);
        PreCon.notNull(enchant);

        addEnchantment(stack, enchant.getEnchantment(), enchant.getLevel());
    }

    /**
     * Add an enchantment to an item.
     *
     * @param stack        The item stack.
     * @param enchantName  The enchantment to add.
     * @param level        The enchantment level.
     *
     * @result True if the enchantName was found and applied.
     */
    public static boolean addEnchantment(ItemStack stack, String enchantName, int level) {
        PreCon.notNull(stack);
        PreCon.notNullOrEmpty(enchantName);
        PreCon.positiveNumber(level);

        Enchantment enchantment = Enchantment.getByName(enchantName);
        if (enchantment == null)
            return false;

        addEnchantment(stack, enchantment, level);

        return true;
    }

	/**
	 * Add an enchantment to an item.
     *
	 * @param stack    The item stack.
	 * @param enchant  The enchantment to add.
	 * @param level    The enchantment level.
	 */
	public static void addEnchantment(ItemStack stack, Enchantment enchant, int level) {
        PreCon.notNull(stack);
        PreCon.notNull(enchant);
        PreCon.positiveNumber(level);
		
		ItemMeta meta = stack.getItemMeta();
		
		// check for enchantment storage items such as enchanted books
		if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;
			storage.addStoredEnchant(enchant, level, true);
			stack.setItemMeta(storage);
		}
		else {
			stack.addUnsafeEnchantment(enchant, level);
		}
	}

    /**
     * Removes an enchantment from an item and returns an
     * {@code EnchantmentWrapper} containing the enchantment
     * and enchantment level before it was removed.
     *
     * @param stack            The item stack.
     * @param enchantmentName  The name of the enchantment to remove.
     *
     * @return  Null if the enchantment name is not found or the item did not have the enchantment.
     */
    @Nullable
    public static EnchantmentWrapper removeEnchantment(ItemStack stack, String enchantmentName) {
        PreCon.notNull(stack);
        PreCon.notNullOrEmpty(enchantmentName);

        Enchantment enchantment = Enchantment.getByName(enchantmentName);
        if (enchantment == null)
            return null;

        return removeEnchantment(stack, enchantment);
    }

    /**
     * Removes an enchantment from an item and returns an
     * {@code EnchantmentWrapper} containing the enchantment
     * and enchantment level before it was removed.
     *
     * @param stack        The item stack.
     * @param enchantment  The enchantment to remove.
     *
     * @return  Null if the item did not have the enchantment
     */
    @Nullable
    public static EnchantmentWrapper removeEnchantment(ItemStack stack, Enchantment enchantment) {
        PreCon.notNull(stack);
        PreCon.notNull(enchantment);

        if (!stack.getEnchantments().containsKey(enchantment))
            return null;

        int level = stack.removeEnchantment(enchantment);

        return new EnchantmentWrapper(enchantment, level);
    }

	/**
	 * Parses item stack string to {@code ItemStack} array.
     *
	 * @param itemString  The item stack string.
     *
     * @return  Null if the string could not be parsed.
	 */
    @Nullable
	public static ItemStack[] parse(String itemString) {
		
		if (itemString == null || itemString.length() == 0)
			return new ItemStack[0];
		
		String[] items = TextUtils.PATTERN_COMMA.split(itemString);
		
		//Messenger.debug(null, "ItemStackHelper: parse: parsing " + items.length + " possible ItemStacks from string: " + itemString);
				
		Collection<ItemStack> wrappers = new ArrayList<ItemStack>(items.length);
		
		for (String item : items) {
			ItemStack stack = parseItemString(item);
			if (stack == null) {
				Messenger.debug(null, "ItemStackHelper: parse: Failed to parse item string item.");
				return null;
			}
			wrappers.add(stack);
		}
		
		//Messenger.debug(null, "ItemStackExt: parse: Successfully parsed " + items.length + " items.");		
		return wrappers.toArray(new ItemStack[wrappers.size()]);
	}
	
	/**
	 * Serialize a collection of item stacks into a string.
     *
	 * @param stacks  The item stack collection.
	 */
	public static String serializeToString(Collection<ItemStack> stacks, SerializerOutputType outputType) {
        PreCon.notNull(stacks);
        PreCon.notNull(outputType);

		return new ItemStackSerializer(stacks.size() * 20, outputType).appendAll(stacks).toString();
	}
	
	/**
	 * Serialize an item stack array into a string.
     *
	 * @param stacks  The item stacks to serialize.
	 */
	public static String serializeToString(ItemStack[] stacks, SerializerOutputType outputType) {
        PreCon.notNull(stacks);
        PreCon.notNull(outputType);

		return new ItemStackSerializer(stacks.length * 20, outputType).appendAll(stacks).toString();
	}
	
	/**
	 * Serialize an item stack into a string.
     *
	 * @param stack  The item stack.
	 */
	public static String serializeToString(ItemStack stack, SerializerOutputType outputType) {
        PreCon.notNull(stack);
        PreCon.notNull(outputType);

		return new ItemStackSerializer(40, outputType).append(stack).toString();
	}

	/*
	 * Parses a serialized item string into an item stack.
	 */
    @Nullable
	private static ItemStack parseItemString(String itemString) {
		ItemStringParser parser = new ItemStringParser(itemString);
		
		if (!parser.isValid()) {
			Messenger.debug(null,
                    "ItemStackHelper: parseItemString: Attempted to parse invalid item string: {0}", itemString);
			return null;
		}

        int qty = parser.getQuantity();
        Color color = parser.getColor();
		MaterialData data = parser.getMaterialData();
        List<String> lore = parser.getLore();
		String displayName = parser.getDisplayName();

		if (data == null)
			return null;
		
		List<EnchantmentWrapper> enchants = parser.getEnchantments();
		Material material = data.getItemType();
		
		// create stack
		ItemStack stack;
		Potion potion;
				
		// handle special cases
		if (material == Material.POTION && (potion = ValueConverters.POTION_ID.convert(parser.getData())) != null) {
			stack = potion.toItemStack(qty);
		}
		else if (material == Material.WOOL) {
			stack = new ItemStack(material, qty);
			stack.setData(data);
			stack.setDurability(parser.getData());
		}
		else {
			// set stack data
			stack = new ItemStack(material, qty);
			stack.setData(data);
			stack.setDurability(parser.getData());
		}
		
		// set enchantments
		if (enchants.size() > 0) {
			addEnchantments(stack, enchants);
		}
		
		// set display name
		if (displayName.length() != 0)
			setDisplayName(stack, displayName);
		
		// set display
		if (color != null) {
			setColor(stack, color);
		}
		
		//set lore
		if (lore != null) {
			setLore(stack, lore);
		}
		
		return stack;
	}
}
