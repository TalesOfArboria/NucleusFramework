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

import com.jcwhatever.nucleus.utils.extended.MaterialExt;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Compares {@link org.bukkit.inventory.ItemStack}'s to determine if they are the same based
 * on defined compare operations parameters.
 */
public class ItemStackMatcher {

    /**
     * Bit flag. Match item stacks based on material and when applicable, based on byte data.
     */
    public static final byte MATCH_TYPE = 1;

    /**
     * Bit flag. Match item stacks based on durability value.
     */
    public static final byte MATCH_DURABILITY = 2;

    /**
     * Bit flag. Match item stacks based on meta data.
     */
    public static final byte MATCH_META = 4;

    /**
     * Bit flag. Match item stacks based on the stack amount.
     */
    public static final byte MATCH_AMOUNT = 8;

    /**
     * Bit flag. The default matching method. Matches by type and meta data.
     */
    public static final byte DEFAULT_MATCH = MATCH_TYPE | MATCH_META;

    /**
     * Bit flag. Match by type, meta data, and durability.
     */
    public static final byte TYPE_META_DURABILITY_MATCH = MATCH_TYPE | MATCH_META | MATCH_DURABILITY;

    private static final ItemStackMatcher _default = new ItemStackMatcher(DEFAULT_MATCH);
    private static final ItemStackMatcher _typeMatcher = new ItemStackMatcher(MATCH_TYPE);
    private static final ItemStackMatcher _durabilityMatcher = new ItemStackMatcher(TYPE_META_DURABILITY_MATCH);
    private static final Map<Byte, ItemStackMatcher> _custom = new HashMap<Byte, ItemStackMatcher>(35);

    /**
     * Get the default singleton instance of the {@link ItemStackMatcher}.
     * Matches by type and meta data.
     */
    public static ItemStackMatcher getDefault() {
        return _default;
    }

    /**
     * Get a singleton instance of an {@link ItemStackMatcher} that matches
     * by type, meta data, and durability.
     */
    public static ItemStackMatcher getTypeMetaDurability() {
        return _durabilityMatcher;
    }

    /**
     * Get a singleton instance of a an ItemStackMatcher that compares
     * by type only.
     */
    public static ItemStackMatcher getTypeMatcher() {
        return _typeMatcher;
    }

    /**
     * Get a singleton instance of a {@link ItemStackMatcher} with custom
     * matcher operations combinations defined.
     *
     * @param matchOperations  The matcher operations bit flags to use.
     */
    public static synchronized ItemStackMatcher getCustom(byte matchOperations) {
        ItemStackMatcher comparer = _custom.get(matchOperations);
        if (comparer == null) {
            comparer = new ItemStackMatcher(matchOperations);
            _custom.put(matchOperations, comparer);
        }

        return comparer;
    }

    private byte _matchOperations = DEFAULT_MATCH;
    private boolean _matchType = true;
    private boolean _matchMeta = true;
    private boolean _matchDurability = false;
    private boolean _matchAmount = false;

    private ItemStackMatcher() {}

    /**
     * Constructor.
     *
     * @param matchOperations  The match operations bit flags to use.
     */
    public ItemStackMatcher(byte matchOperations) {
        _matchOperations = matchOperations;
        _matchType = (_matchOperations & MATCH_TYPE) == MATCH_TYPE;
        _matchDurability = (_matchOperations & MATCH_DURABILITY) == MATCH_DURABILITY;
        _matchMeta = (_matchOperations & MATCH_META) == MATCH_META;
        _matchAmount = (_matchOperations & MATCH_AMOUNT) == MATCH_AMOUNT;
    }

    /**
     * Get the matcher operations bit flags.
     */
    public byte getMatcherOperations() {
        return _matchOperations;
    }

    /**
     * Determine if the matcher matches item type.
     */
    public boolean isTypeMatcher() {
        return _matchType;
    }

    /**
     * Determine if the matcher matches item meta data.
     */
    public boolean isMetaMatcher() {
        return _matchMeta;
    }

    /**
     * Determine if the matcher matches item durability.
     */
    public boolean isDurabilityMatcher() {
        return _matchDurability;
    }

    /**
     * Determine if the matcher matches item amount.
     */
    public boolean isAmountMatcher() {
        return _matchAmount;
    }

    /**
     * Determine if two stacks are the same based on the properties
     * the matcher matches..
     *
     * @param stack1  The first item stack to compare.
     * @param stack2  The second item stack to compare.
     */
    public boolean isMatch(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {

        // if either stack is null, they must not be the same.
        if (stack1 == null || stack2 == null)
            return false;

        // same instance is always the same
        if (stack1 == stack2)
            return true;

        // compare type
        if (_matchType) {
            if (stack1.getType() != stack2.getType())
                return false;

            MaterialExt ext = MaterialExt.from(stack1.getType());

            if (!_matchMeta && (ext.usesColorData() || ext.usesSubMaterialData()) &&
                    !stack1.getData().equals(stack2.getData())) {
                return false;
            }
        }

        // compare meta data
        if (_matchMeta) {

            if (stack1.hasItemMeta() != stack2.hasItemMeta())
                return false;

            if (stack1.hasItemMeta() && !Bukkit.getItemFactory().equals(stack1.getItemMeta(), stack2.getItemMeta()))
                return false;
        }

        // compare durability
        if (_matchDurability && stack1.getDurability() != stack2.getDurability())
            return false;

        // compare amount
        if (_matchAmount && stack1.getAmount() != stack2.getAmount())
            return false;

        return true;
    }

}
