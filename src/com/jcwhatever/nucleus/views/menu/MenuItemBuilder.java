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

package com.jcwhatever.nucleus.views.menu;

import com.jcwhatever.nucleus.managed.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.materials.NamedMaterialData;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class to build {@link MenuItem}'s.
 */
public class MenuItemBuilder {

    private ItemStack _itemStack;
    private MaterialData _materialData;
    private Integer _amount;
    private CharSequence _title;
    private CharSequence _description;
    private Map<Object, Object> _meta;
    private List<Runnable> _onClick;

    /**
     * Constructor.
     *
     * @param itemStack  The {@link org.bukkit.inventory.ItemStack} that represents the menu item.
     */
    public MenuItemBuilder(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _itemStack = itemStack;
    }

    /**
     * Constructor.
     *
     * @param material  The item {@link org.bukkit.Material}.
     */
    public MenuItemBuilder(Material material) {
        PreCon.notNull(material);

        _materialData = new MaterialData(material);
    }

    /**
     * Constructor.
     *
     * @param materialData  The item {@link org.bukkit.material.MaterialData}.
     */
    public MenuItemBuilder(MaterialData materialData) {
        PreCon.notNull(materialData);

        _materialData = materialData.clone();
    }

    /**
     * Constructor.
     *
     * @param materialName  The name of the material or serialized item stack string.
     */
    public MenuItemBuilder(String materialName) {
        PreCon.notNullOrEmpty(materialName);

        _materialData = NamedMaterialData.get(materialName);
        if (_materialData == null) {
            try {
                // try parsing serialized item stack
                ItemStack[] itemStacks = ItemStackUtils.parse(materialName);
                if (itemStacks != null && itemStacks.length == 1) {
                    _itemStack = itemStacks[0];
                    return;
                }

            } catch (InvalidItemStackStringException ignore) {}

            throw new RuntimeException(materialName + " is not a recognized material.");
        }
    }

    /**
     * Set the menu item title.
     *
     * @param title  The title text.
     * @param args   Optional title format arguments.
     *
     * @return  Self for chaining.
     */
    public MenuItemBuilder title(CharSequence title, Object... args) {
        PreCon.notNull(title);

        _title = TextUtils.format(title, args);

        return this;
    }

    /**
     * Set the menu item description.
     *
     * @param description  The description text.
     * @param args         Optional description format arguments.
     *
     * @return  Self for chaining.
     */
    public MenuItemBuilder description(CharSequence description, Object... args) {
        PreCon.notNull(description);

        _description = TextUtils.format(description, args);

        return this;
    }

    /**
     * Set the amount.
     *
     * @param amount  The amount.
     *
     * @return  Self for chaining.
     */
    public MenuItemBuilder amount(int amount) {
        _amount = amount;

        return this;
    }

    /**
     * Add meta data to the menu item.
     *
     * @param key    The meta key.
     * @param value  The meta value.
     *
     * @param <T>  The meta value type.
     *
     * @return  Self for chaining.
     */
    public <T> MenuItemBuilder meta(MetaKey<T> key, T value) {
        PreCon.notNull(key);

        if (_meta == null) {
            _meta = new HashMap<>(7);
        }

        _meta.put(key, value);

        return this;
    }

    /**
     * Add an onClick handler to the menu item.
     *
     * @param onClick  The onClick handler.
     *
     * @return  Self for chaining.
     */
    public MenuItemBuilder onClick(final Runnable onClick) {
        PreCon.notNull(onClick);

        if (_onClick == null)
            _onClick = new ArrayList<>(3);

        // encapsulate handler to prevent exceptions caused by script objects
        // that do not have a hashCode or equals method.
        _onClick.add(new Runnable() {
            @Override
            public void run() {
                onClick.run();
            }
        });

        return this;
    }

    /**
     * Build and return a new {@link MenuItem}.
     *
     * @param slot  The inventory slot the menu item will be placed in.
     */
    public MenuItem build(int slot) {

        if (_itemStack == null) {
            _itemStack = new ItemStack(_materialData.getItemType());
            _itemStack.setData(_materialData);
        }

        MenuItem item = createMenuItem(slot, _itemStack.clone(), _meta, _onClick);

        if (_amount != null)
            item.setAmount(_amount);

        if (_title != null)
            item.setTitle(_title);

        if (_description != null)
            item.setDescription(_description);

        return item;
    }

    protected MenuItem createMenuItem(int slot, ItemStack itemStack,
                                      @Nullable Map<Object, Object> meta,
                                      @Nullable List<Runnable> onClick) {

        MenuItem menuItem = new MenuItem(slot, itemStack);

        if (meta != null)
            menuItem.getMeta().copyAll(meta);

        if (onClick != null) {
            for (Runnable runnable : onClick) {
                menuItem.onClick(runnable);
            }
        }

        return menuItem;
    }
}
