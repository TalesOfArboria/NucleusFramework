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

import com.jcwhatever.nucleus.utils.PreCon;

import com.jcwhatever.nucleus.utils.ThreadSingletons;
import org.bukkit.inventory.ItemStack;

/**
 * A {@link MatchableItem} whose {@link ItemStack} and {@link ItemStackMatcher}
 * can be changed after instantiation.
 */
public class MutableMatchableItem extends MatchableItem {

    /**
     * Create a new {@link ThreadSingletons} of {@link MutableMatchableItem}'s
     */
    public static ThreadSingletons<MutableMatchableItem> createThreadSingletons() {
        return new ThreadSingletons<>(new ThreadSingletons.ISingletonFactory<MutableMatchableItem>() {
            @Override
            public MutableMatchableItem create(Thread thread) {
                return new MutableMatchableItem();
            }
        });
    }

    /**
     * Constructor.
     *
     * <p>Uses the default {@link ItemStackMatcher}.</p>
     */
    public MutableMatchableItem() {
        super(ItemStackMatcher.getDefault());
    }

    /**
     * Constructor.
     *
     * @param matcher  The {@link ItemStackMatcher} to use for matching.
     */
    public MutableMatchableItem(ItemStackMatcher matcher) {
        super(matcher);
    }

    /**
     * Set the {@link ItemStack} and {@link ItemStackMatcher}.
     *
     * @param itemStack  The {@link ItemStack}.
     * @param matcher    The {@link ItemStackMatcher}.
     */
    public void setItem(ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher);

        setItem(itemStack);
        setMatcher(matcher);
    }

    @Override
    public void setItem(ItemStack itemStack) {
        super.setItem(itemStack);
    }

    @Override
    public void setMatcher(ItemStackMatcher matcher) {
        super.setMatcher(matcher);
    }
}
