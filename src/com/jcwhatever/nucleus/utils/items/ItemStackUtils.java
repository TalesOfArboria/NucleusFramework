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


package com.jcwhatever.nucleus.utils.items;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.items.serializer.IItemStackDeserializer;
import com.jcwhatever.nucleus.managed.items.serializer.IItemStackSerializer;
import com.jcwhatever.nucleus.managed.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.utils.materials.Materials;
import com.jcwhatever.nucleus.utils.materials.NamedMaterialData;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * {@link ItemStack} utilities.
 */
public final class ItemStackUtils {

    private static final Pattern PATTERN_REPLACE_UNDERSCORE = Pattern.compile("_", Pattern.LITERAL);

    private ItemStackUtils() {}

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    /**
     * Used to specify if a display name for an {@link ItemStack} is required or
     * optional.
     */
    public enum DisplayNameOption {

        /**
         * Name is required and the items material name can be used as a substitute
         * if a display name is not set.
         */
        REQUIRED,
        /**
         * Name is optional and null is expected if the display name is not set.
         */
        OPTIONAL
    }

    /**
     * Convert an object to an {@link ItemStack} if possible.
     *
     * <p>The following are valid arguments that can be converted:</p>
     * <ul>
     *     <li>{@link ItemStack}</li>
     *     <li>{@link Material}</li>
     *     <li>{@link MaterialData}</li>
     *     <li>The name or alternate name of a material. Valid names
     *     are from {@link NamedMaterialData}.</li>
     *     <li>A item stack string serialized by an {@link IItemStackSerializer}.</li>
     * </ul>
     *
     * @param object  The object to retrieve an {@link ItemStack} from.
     *
     * @return  An {@link ItemStack} or null if failed.
     */
    public static ItemStack getItemStack(Object object) {

        ItemStack result = null;

        if (object instanceof ItemStack) {
            result = (ItemStack) object;
        }
        else if (object instanceof String) {

            String str = (String)object;

            MaterialData data = NamedMaterialData.get(str);
            if (data != null) {
                result = data.toItemStack();
            }
            else {
                try {
                    ItemStack[] itemStacks = parse(str);

                    if (itemStacks != null && itemStacks.length > 0)
                        result = itemStacks[0];
                } catch (InvalidItemStackStringException ignore) {}
            }
        }
        else if (object instanceof Material) {
            result = new ItemStack((Material)object);
        }
        else if (object instanceof MaterialData) {
            result = ((MaterialData)object).toItemStack();
        }

        if (result != null && result.getAmount() <= 0)  {
            result.setAmount(1);
        }

        return result;
    }

    /**
     * Set the lore on an {@link ItemStack}.
     *
     * @param stack  The {@link ItemStack}.
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
     * Set the lore on an {@link ItemStack}.
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
     * Gets the lore from an {@link ItemStack}.
     *
     * @param stack  The {@link ItemStack}.
     */
    public static List<String> getLore(ItemStack stack) {
        PreCon.notNull(stack);

        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return CollectionUtils.unmodifiableList();

        List<String> result = meta.getLore();
        if (result == null)
            return CollectionUtils.unmodifiableList();

        return CollectionUtils.unmodifiableList(result);
    }

    /**
     * Repair an item stack
     *
     * @param item  The item stack.
     */
    public static void repair(ItemStack item) {
        PreCon.notNull(item);

        if (!Materials.isRepairable(item.getType()))
            return;

        item.setDurability((short)0);
    }

    /**
     * Repair an array of {@link ItemStack}.
     *
     * @param items The array of {@link ItemStack} to repair.
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
     * Gets the display name of an {@link ItemStack}. Returns empty string if
     * the item has no display name.
     *
     * @param stack       The {@link ItemStack} to get a display name from.
     * @param nameResult  Specify how a missing display name should be returned.
     */
    @Nullable
    public static String getDisplayName(ItemStack stack, DisplayNameOption nameResult) {
        PreCon.notNull(stack);

        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {

            switch (nameResult) {
                case REQUIRED:
                    String materialName = NamedMaterialData.get(stack.getData()).toLowerCase();
                    String spaced = PATTERN_REPLACE_UNDERSCORE.matcher(materialName).replaceAll(" ");
                    return TextUtils.titleCase(spaced);

                case OPTIONAL:
                    // fall through

                default:
                    return null;
            }
        }

        return meta.getDisplayName();
    }

    /**
     * Sets the display name of an {@link ItemStack}.
     *
     * @param stack        The {@link ItemStack}.
     * @param displayName  The display name.
     */
    public static void setDisplayName(ItemStack stack, @Nullable String displayName) {
        PreCon.notNull(stack);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        stack.setItemMeta(meta);
    }

