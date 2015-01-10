package com.jcwhatever.dummy;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/*
 * 
 */
public class DummyInventory implements Inventory {

    private InventoryHolder _holder;
    private InventoryType _type;
    private List<ItemStack> _array;
    private int _maxStackSize = 64;

    public DummyInventory(InventoryHolder holder, InventoryType type, int size) {
        _holder = holder;
        _type = type;
        _array = new ArrayList<>(size);

        for (int i=0; i < size; i++)
            _array.add(null);
    }

    @Override
    public int getSize() {
        return _array.size();
    }

    @Override
    public int getMaxStackSize() {
        return _maxStackSize;
    }

    @Override
    public void setMaxStackSize(int i) {
        _maxStackSize = i;
    }

    @Override
    public String getName() {
        return "Inventory";
    }

    @Override
    public ItemStack getItem(int i) {
        return _array.get(i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        _array.set(i, itemStack);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        return null;
    }

    @Override
    public ItemStack[] getContents() {
        return _array.toArray(new ItemStack[_array.size()]);
    }

    @Override
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        _array.clear();
        Collections.addAll(_array, itemStacks);
    }

    @Override
    public boolean contains(int id) {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.getType().getId() == id)
                return true;
        }
        return false;
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.getType() == material)
                return true;
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack itemStack) {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.equals(itemStack))
                return true;
        }
        return false;
    }

    @Override
    public boolean contains(int id, int amount) {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.getType().getId() == id) {
                if (stack.getAmount() >= amount)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(Material material, int amount) throws IllegalArgumentException {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.getType() == material) {
                if (stack.getAmount() >= amount)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemStack itemStack, int amount) {
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.equals(itemStack)) {
                if (stack.getAmount() >= amount)
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int amount) {
        int found = 0;
        for (ItemStack stack : _array) {
            if (stack == null)
                continue;

            if (stack.equals(itemStack)) {
                found += stack.getAmount();
            }
        }
        return found >= amount;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        return null;
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        return null;
    }

    @Override
    public int first(int id) {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.getType().getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.getType() == material) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int first(ItemStack itemStack) {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.equals(itemStack)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null || stack.getType() == Material.AIR)
                return i;
        }
        return -1;
    }

    @Override
    public void remove(int id) {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.getType().getId() == id) {
                _array.set(i, null);
            }
        }
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.getType() == material) {
                _array.set(i, null);
            }
        }
    }

    @Override
    public void remove(ItemStack itemStack) {
        for (int i=0; i < _array.size(); i++) {
            ItemStack stack = _array.get(i);

            if (stack == null)
                continue;

            if (stack.equals(itemStack)) {
                _array.set(i, null);
            }
        }
    }

    @Override
    public void clear(int i) {
        _array.set(i, null);
    }

    @Override
    public void clear() {
        int size = _array.size();
        _array.clear();
        for (int i=0; i < size; i++)
            _array.add(null);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return new ArrayList<>(0);
    }

    @Override
    public String getTitle() {
        return "Inventory";
    }

    @Override
    public InventoryType getType() {
        return _type;
    }

    @Override
    public InventoryHolder getHolder() {
        return _holder;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return _array.listIterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        return _array.listIterator(i);
    }
}
