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


package com.jcwhatever.nucleus.scripting.api;

import com.jcwhatever.nucleus.utils.items.ItemStackComparer;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Provide scripts with API access to chest helper functions.
 */
@ScriptApiInfo(
        variableName = "inventory",
        description = "Provide scripts with API access to chest helper functions.")
public class ScriptApiInventory extends NucleusScriptApi {

    private static ApiObject _api;

    /**
     * Constructor. Automatically adds variable to script.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiInventory(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        if (_api == null)
            _api = new ApiObject();

        return _api;
    }

    public void reset() {
        if (_api != null)
            _api.dispose();
    }

    public static class ApiObject implements IScriptApiObject {

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void dispose() {
            // do nothing
        }

        /**
         * Get an {@code ItemStack} comparer.
         *
         * @param operations  The compare operations to perform.
         */
        public ItemStackComparer getComparer(byte operations) {
            return ItemStackComparer.getCustom(operations);
        }

        /**
         * Get the default item stack comparer.
         */
        public ItemStackComparer getDefaultComparer() {
            return ItemStackComparer.getDefault();
        }

        /**
         * Get the durability/meta/type item stack comparer.
         */
        public ItemStackComparer getDurabilityComparer() {
            return ItemStackComparer.getDefault();
        }

        /**
         * Get the maximum number of the specified item stack that will fit into
         * the specified inventory.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public int getMax(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryUtils.getMax(inventory, itemStack, comparer);
        }

        /**
         * Determine if there is enough room in the specified inventory
         * for the specified stack.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryUtils.hasRoom(inventory, itemStack, comparer, itemStack.getAmount());
        }

        /**
         * Determine if there is enough room in the specified inventory for
         * items of the same type of the specified stack in the amount of
         * the specified quantity.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check .
         * @param comparer   The {@code ItemStackComparer} to use.
         * @param qty        The amount of space needed.
         */
        public boolean hasRoomForQty(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryUtils.hasRoom(inventory, itemStack, comparer, qty);
        }

        /**
         * Get the number of items of the specified item stack are in the
         * specified inventory.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public int count(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryUtils.count(inventory, itemStack, comparer);
        }

        /**
         * Determine if the inventory contents have at least one of the specified item stack.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public boolean has(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryUtils.has(inventory, itemStack, comparer);
        }

        /**
         * Determine if the inventory contents has at least the specified quantity of
         * the specified item stack.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         * @param qty        The quantity.
         */
        public boolean hasQty(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryUtils.has(inventory, itemStack, comparer, qty);
        }

        /**
         * Get an item stack array representing all stacks of the specified item
         * from the specified inventory.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public ItemStack[] getAll(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryUtils.getAll(inventory, itemStack, comparer);
        }

        /**
         * Remove items from the specified inventory that match the specified
         * item stack in the specified quantity.
         *
         * @param inventory  The inventory to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         * @param qty        The quantity to remove.
         */
        public List<ItemStack> remove(Inventory inventory, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryUtils.remove(inventory, itemStack, comparer, qty);
        }
    }
}
