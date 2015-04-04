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

package com.jcwhatever.nucleus.internal.items.meta;

import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles {@link org.bukkit.inventory.ItemStack} display name meta.
 *
 * @see ItemMetaHandlers
 */
public final class DisplayNameHandler implements IMetaHandler {

    @Override
    public String getMetaName() {
        return "name";
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasDisplayName();
    }

    @Override
    public boolean apply(ItemStack itemStack, ItemMetaValue meta) {
        PreCon.notNull(itemStack);
        PreCon.notNull(meta);

        if (!meta.getName().equals(getMetaName()))
            return false;

        ItemStackUtils.setDisplayName(itemStack, meta.getRawData());

        return true;
    }

    @Override
    public List<ItemMetaValue> getMeta(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.hasDisplayName())
            return new ArrayList<>(0);

        List<ItemMetaValue> result = new ArrayList<>(1);

        result.add(new ItemMetaValue(getMetaName(), meta.getDisplayName()));

        return result;
    }
}
