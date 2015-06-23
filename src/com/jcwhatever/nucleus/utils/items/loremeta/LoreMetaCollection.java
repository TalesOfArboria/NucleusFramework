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

import com.jcwhatever.nucleus.collections.wrap.CollectionWrapper;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of {@link LoreMetaItem}.
 */
public class LoreMetaCollection extends CollectionWrapper<LoreMetaItem> {

    private final Map<String, LoreMetaItem> _map;

    /**
     * Constructor.
     */
    public LoreMetaCollection() {
        _map = new HashMap<>(7);
    }

    /**
     * Constructor.
     *
     * @param items  The collection of meta items to initialize with.
     */
    public LoreMetaCollection(Collection<? extends LoreMetaItem> items) {
        PreCon.notNull(items);

        _map = new HashMap<>(items.size() + (int)(items.size() * 0.25D));

        for (LoreMetaItem item : items)
            _map.put(item.getName(), item);
    }

    /**
     * Constructor.
     *
     * @param itemStack  The item stack to parse meta from.
     */
    public LoreMetaCollection(ItemStack itemStack) {
        this(itemStack, LoreMetaParser.get());
    }

    /**
     * Constructor.
     *
     * @param itemStack  The item stack to parse meta from.
     * @param parser     The parser to use.
     */
    public LoreMetaCollection(ItemStack itemStack, ILoreMetaParser parser) {
        PreCon.notNull(itemStack);
        PreCon.notNull(parser);

        List<String> lore = ItemStackUtils.getLore(itemStack);
        _map = new HashMap<>(lore.size() + 10);

        for (String loreLine : lore) {
            LoreMetaItem item = parser.parseLoreMeta(loreLine);
            if (item != null)
                _map.put(item.getName(), item);
        }
    }

    /**
     * Add a new meta item to the collection.
     *
     * <p>Replaces existing value.</p>
     *
     * @param name   The name of the meta.
     * @param value  The meta value.
     *
     * @return  The newly created {@link LoreMetaItem} instance.
     */
    public LoreMetaItem add(String name, String value) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(value);

        LoreMetaItem item = new LoreMetaItem(name, value);
        _map.put(name, item);

        return item;
    }

    /**
     * Set the value of a meta item.
     *
     * <p>Replaces existing value. Adds new value if not found.</p>
     *
     * @param name   The name of the meta.
     * @param value  The meta value.
     *
     * @return  Self for chaining.
     */
    public LoreMetaCollection set(String name, String value) {
        add(name, value);
        return this;
    }

    /**
     * Remove a meta value by name.
     *
     * @param name  The name of the meta.
     *
     * @return  The removed meta item or null if not found.
     */
    @Nullable
    public LoreMetaItem remove(String name) {
        PreCon.notNull(name);

        return _map.remove(name);
    }

    /**
     * Get a meta item by name.
     *
     * @param name  The name of the meta.
     *
     * @return  The meta item or null if not found.
     */
    @Nullable
    public LoreMetaItem get(String name) {
        PreCon.notNull(name);

        return _map.get(name);
    }

    /**
     * Append meta in the collection to an item stacks lore text.
     *
     * @param itemStack  The item stack.
     */
    public void appendTo(ItemStack itemStack) {
        appendTo(itemStack, LoreMetaParser.get());
    }

    /**
     * Append meta in the collection to an item stacks lore text.
     *
     * @param itemStack  The item stack.
     * @param parser     The parser to use.
     */
    public void appendTo(ItemStack itemStack, ILoreMetaParser parser) {
        PreCon.notNull(itemStack);

        LoreMeta.append(itemStack, parser, this);
    }

    /**
     * Prepend meta in the collection to an item stacks lore text.
     *
     * @param itemStack  The item stack.
     */
    public void prependTo(ItemStack itemStack) {
        prependTo(itemStack, LoreMetaParser.get());
    }

    /**
     * Prepend meta in the collection to an item stacks lore text.
     *
     * @param itemStack  The item stack.
     * @param parser     The parser to use.
     */
    public void prependTo(ItemStack itemStack, ILoreMetaParser parser) {
        PreCon.notNull(itemStack);

        LoreMeta.prepend(itemStack, parser, this);
    }

    @Override
    protected Collection<LoreMetaItem> collection() {
        return _map.values();
    }
}
