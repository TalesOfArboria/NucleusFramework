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

package com.jcwhatever.nucleus.utils.items.serializer.metahandlers;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Interface for an implementation that can apply and retrieve
 * specific meta from an {@link org.bukkit.inventory.ItemStack} for use by a
 * serializer/deserializer.
 *
 * @see ItemMetaHandlers
 */
public interface IMetaHandler {

    /**
     * Get the name of the meta that is handled.
     */
    String getMetaName();

    /**
     * Determine if the meta handler can get meta from the
     * item stack.
     *
     * @param itemStack  The item stack to check.
     */
    boolean canHandle(ItemStack itemStack);

    /**
     * Apply the meta to the item stack. The name of the meta must
     * match the handler meta name.
     *
     * @param itemStack  The item stack to apply meta to.
     * @param meta       The meta to apply.
     *
     * @return  True if successful.
     */
    boolean apply(ItemStack itemStack, ItemMetaValue meta);

    /**
     * Get the item meta values for the item stack.
     *
     * <p>If the meta handler supports more than 1 meta entry of itself,
     * multiple values can be returned.</p>
     *
     * @param itemStack  The item stack to get meta from.
     *
     * @return  The meta values. Empty if unable to retrieve meta.
     */
    List<ItemMetaValue> getMeta(ItemStack itemStack);
}
