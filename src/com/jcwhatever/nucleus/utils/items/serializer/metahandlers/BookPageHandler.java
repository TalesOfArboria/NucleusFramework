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

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles a books page meta. Each page reference is a single page.
 * (i.e 3 pages require 3 "bookPage" parameters)
 */
public class BookPageHandler implements IMetaHandler {

    @Override
    public String getMetaName() {
        return "bookPage";
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemMeta meta = itemStack.getItemMeta();
        return meta instanceof BookMeta;
    }

    @Override
    public boolean apply(ItemStack itemStack, ItemMetaObject meta) {
        PreCon.notNull(itemStack);
        PreCon.notNull(meta);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof BookMeta))
            return false;

        BookMeta bookMeta = (BookMeta)itemMeta;

        List<String> pages = bookMeta.getPages();

        List<String> newPages = pages == null
                ? new ArrayList<String>(5)
                : new ArrayList<String>(pages.size() + 1);

        if (pages != null) {
            for (String page : pages) {
                newPages.add(page);
            }
        }

        newPages.add(meta.getRawData());

        bookMeta.setPages(newPages);

        itemStack.setItemMeta(bookMeta);

        return true;
    }

    @Override
    public List<ItemMetaObject> getMeta(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof BookMeta))
            return new ArrayList<>(0);

        BookMeta bookMeta = (BookMeta)itemMeta;

        List<String> pages = bookMeta.getPages();

        if (pages == null || pages.isEmpty())
            return new ArrayList<>(0);

        List<ItemMetaObject> result = new ArrayList<>(pages.size());

        for (String page : pages) {
            result.add(new ItemMetaObject(getMetaName(), page));
        }

        itemStack.setItemMeta(bookMeta);

        return result;
    }
}
