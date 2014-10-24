package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.inventory.InventoryHelper;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.IScriptApiInfo;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Provide scripts with API access to inventory helper functions.
 */
@IScriptApiInfo(
        variableName = "inventory",
        description = "Provide scripts with API access to inventory helper functions.")
public class ScriptApiInventory extends GenericsScriptApi {

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

    @Override
    public void reset() {
        if (_api != null)
            _api.reset();
    }

    public static class ApiObject implements IScriptApiObject {

        @Override
        public void reset() {
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
         * Get the maximum number of the specified item stack that will fit into
         * the specified inventory contents.
         *
         * @param contents   The inventory contents.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         * @return
         */
        public int getMax(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryHelper.getMax(contents, itemStack, comparer);
        }

        /**
         * Determine if there is enough room in the specified inventory
         * for the specified stack.
         *
         * @param contents  The inventory contents to check.
         * @param itemStack The item stack to check.
         * @param comparer  The {@code ItemStackComparer} to use.
         */
        public boolean hasRoom(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryHelper.hasRoom(contents, itemStack, comparer, itemStack.getAmount());
        }

        /**
         * Determine if there is enough room in the specified inventory for
         * items of the same type of the specified stack in the amount of
         * the specified quantity.
         *
         * @param contents  The inventory contents to check.
         * @param itemStack The item stack to check .
         * @param comparer  The {@code ItemStackComparer} to use.
         * @param qty       The amount of space needed.
         */
        public boolean hasRoom(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryHelper.hasRoom(contents, itemStack, comparer, qty);
        }

        /**
         * Get the number of items of the specified item stack are in the
         * specified inventory contents.
         *
         * @param contents   The inventory contents to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public int count(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryHelper.count(contents, itemStack, comparer);
        }

        /**
         * Determine if the inventory contents have at least one of the specified item stack.
         *
         * @param contents   The inventory contents to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public boolean has(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryHelper.has(contents, itemStack, comparer);
        }

        /**
         * Determine if the inventory contents has at least the specified quantity of
         * the specified item stack.
         *
         * @param contents   The inventory contents to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         * @param qty        The quantity.
         */
        public boolean has(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryHelper.has(contents, itemStack, comparer, qty);
        }

        /**
         * Get an item stack array representing all stacks of the specified item
         * from the specified inventory contents.
         *
         * @param contents   The inventory contents to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         */
        public ItemStack[] getAll(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer) {
            return InventoryHelper.getAll(contents, itemStack, comparer);
        }

        /**
         * Remove items from the specified inventory contents that match the specified
         * item stack in the specified quantity.
         *
         * @param contents   The inventory contents to check.
         * @param itemStack  The item stack to check.
         * @param comparer   The {@code ItemStackComparer} to use.
         * @param qty        The quantity to remove.
         * @return
         */
        public List<ItemStack> remove(ItemStack[] contents, ItemStack itemStack, ItemStackComparer comparer, int qty) {
            return InventoryHelper.remove(contents, itemStack, comparer, qty);
        }
    }


}