    /**
     * Sets 32 bit color of an {@link ItemStack}, if possible.
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
     * Sets RGB Color of an {@link ItemStack}, if possible.
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
     * Sets RGB color of an {@link ItemStack}, if possible.
     *
     * @param item   The {@link ItemStack}.
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
     * Gets the 32-bit color of an {@link ItemStack} if it has any.
     *
     * @param item  The {@link ItemStack}.
     *
     * @return The {@link Color} or null if item does not have 32-bit color.
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
     * Add enchantments to an {@link ItemStack}.
     *
     * @param stack         The {@link ItemStack} to add enchantments to.
     * @param enchantments  Enchantments to add
     */
    public static void addEnchantments(ItemStack stack, Collection<EnchantmentLevel> enchantments) {
        PreCon.notNull(stack);
        PreCon.notNull(enchantments);

        ItemMeta meta = stack.getItemMeta();

        // check for enchantment storage items such as enchanted books
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storage = (EnchantmentStorageMeta)meta;

            for (EnchantmentLevel enchant : enchantments) {
                storage.addStoredEnchant(enchant.getEnchantment(), enchant.getLevel(), true);
            }

            stack.setItemMeta(storage);
        }
        else {
            for (EnchantmentLevel enchant : enchantments) {
                stack.addUnsafeEnchantment(enchant.getEnchantment(), enchant.getLevel());
            }
        }
    }

    /**
     * Add an enchantment to an {@link ItemStack}.
     *
     * @param stack    The {@link ItemStack}.
     * @param enchant  The {@link EnchantmentLevel} containing enchantment info.
     */
    public static void addEnchantment(ItemStack stack, EnchantmentLevel enchant) {
        PreCon.notNull(stack);
        PreCon.notNull(enchant);

        addEnchantment(stack, enchant.getEnchantment(), enchant.getLevel());
    }

    /**
     * Add an enchantment to an {@link ItemStack}.
     *
     * @param stack        The {@link ItemStack}.
     * @param enchantName  The enchantment to add.
     * @param level        The enchantment level.
     *
     * @return  True if the enchantName was found and applied, otherwise false.
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
     * Add an enchantment to an {@link ItemStack}.
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
     * Removes an enchantment from an {@link ItemStack} and returns an
     * {@link EnchantmentLevel} containing the enchantment and enchantment
     * level before it was removed.
     *
     * @param stack            The item stack.
     * @param enchantmentName  The name of the enchantment to remove.
     *
     * @return  Null if the enchantment name is not found or the item did not have
     * the enchantment.
     */
    @Nullable
    public static EnchantmentLevel removeEnchantment(ItemStack stack, String enchantmentName) {
        PreCon.notNull(stack);
        PreCon.notNullOrEmpty(enchantmentName);

        Enchantment enchantment = Enchantment.getByName(enchantmentName);
        if (enchantment == null)
            return null;

        return removeEnchantment(stack, enchantment);
    }

    /**
     * Removes an enchantment from an {@link ItemStack} and returns an
     * {@link EnchantmentLevel} containing the enchantment and enchantment
     * level before it was removed.
     *
     * @param stack        The item stack.
     * @param enchantment  The enchantment to remove.
     *
     * @return  Null if the item did not have the enchantment
     */
    @Nullable
    public static EnchantmentLevel removeEnchantment(ItemStack stack, Enchantment enchantment) {
        PreCon.notNull(stack);
        PreCon.notNull(enchantment);

        if (!stack.getEnchantments().containsKey(enchantment))
            return null;

        int level = stack.removeEnchantment(enchantment);

        return new EnchantmentLevel(level, enchantment);
    }

    /**
     * Parses serialized item string to {@link ItemStack} array.
     *
     * @param itemString  The {@link ItemStack} string to parse.
     *
     * @throws InvalidItemStackStringException
     *
     * @return  Null if the string could not be parsed.
     *
     * @see IItemStackSerializer
     * @see IItemStackDeserializer
     */
    @Nullable
    public static ItemStack[] parse(String itemString) throws InvalidItemStackStringException {

        if (itemString == null || itemString.length() == 0)
            return new ItemStack[0];

        IItemStackDeserializer parser;

        parser = Nucleus.getItemSerialization().parse(itemString);

        return parser.getArray();
    }

    /**
     * Serialize a collection of {@link ItemStack} into a string.
     *
     * @param stacks  The {@link ItemStack} collection.
     */
    public static String serialize(Collection<ItemStack> stacks) {
        PreCon.notNull(stacks);

        return Nucleus.getItemSerialization()
                .createSerializer(stacks.size()).appendAll(stacks).toString();
    }

    /**
     * Serialize an {@link ItemStack} array into a string.
     *
     * @param stacks  The {@link ItemStack}'s to serialize.
     */
    public static String serialize(ItemStack... stacks) {
        PreCon.notNull(stacks);

        return Nucleus.getItemSerialization()
                .createSerializer(stacks.length).appendAll(stacks).toString();
    }
}
