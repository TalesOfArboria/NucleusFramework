/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Compares {@code ItemStack}'s to determine if they are the same based
 * on defined compare operations parameters.
 */
public class ItemStackComparer {

    /**
     * Bit flag. Compare item stacks based on material and when applicable, based on byte data.
     */
    public static final byte COMPARE_TYPE = 1;

    /**
     * Bit flag. Compare item stacks based on durability value.
     */
    public static final byte COMPARE_DURABILITY = 2;

    /**
     * Bit flag. Compare item stacks based on meta data.
     */
    public static final byte COMPARE_META = 4;

    /**
     * Bit flag. Compare item stacks based on the stack amount.
     */
    public static final byte COMPARE_AMOUNT = 8;

    /**
     * Bit flag. The default compare method. Compares by type and meta data.
     */
    public static final byte DEFAULT_COMPARE = COMPARE_TYPE | COMPARE_META;

    /**
     * Bit flag. Compares by type, meta data, and durability.
     */
    public static final byte DURABILITY_COMPARE = COMPARE_TYPE | COMPARE_META | COMPARE_DURABILITY;

    private static ItemStackComparer _default;
    private static ItemStackComparer _typeComparer;
    private static ItemStackComparer _durability;
    private static Map<Byte, ItemStackComparer> _custom = new HashMap<Byte, ItemStackComparer>(35);

    /**
     * Get the default singleton instance of the ItemStackComparer.
     * Compares by type and meta data.
     */
    public static ItemStackComparer getDefault() {
        if (_default == null)
            _default = new ItemStackComparer(DEFAULT_COMPARE);

        return _default;
    }

    /**
     * Get a singleton instance of an ItemStackComparer that compares
     * by type, meta data, and durability.
     */
    public static ItemStackComparer getDurability() {
        if (_durability == null)
            _durability = new ItemStackComparer(DURABILITY_COMPARE);

        return _durability;
    }

    /**
     * Get a singleton instance of a an ItemStackComparer that compares
     * by type only.
     */
    public static ItemStackComparer getTypeComparer() {
        if (_typeComparer == null)
            _typeComparer = new ItemStackComparer(COMPARE_TYPE);

        return _typeComparer;
    }

    /**
     * Get a singleton instance of a custom ItemStackComparer.
     *
     * @param compareOperations  The compare operations bit flags to use.
     */
    public static ItemStackComparer getCustom(byte compareOperations) {
        ItemStackComparer comparer = _custom.get(compareOperations);
        if (comparer == null) {
            comparer = new ItemStackComparer(compareOperations);
            _custom.put(compareOperations, comparer);
        }

        return comparer;
    }

    private byte _compareOperations = DEFAULT_COMPARE;
    private boolean _compareType = true;
    private boolean _compareMeta = true;
    private boolean _compareDurability = false;
    private boolean _compareAmount = false;

    private ItemStackComparer() {}

    /**
     * Constructor.
     *
     * @param compareOperations  The compare operations bit flags to use.
     */
    public ItemStackComparer(byte compareOperations) {
        _compareOperations = compareOperations;
        _compareType = (_compareOperations & COMPARE_TYPE) == COMPARE_TYPE;
        _compareDurability = (_compareOperations & COMPARE_DURABILITY) == COMPARE_DURABILITY;
        _compareMeta = (_compareOperations & COMPARE_META) == COMPARE_META;
        _compareAmount = (_compareOperations & COMPARE_AMOUNT) == COMPARE_AMOUNT;
    }

    /**
     * Get the compare operations bit flags.
     */
    public byte getCompareOperations() {
        return _compareOperations;
    }

    /**
     * Determine if the comparer compares item type.
     */
    public boolean comparesType() {
        return _compareType;
    }

    /**
     * Determine if the comparer compares item meta data.
     */
    public boolean comparesMeta() {
        return _compareMeta;
    }

    /**
     * Determine if the comparer compares item durability.
     */
    public boolean comparesDurability() {
        return _compareDurability;
    }

    /**
     * Determine if the comparer compares item amount.
     */
    public boolean comparesAmount() {
        return _compareAmount;
    }

    /**
     * Determine if two stacks are the same based on the properties
     * the comparer compares.
     *
     * @param stack1  The first item stack to compare.
     * @param stack2  The second item stack to compare.
     */
    public boolean isSame(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {

        // if either stack is null, they must not be the same.
        if (stack1 == null || stack2 == null)
            return false;

        // same instance is always the same
        if (stack1 == stack2)
            return true;

        // compare type
        if (_compareType) {
            if (stack1.getType() != stack2.getType())
                return false;

            MaterialExt ext = MaterialExt.from(stack1.getType());

            if (!_compareMeta && (ext.usesColorData() || ext.usesSubMaterialData()) &&
                    !stack1.getData().equals(stack2.getData())) {
                return false;
            }
        }

        // compare meta data
        if (_compareMeta) {

            if (stack1.hasItemMeta() != stack2.hasItemMeta())
                return false;

            if (stack1.hasItemMeta() && !Bukkit.getItemFactory().equals(stack1.getItemMeta(), stack2.getItemMeta()))
                return false;
        }

        // compare durability
        if (_compareDurability && stack1.getDurability() != stack2.getDurability())
            return false;

        // compare amount
        if (_compareAmount && stack1.getAmount() != stack2.getAmount())
            return false;

        return true;
    }

}
