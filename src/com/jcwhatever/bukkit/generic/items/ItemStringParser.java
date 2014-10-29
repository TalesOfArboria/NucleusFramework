/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.converters.ValueConverters;
import com.jcwhatever.bukkit.generic.messaging.Messenger;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parses a string representing a single {@code ItemStacks} serialized
 * by {@code ItemStackSerializer}.
 */
public class ItemStringParser {

    private static final Pattern PATTER_LORE_SPLITTER = Pattern.compile("\\|");

    private static final int MATERIAL = 0;
    private static final int DATA = 1;
    private static final int QUANTITY = 1;
    private static final int ENCHANTMENTS = 2;
    private static final int ENCHANTMENT_NAME = 0;
    private static final int ENCHANTMENT_LEVEL = 1;
    private static final int DISPLAY_NAME = 3;
    private static final int COLOR = 4;
    private static final int LORE = 5;

    private final String _itemString;
    private MaterialData _materialData;
    private short _data;
    private int _qty;
    private String _displayName;
    private Color _color;
    private List<EnchantmentWrapper> _enchantments;
    private List<String> _lore;

    private boolean _isValid;

    /**
     * Constructor.
     *
     * @param itemString  The string to parse. The string must represent a single {@code ItemStack}
     */
    public ItemStringParser(String itemString) {
        PreCon.notNull(itemString);

        _itemString = itemString;
        _enchantments = new ArrayList<EnchantmentWrapper>(10);
        _isValid = parse(itemString);
    }

    /**
     * Determine if the string is a valid {@code ItemStack} string.
     */
    public boolean isValid() {
        return _isValid;
    }

    /**
     * Get the {@code ItemStack} string.
     */
    public String getItemString() {
        return _itemString;
    }

    /**
     * Get the material data parsed from the string.
     *
     * @return  Null if the {@code ItemStack} string is not valid.
     */
    @Nullable
    public MaterialData getMaterialData() {
        return _materialData;
    }

    /**
     * Get the item stack raw data parsed from the string.
     */
    public short getData() {
        return _data;
    }

    /**
     * Get the item stack quantity/amount parsed from the string.
     */
    public int getQuantity() {
        return _qty;
    }

    /**
     * Get the the 32-bit color of the item parsed from the string.
     *
     * @return  Null if the string did not contain 32-bit color info.
     */
    @Nullable
    public Color getColor() {
        return _color;
    }

    /**
     * Get the item lore parsed from the string.
     *
     * @return  Null if the string did not contain lore.
     */
    @Nullable
    public List<String> getLore() {
        return _lore;
    }

    /**
     * Get the item stack display name parsed from the string.
     *
     * @return  Null if the string did not contain display name info.
     */
    @Nullable
    public String getDisplayName() {
        return _displayName;
    }

    /**
     * Get enchantments parsed from the string.
     */
    public List<EnchantmentWrapper> getEnchantments() {
        return _enchantments;
    }

    /**
     * Returns the raw {@code ItemString} string.
     */
    @Override
    public String toString() {
        return _itemString;
    }


    /*
     * Parses a string representation of an ItemStackWrapper instance
     * into the ItemComponents instance.
     */
    private boolean parse(String itemString) {

        String[] primary = parsePrimaryComponents(itemString);
        if (primary == null)
            return false;

        if (primary[MATERIAL].equals("null")) {
            primary = new String[] { "AIR", "-1", "", "", "", ""};
        }

        String[] material = parseMaterialComponents(primary[MATERIAL]);
        if (material == null)
            return false;

        // material or item id
        MaterialData m = ValueConverters.ITEM_NAME_MATERIALDATA.unconvert(material[MATERIAL]);
        if (m == null)
            return false;

        _materialData = m;
        _displayName = primary[DISPLAY_NAME];

        // parse data
        try {
            _data = Short.parseShort(material[DATA]);
        }
        catch (NumberFormatException nfe) {
            Messenger.debug(null, "ItemStringParser: parse: Failed to parse data: " + primary[DATA]);
            nfe.printStackTrace();
            return false;
        }

        if (_data != 0 && _materialData.getData() == 0)
            _materialData.setData((byte)_data);
        else
            _data = _materialData.getData();


        // parse quantity
        try {
            _qty = Integer.parseInt(primary[QUANTITY]);
        }
        catch (NumberFormatException nfe) {
            Messenger.debug(null, "ItemStringParser: parse: Failed to parse quantity: " + primary[QUANTITY]);
            nfe.printStackTrace();
            return false;
        }

        // parse enchantments
        List<String> enchantmentComponents = parseEnchantmentComponents(primary[ENCHANTMENTS]);

        if (enchantmentComponents != null && enchantmentComponents.size() > 0) {
            for (String enchantComp : enchantmentComponents) {
                EnchantmentWrapper enchantment = parseEnchantment(enchantComp);
                if (enchantment == null) {
                    Messenger.debug(null, "ItemStringParser: parse: Failed to parse enchantment: " + enchantComp);
                    return false;
                }

                _enchantments.add(enchantment);
            }
        }


        //parse color
        String rawColor = primary[COLOR];
        if (!rawColor.isEmpty()) {
            int intColor;
            try {
                intColor = Integer.parseInt(rawColor, 16);
            }
            catch (NumberFormatException nfe) {
                Messenger.debug(null, "ItemStringParser: parse: Failed to parse color: " + rawColor);
                nfe.printStackTrace();
                return false;
            }
            _color = Color.fromRGB(intColor);
        }


        //parse lore
        String rawLore = primary[LORE];
        if (!rawLore.isEmpty()) {
            _lore = parseLoreComponents(rawLore);
        }

        return true;
    }



