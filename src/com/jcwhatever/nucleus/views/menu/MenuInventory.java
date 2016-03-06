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

import com.jcwhatever.nucleus.mixins.IWrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * An {@link org.bukkit.inventory.Inventory} wrapper.
 *
 * <p>Note that CraftBukkit methods that accept {@link org.bukkit.inventory.Inventory}
 * instances cast them to CraftInventory. If the instance is to be passed to a Bukkit method,
 * use {@link com.jcwhatever.nucleus.utils.Utils#unwrap} or invoke {@link #getHandle} to
 * get the CraftInventory instance.</p>
 */
public class MenuInventory implements Inventory, IWrapper<Inventory> {

    private final Map<Integer, MenuItem> _menuItemMap;
    private final Inventory _inventory;

    public MenuInventory(InventoryHolder inventoryHolder, int slots, @Nullable String title) {

        _inventory = title != null
                ? Bukkit.createInventory(inventoryHolder, slots, title)
                : Bukkit.createInventory(inventoryHolder, slots);

        _menuItemMap = new HashMap<>(slots);
    }

    /**
     * Get encapsulated {@link org.bukkit.inventory.Inventory}
     * which is a CraftBukkit instance.
     */
    @Override
    public Inventory getHandle() {
        return _inventory;
    }

    /**
     * Add a menu item to the {@link MenuInventory}.
     *
     * @param menuItem  The menu item to add.
     */
    public void addMenuItem(MenuItem menuItem) {
        _inventory.setItem(menuItem.getSlot(), menuItem);

        _menuItemMap.put(menuItem.getSlot(), menuItem);
    }

    /**
     * Get the menu item assigned to the specified slot.
     *
     * @param slot  The slot.
     *
     * @return  Null if no menu item assigned.
     */
    @Nullable
    public MenuItem getMenuItem(int slot) {
        return _menuItemMap.get(slot);
    }

    /**
     * Get the total number of {@link MenuItem}.
     */
    public int getTotalMenuItems() {
        return _menuItemMap.size();
    }

    /**
     * Get all {@link MenuItem}.
     */
    public Collection<MenuItem> getMenuItems() {
        return _menuItemMap.values();
    }

    @Override
    public int getSize() {
        return _inventory.getSize();
    }

    @Override
    public int getMaxStackSize() {
        return _inventory.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int maxStackSize) {
        _inventory.setMaxStackSize(maxStackSize);
    }

    @Override
    public String getName() {
        return _inventory.getName();
    }

    @Override
    public ItemStack getItem(int index) {
        return _inventory.getItem(index);
    }

    @Override
    public void setItem(int index, ItemStack itemStack) {

        if (itemStack instanceof MenuItem) {
            MenuItem menuItem = (MenuItem)itemStack;
            _menuItemMap.put(index, menuItem);
        }
        else {
            _menuItemMap.remove(index);
        }

        _inventory.setItem(index, itemStack);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks)
            throws IllegalArgumentException {

        HashMap<Integer, ItemStack> unstorable = new HashMap<>(itemStacks.length);

        for (int i=0; i < itemStacks.length; i++) {

            ItemStack itemStack = itemStacks[i];

            if (itemStack instanceof MenuItem) {
                MenuItem menuItem = (MenuItem)itemStack;

                setItem(menuItem.getSlot(), menuItem);
            }
            else {
                unstorable.put(i, itemStack);
            }
        }

        return unstorable;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks)
            throws IllegalArgumentException {
        return _inventory.removeItem(itemStacks);
    }

    @Override
    public ItemStack[] getContents() {
        return _inventory.getContents();
    }

    @Override
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {

        _menuItemMap.clear();

        for (int i=0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];

            if (itemStack == null || itemStack.getType() == Material.AIR)
                continue;

            if (itemStack instanceof MenuItem) {
                MenuItem menuItem = (MenuItem)itemStack;
                _menuItemMap.put(i, menuItem);
            }
        }

        _inventory.setContents(itemStacks);
    }

    @Override
    @Deprecated
    public boolean contains(int materialId) {
        return _inventory.contains(materialId);
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return _inventory.contains(material);
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        return _inventory.contains(itemStack);
    }

    @Override
    @Deprecated
    public boolean contains(int materialId, int amount) {
        return _inventory.contains(materialId, amount);
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        return _inventory.contains(material, amount);
    }

    @Override
    public boolean contains(ItemStack itemStack, int amount) {
        return _inventory.contains(itemStack, amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int amount) {
        return _inventory.containsAtLeast(itemStack, amount);
    }

    @Override
    @Deprecated
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return _inventory.all(materialId);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return _inventory.all(material);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return _inventory.all(itemStack);
    }

    @Override
    @Deprecated
    public int first(int materialId) {
        return _inventory.first(materialId);
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        return _inventory.first(material);
    }

    @Override
    public int first(ItemStack itemStack) {
        return _inventory.first(itemStack);
    }

    @Override
    public int firstEmpty() {
        return _inventory.firstEmpty();
    }

    @Override
    @Deprecated
    public void remove(int materialId) {
        _inventory.remove(materialId);
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException {
        _inventory.remove(material);
    }

    @Override
    public void remove(ItemStack itemStack) {
        _inventory.remove(itemStack);
    }

    @Override
    public void clear(int materialId) {
        _inventory.clear(materialId);
    }

    @Override
    public void clear() {
        _inventory.clear();
    }

    @Override
    public List<HumanEntity> getViewers() {
        return _inventory.getViewers();
    }

    @Override
    public String getTitle() {
        return _inventory.getTitle();
    }

    @Override
    public InventoryType getType() {
        return _inventory.getType();
    }

    @Override
    public InventoryHolder getHolder() {
        return _inventory.getHolder();
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return _inventory.iterator();
    }

    @Override
    @Deprecated
    public ListIterator<ItemStack> iterator(int materialId) {
        return _inventory.iterator();
    }

    @Override
    public Location getLocation() {
        return _inventory.getLocation();
    }
}
