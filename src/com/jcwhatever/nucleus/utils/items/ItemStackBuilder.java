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

import com.jcwhatever.nucleus.extended.NamedMaterialData;
import com.jcwhatever.nucleus.utils.PreCon;
import com.sun.istack.internal.Nullable;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@code ItemStack} builder.
 */
public class ItemStackBuilder {

    private MaterialData _materialData;
    private int _amount = 1;
    private Short _durability;
    private String _display;
    private List<String> _lore;
    private List<EnchantmentWrapper> _enchantments;
    private Color _color;

    /**
     * Constructor.
     *
     * @param material  The item {@code Material}.
     */
    public ItemStackBuilder(Material material) {
        PreCon.notNull(material);

        _materialData = new MaterialData(material);
    }

    /**
     * Constructor.
     *
     * @param materialData  The item {@code MaterialData}.
     */
    public ItemStackBuilder(MaterialData materialData) {
        PreCon.notNull(materialData);

        _materialData = materialData.clone();
    }

    /**
     * Constructor.
     *
     * @param itemName  The name of the material.
     */
    public ItemStackBuilder(String itemName) {
        PreCon.notNullOrEmpty(itemName);

        _materialData = NamedMaterialData.get(itemName);
        if (_materialData == null)
            throw new RuntimeException(itemName + " is not a recognized material.");
    }

    /**
     * Set the meta magic value.
     *
     * @param meta  The meta magic value.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder meta(int meta) {
        _materialData.setData((byte) meta);

        return this;
    }

    /**
     * Set the display name.
     *
     * @param displayName  The display name text.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder display(@Nullable String displayName) {

        _display = displayName;

        return this;
    }

    /**
     * Set the amount.
     *
     * @param amount  The amount.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder amount(int amount) {
        _amount = amount;

        return this;
    }

    /**
     * Set the lore.
     *
     * @param lore  An array of line text.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder lore(String... lore) {
        PreCon.notNull(lore);

        _lore = new ArrayList<>(lore.length);
        Collections.addAll(_lore, lore);

        return this;
    }

    /**
     * Set the lore.
     *
     * @param lore  A collection of line text.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder lore(Collection<String> lore) {
        PreCon.notNull(lore);

        _lore = new ArrayList<>(lore);

        return this;
    }

    /**
     * Set the durability.
     *
     * @param durability  The durability value.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder durability(int durability) {
        _durability = (short)durability;

        return this;
    }

    /**
     * Add an enchantment.
     *
     * @param level        The enchantment level.
     * @param enchantment  The enchantment.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder enchant(int level, Enchantment enchantment) {
        PreCon.notNull(enchantment);

        if (_enchantments == null)
            _enchantments = new ArrayList<>(15);

        _enchantments.add(new EnchantmentWrapper(enchantment, level));

        return this;
    }

    /**
     * Add an enchantment.
     *
     * @param level            The enchantment level.
     * @param enchantmentName  The enchantment name.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder enchant(int level, String enchantmentName) {
        PreCon.notNull(enchantmentName);

        Enchantment enchantment = Enchantment.getByName(enchantmentName);
        if (enchantment == null)
            throw new RuntimeException("Cannot find an enchantment named " + enchantmentName);

        return enchant(level, enchantment);
    }

    /**
     * Set the RGB color.
     *
     * <p>Not all items can have their color set.</p>
     *
     * @param color  The color.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder color(Color color) {
        PreCon.notNull(color);

        _color = color;
        return this;
    }

    /**
     * Set the RGB color.
     *
     * <p>Not all items can have their color set.</p>
     *
     * @param red    The red component.
     * @param green  The green component.
     * @param blue   The blue component.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder color(int red, int green, int blue) {
        _color = Color.fromRGB(red, green, blue);
        return this;
    }

    /**
     * Set the RGB color.
     *
     * <p>Not all items can have their color set.</p>
     *
     * @param rgb  The rgb integer.
     *
     * @return  Self for chaining.
     */
    public ItemStackBuilder color(int rgb) {

        _color = Color.fromRGB(rgb);
        return this;
    }

    /**
     * Build and return a new {@Code ItemStack}.
     */
    public ItemStack build() {

        ItemStack itemStack = new ItemStack(_materialData.getItemType());
        itemStack.setData(_materialData.clone());
        itemStack.setAmount(_amount);

        if (_durability != null)
            itemStack.setDurability(_durability);

        if (_display != null)
            ItemStackUtils.setDisplayName(itemStack, _display);

        if (_lore != null)
            ItemStackUtils.setLore(itemStack, _lore);

        if (_enchantments != null) {

            for (EnchantmentWrapper wrapper : _enchantments) {
                itemStack.addUnsafeEnchantment(wrapper.getEnchantment(), wrapper.getLevel());
            }
        }

        if (_color != null) {
            ItemStackUtils.setColor(itemStack, _color);
        }

        return itemStack;
    }
}
