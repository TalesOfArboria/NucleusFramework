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

package com.jcwhatever.nucleus.utils.inventory;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Inventory builder.
 */
public class InventoryBuilder {

    public static final int MAX_ROWS = 6;
    public static final int MAX_COLS = 9;
    public static final int MAX_SLOTS = MAX_ROWS * MAX_COLS;
    public static final char EMPTY_SLOT_CHAR = '0';

    protected String _title;
    protected int _rows = 6;
    protected boolean _rowsSetExplicit;

    protected ItemStack[][] _layoutArray;

    protected String[] _layout;
    protected List<ItemStack> _items;
    protected Map<Character, ItemStack> _layoutMap;

    /**
     * Set the inventory title.
     *
     * @param title  The title of the inventory.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder title(String title) {
        PreCon.notNull(title);

        _title = title;
        return this;
    }

    /**
     * Set the number of slots to show.
     *
     * @param slots  The number of slots.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder slots(int slots) {
        PreCon.positiveNumber(slots, "Number of slots must be a positive number. ({0})", slots);
        PreCon.lessThanEqual(slots, MAX_SLOTS, "Cannot have more than {0} slots. ({1})", MAX_SLOTS, slots);

        _rows = (int)Math.ceil((double)slots / 9);
        _rowsSetExplicit = true;
        return this;
    }

    /**
     * Set the number of rows to show.
     *
     * @param rows  The number of rows.
     * @return
     */
    public InventoryBuilder rows(int rows) {
        PreCon.positiveNumber(rows, "Number of rows must be a positive number. ({0})", rows);
        PreCon.lessThanEqual(rows, MAX_ROWS, "Cannot have more than {0} rows. ({1})", MAX_ROWS, rows);

        _rows = rows;
        _rowsSetExplicit = true;
        return this;
    }

    /**
     * Set the layout template. Each character in a string represents an item stack slot.
     * The string can be no more than 9 characters as this is the max column size. The
     * character '0' denotes the slot is empty. Each string provided represents 1 row. No
     * more than 6 strings can be provided as this is the max row size.
     *
     * @param template  The layout template.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder layout(String... template) {
        PreCon.lessThanEqual(template.length, MAX_ROWS,
                "Cannot have more than '{0}' rows. ({1})", MAX_ROWS, template.length);

        int columns = 0;
        int slots = 0;

        for (String row : template) {
            if (row.length() > columns)
                columns = row.length();

            if (columns > MAX_COLS)
                throw new IllegalArgumentException("Cannot have more than " + MAX_COLS + " columns.");

            slots += MAX_COLS;

            if (slots > MAX_SLOTS)
                throw new IllegalArgumentException("Cannot have more than " + MAX_SLOTS + "slots");
        }

        _layout = template;
        setSlots(slots);

        return this;
    }

    /**
     * Assign an {@code ItemStack} to a layout character. When parsing the layout template,
     * the {@code ItemStack} will be placed into the inventory whenever the specified
     * layout character is encountered.
     *
     * @param layoutChar  The layout character.
     * @param itemStack   The {@code ItemStack} to assign.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder item(char layoutChar, @Nullable ItemStack itemStack) {

        if (_layoutMap == null)
            _layoutMap = new HashMap<>(15);

        _layoutMap.put(layoutChar, itemStack);

        return this;
    }

    /**
     * Set the inventory items. If no template is specified, the
     * items will be added in linear order until there is no more room or there
     * are no more items left.
     *
     * If a layout template is used, these items are added in linear order
     * whenever a character in the template is not registered for a specific
     * {@code ItemStack}.
     *
     * @param items  The items to add.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder items(Collection<ItemStack> items) {
        PreCon.notNull(items);

        _items = new ArrayList<>(items);

        return this;
    }

    /**
     * Set inventory items. If no template is specified, the
     * items will be added in linear order until there is no more room or there
     * are no more items left.
     *
     * If a layout template is used, these items are added in linear order
     * whenever a character in the template is not registered for a specific
     * {@code ItemStack}.
     *
     * @param items  The items to add.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder items(ItemStack... items) {

        _items = new ArrayList<>(items.length);
        Collections.addAll(_items, items);

        return this;
    }

    /**
     * Add a 2 dimensional array representing the layout of the
     * {@code ItemStack}'s in the inventory.
     *
     * @param layout  The layout of {@code ItemStack}'s.
     *
     * @return  Self for chaining.
     */
    public InventoryBuilder items(ItemStack[/*rows*/][/* columns */] layout) {
        PreCon.notNull(layout);
        PreCon.lessThanEqual(layout.length, MAX_ROWS,
                "Cannot have more than '{0}' rows. ({1})", MAX_ROWS, layout.length);

        int columns = 0;
        int slots = 0;

        for (ItemStack[] row : layout) {

            if (row.length > columns)
                columns = row.length;

            if (columns > MAX_COLS)
                throw new IllegalArgumentException("Cannot have more than " + MAX_COLS + " columns.");

            slots += MAX_COLS;

            if (slots > MAX_SLOTS)
                throw new IllegalArgumentException("Cannot have more than " + MAX_SLOTS + "slots");
        }

        _layoutArray = layout;
        setSlots(slots);

        return this;
    }

