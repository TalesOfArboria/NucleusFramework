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

package com.jcwhatever.nucleus.utils.items.loremeta;

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/*
 * 
 */
public final class LoreMeta {

    private LoreMeta() {}

    public static LoreMetaMap get(ItemStack itemStack) {
        return new LoreMetaMap(itemStack);
    }

    public static LoreMetaMap get(ItemStack itemStack, ILoreMetaParser parser) {
        return new LoreMetaMap(itemStack, parser);
    }

    public static void prepend(ItemStack itemStack, LoreMetaItem... items) {
        prepend(itemStack, LoreMetaParser.get(), ArrayUtils.asList(items));
    }

    public static void prepend(ItemStack itemStack, ILoreMetaParser parser, LoreMetaItem... items) {
        prepend(itemStack, parser, ArrayUtils.asList(items));
    }

    public static void prepend(ItemStack itemStack, Collection<LoreMetaItem> items) {
        prepend(itemStack, LoreMetaParser.get(), items);
    }

    public static void prepend(ItemStack itemStack, ILoreMetaParser parser, Collection<LoreMetaItem> items) {

        LinkedList<String> lines = new LinkedList<>(ItemStackUtils.getLore(itemStack));
        List<LoreMetaItem> itemsList = items instanceof List
                ? (List<LoreMetaItem>)items
                : new ArrayList<>(items);

        removeItemLines(lines, parser, items);

        for (int i=itemsList.size() - 1; i >= 0; i--) {
            lines.addFirst(parser.getMetaDisplay(itemsList.get(i)));
        }

        ItemStackUtils.setLore(itemStack, lines);
    }

    public static void append(ItemStack itemStack, LoreMetaItem...items) {
        append(itemStack, LoreMetaParser.get(), ArrayUtils.asList(items));
    }

    public static void append(ItemStack itemStack, ILoreMetaParser parser, LoreMetaItem...items) {
        append(itemStack, parser, ArrayUtils.asList(items));
    }

    public static void append(ItemStack itemStack, Collection<LoreMetaItem> items) {
        append(itemStack, LoreMetaParser.get(), items);
    }

    public static void append(ItemStack itemStack, ILoreMetaParser parser, Collection<LoreMetaItem> items) {
        PreCon.notNull(itemStack);
        PreCon.notNull(parser);
        PreCon.notNull(items);

        List<String> lines = new ArrayList<>(ItemStackUtils.getLore(itemStack));

        removeItemLines(lines, parser, items);

        for (LoreMetaItem item : items)
            lines.add(parser.getMetaDisplay(item));

        ItemStackUtils.setLore(itemStack, lines);
    }

    public static Collection<LoreMetaItem> removeFrom(
            ItemStack itemStack, ILoreMetaParser parser, String... metaItemNames) {
        return removeFrom(itemStack, parser, ArrayUtils.asList(metaItemNames), metaItemNames.length == 0);
    }

    public static Collection<LoreMetaItem> removeFrom(
            ItemStack itemStack, ILoreMetaParser parser, Collection<String> metaItemNames) {
        PreCon.notNull(itemStack);
        PreCon.notNull(parser);
        PreCon.notNull(metaItemNames);

        return removeFrom(itemStack, parser, metaItemNames, false);
    }

    private static Collection<LoreMetaItem> removeFrom(
            ItemStack itemStack, ILoreMetaParser parser, Collection<String> metaItemNames, boolean removeAll) {
        PreCon.notNull(itemStack);
        PreCon.notNull(parser);
        PreCon.notNull(metaItemNames);

        List<String> lines = new ArrayList<>(ItemStackUtils.getLore(itemStack));
        List<LoreMetaItem> removed = new ArrayList<>(lines.size());

        removeLines(lines, removed, parser, metaItemNames, removeAll);

        if (!removed.isEmpty())
            ItemStackUtils.setLore(itemStack, lines);

        return removed;
    }

    private static void removeLines(List<String> lines, @Nullable Collection<LoreMetaItem> removed,
                                    ILoreMetaParser parser, Collection<String> names, boolean removeAll) {

        Iterator<String> iterator = lines.iterator();
        while(iterator.hasNext()) {

            String loreLine = iterator.next();

            LoreMetaItem metaItem = parser.parseLoreMeta(loreLine);
            if (metaItem != null && (removeAll || names.contains(metaItem.getName()))) {
                iterator.remove();

                if (removed != null)
                    removed.add(metaItem);
            }
        }
    }

    private static void removeItemLines(Collection<String> lines,
                                    ILoreMetaParser parser, Collection<LoreMetaItem> items) {

        Iterator<String> iterator = lines.iterator();
        while(iterator.hasNext()) {

            String loreLine = iterator.next();

            LoreMetaItem metaItem = parser.parseLoreMeta(loreLine);
            if (metaItem != null) {

                for (LoreMetaItem item : items) {
                    if (item.getName().equals(loreLine)) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }
}