    /**
     * Takes a string representation of an ItemStack instance
     * and breaks it down into its primary components and returns them
     * in a String array with exactly 6 items.
     *
     * [0] = item name and possibly data
     * [1] = quantity
     * [2] = enchantments
     * [3] = item display name
     * [4] = color
     * [5] = lore
     *
     * All items in the array are guaranteed to have a value
     *
     * @return  Returns null if invalid string provided
     */
    @Nullable
    private static String[] parsePrimaryComponents(String itemStackString) {

        if (itemStackString == null || itemStackString.trim().length() == 0)
            return null;

        String[] components = new String[6];

        String[] rawComponents = TextUtils.PATTERN_SEMI_COLON.split(itemStackString);

        String material = rawComponents[MATERIAL].trim();

        // Make sure there are no more than 6 components and that the
        // first component is not empty
        if (rawComponents.length > 6 || material.length() == 0)
            return null;

        components[MATERIAL] = material;

        if (rawComponents.length > 1) {
            components[QUANTITY] = rawComponents[QUANTITY].trim();
            if (components[QUANTITY].isEmpty())
                components[QUANTITY] = "1";
        } else
            components[QUANTITY] = "1";

        components[ENCHANTMENTS] = rawComponents.length > 2
                ? rawComponents[ENCHANTMENTS].trim()
                : "";

        components[DISPLAY_NAME] = rawComponents.length > 3
                ? rawComponents[DISPLAY_NAME].trim()
                : "";

        components[COLOR] = rawComponents.length > 4
                ? rawComponents[COLOR].trim()
                : "";

        components[LORE] = rawComponents.length == 6
                ? rawComponents[LORE].trim()
                : "";

        return components;
    }


    /**
     * Parses lore into a String[] based on '|' delimeter.
     */
    @Nullable
    private static List<String> parseLoreComponents(String lore) {

        if (lore == null)
            return null;

        if (lore.isEmpty())
            return null;

        String[] loreLines = PATTER_LORE_SPLITTER.split(lore);

        List<String> loreResults = new ArrayList<String>(loreLines.length);

        for (String loreLine : loreLines) {
            loreResults.add(ChatColor.translateAlternateColorCodes('&', loreLine));
        }

        return loreResults;
    }


    /**
     * Parses the material portion of a string that represents
     * an ItemStackWrapper instance. The material portion is
     * contained in the first primary component of the original string.
     *
     * Returns a String array with exactly 2 items.
     * [0] = Material name or item id
     * [1] = Data
     *
     * All items in the array are guaranteed to have a value.
     */
    @Nullable
    private static String[] parseMaterialComponents(String material) {

        if (material == null || material.trim().length() == 0)
            return null;

        String[] components = new String[2];

        String[] rawComponents = TextUtils.PATTERN_COLON.split(material);

        if (rawComponents.length > 2 || rawComponents[0].trim().length() == 0)
            return null;

        components[MATERIAL] = rawComponents[MATERIAL];

        components[DATA] = rawComponents.length == 1
                ? "0"
                : rawComponents[DATA];

        return components;
    }


    /**
     * Parses the enchantment components from a string. The enchantment string
     * is located in the 3rd primary components of the item string.
     */
    @Nullable
    private static List<String> parseEnchantmentComponents(String enchantments) {
        if (enchantments == null) {
            return null;
        }

        if (enchantments.trim().length() == 0)
            return new ArrayList<String>(0);

        String[] rawComponents = TextUtils.PATTERN_COLON.split(enchantments);

        if (rawComponents.length == 1 && rawComponents[0].trim().length() == 0)
            return new ArrayList<String>(0);

        List<String> components = new ArrayList<String>(rawComponents.length);

        for (String rawComponent : rawComponents) {
            String enchantment = rawComponent.trim();

            if (enchantment.length() == 0)
                continue;

            components.add(enchantment);
        }

        return components;
    }


    /**
     * Parse an individual enchantment string component into an
     * Enchantment instance.
     */
    @Nullable
    private static EnchantmentWrapper parseEnchantment(String enchantment) {

        String[] rawComponents = TextUtils.PATTERN_DASH.split(enchantment);

        if (rawComponents.length > 2) {
            Messenger.debug(null, "ItemStringParser: parseEnchantment: Too many components. " + rawComponents.length);
            return null;
        }

        // handle enchantment name or id
        String enchantStr = rawComponents[ENCHANTMENT_NAME].trim();

        if (enchantStr.length() == 0) {
            Messenger.debug(null, "ItemStringParser: parseEnchantment: No Enchantment specified.");
            return null;
        }

        enchantStr = enchantStr.toUpperCase();

        // handle enchantment level
        String levelStr = rawComponents.length == 2 ? rawComponents[ENCHANTMENT_LEVEL] : "";

        int level = 1;

        if (levelStr.length() != 0) {
            try {
                level = Integer.parseInt(levelStr);
            }
            catch(NumberFormatException nfe) {
                Messenger.debug(null, "ItemStringParser: parseEnchantment: Invalid level component: " + levelStr);
                return null;
            }
        }

        // get enchantment
        Enchantment result = Enchantment.getByName(enchantStr);

        if (result == null) {
            // maybe an id was used

            try {
                int enchantId = Integer.parseInt(enchantStr);
                result = Enchantment.getById(enchantId);
            }
            catch (NumberFormatException nfe) {
                Messenger.debug(null, "ItemStringParser: parseEnchantment: Invalid Enchantment id: " + enchantStr);
                return null;
            }
        }

        return new EnchantmentWrapper(result, level);
    }

}
