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

import com.jcwhatever.nucleus.extended.MaterialExt;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;

/**
 * An {@code ItemStack} wrapper.
 *
 * <p>
 *     Provides built in {@code ItemStackMatcher} support in the
 *     {@code equals} method making the wrapper ideal for use as a {@code Map} key.
 * </p>
 *
 * <p>
 *     {@code hashCode} method returns the hash of the encapsulated {@code ItemStack}'s
 *     {@code Material} type so that different {@code ItemStackMatcher} compare operations
 *     can be used to find an {@code ItemStack} by key or in a hash set.
 * </p>
 *
 */
public class ItemWrapper {

    private ItemStack _itemStack;
    private MaterialExt _materialExt;
    private ItemStackMatcher _comparer;
    private int _hash = -1;

    /**
     * Constructor.
     *
     * <p>
     *     Uses the default {@code ItemStackMatcher}.
     * </p>
     *
     * @param itemStack  The item stack to encapsulate.
     */
    public ItemWrapper(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _itemStack = itemStack;
        _comparer = ItemStackMatcher.getDefault();
    }

    /**
     * Constructor.
     *
     * @param itemStack  The item stack to encapsulate.
     * @param matcher    The matcher  to use.
     */
    public ItemWrapper(ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher );

        _itemStack = itemStack;
        _comparer = matcher ;
    }

    /**
     * Get the encapsulated {@code ItemStack}.
     */
    public ItemStack getItem() {
        return _itemStack;
    }

    /**
     * Get the extended material type.
     */
    public MaterialExt getMaterialExt() {
        if (_materialExt == null) {
            _materialExt = MaterialExt.from(_itemStack.getType());
        }

        return _materialExt;
    }

    /**
     * Get the compare operations of the {@code ItemStackMatcher}.
     */
    public byte getCompareOperations() {
        return _comparer.getMatcherOperations();
    }

    /**
     * Get the {@code ItemStackMatcher}.
     */
    public ItemStackMatcher getItemStackMatcher() {
        return _comparer;
    }

    @Override
    public int hashCode() {
        if (_hash == -1) {
            _hash = 1;
            _hash = _hash * 31 + _itemStack.getTypeId();
        }

        return _hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ItemStack) {
            return _comparer.isMatch(_itemStack, (ItemStack) o);
        }
        else if (o instanceof ItemWrapper) {
            ItemWrapper wrapper = (ItemWrapper)o;

            return _comparer.isMatch(_itemStack, wrapper.getItem());
        }

        return false;
    }
}