    /**
     * Build a new inventory using the current settings.
     *
     * @param holder  The inventory holder.
     */
    public Inventory build(@Nullable InventoryHolder holder) {

        Inventory inventory = _title != null
                ? Bukkit.createInventory(holder, _rows * 9, _title)
                : Bukkit.createInventory(holder, _rows * 9);

        if (_layoutArray != null) {
            fillFromLayoutArray(inventory);
        }
        else if (_layout != null) {
            prepLayout();
            fillFromLayoutArray(inventory);
        }
        else if (_items != null) {
            setSlots(_items.size());
            for (int i = 0; i < Math.min(_items.size(), MAX_SLOTS); i++) {
                ItemStack item = _items.get(i);
                if (item != null)
                    inventory.setItem(i, item.clone());
            }
        }

        return inventory;
    }

    // set slots if not explicitly set or the setting is smaller than required.
    protected void setSlots(int slots) {
        if (!_rowsSetExplicit || slots < _rows * MAX_COLS) {
            _rows = (int)Math.ceil(slots / 9.0D);
        }
    }

    // set rows if not explicitly set or the setting is smaller than required.
    protected void setRows(int rows) {
        if (!_rowsSetExplicit || rows < _rows) {
            _rows = rows;
        }
    }

    // fill an inventory using the layout array.
    protected void fillFromLayoutArray(Inventory inventory) {

        for (int row = 0; row < _layoutArray.length; row++) {
            for (int column = 0; column < _layoutArray.length; column++) {

                ItemStack itemStack = _layoutArray[row][column];

                if (itemStack == null || itemStack.getType() == Material.AIR)
                    continue;

                inventory.setItem(row * MAX_COLS + column, itemStack.clone());
            }
        }
    }

    // prep the layout array using the layout template.
    protected void prepLayout() {
        _layoutArray = new ItemStack[_layout.length][MAX_COLS];

        int itemsIndex = 0;

        for (int row=0; row < _layout.length; row++) {

            String template = _layout[row];

            for (int column=0; column < template.length(); column++) {

                char ch = template.charAt(column);

                if (ch == EMPTY_SLOT_CHAR)
                    continue;

                ItemStack itemStack = null;

                if (_layoutMap != null) {
                    itemStack = _layoutMap.get(ch);
                }

                if (itemStack == null && _items != null) {

                    if (itemsIndex < _items.size()) {
                        itemStack = _items.get(itemsIndex);
                        itemsIndex++;
                    }
                }

                if (itemStack != null)
                    _layoutArray[row][column] = itemStack.clone();
            }
        }
    }
}
