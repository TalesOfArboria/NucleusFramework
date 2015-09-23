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

package com.jcwhatever.nucleus.utils.text.format.args;

import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Modifies a formatter argument with click functionality which
 * gives items to the clicker.
 *
 * <p>Uses nucleus give command. Permissions required.</p>
 */
public class ClickGiveItemsArgModifier extends ClickableArgModifier {

    /**
     * Constructor.
     *
     * @param items  The item(s) to give when clicking.
     */
    public ClickGiveItemsArgModifier(ItemStack... items) {
        super(ClickAction.RUN_COMMAND, getArgument(items));
    }

    private static String getArgument(ItemStack[] items) {

        List<ItemStack> list = new ArrayList<>(items.length);
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;

            list.add(item);
        }

        String serialized = ItemStackUtils.serialize(list.toArray(new ItemStack[list.size()]));
        return "/nucleus give '" + serialized + '\'';
    }
}

