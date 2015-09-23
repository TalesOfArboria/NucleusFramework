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

import com.jcwhatever.nucleus.internal.NucLang;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of ItemStacks that can be used for validation/filtering of ItemStacks.
 */
public class ItemFilter implements IPluginOwned {

    @Localizable static final String _WHITELIST = "Whitelist";
    @Localizable static final String _BLACKLIST = "Blacklist";

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final ItemStackMatcher _matcher;
    private final Map<MatchableItem, MatchableItem> _filterItems = new HashMap<>(6 * 9);

    private FilterPolicy _filter = FilterPolicy.WHITELIST;

    /**
     * Validation filtering policy.
     */
    public enum FilterPolicy {
        WHITELIST,
        BLACKLIST;

        public String getDisplayName() {
            switch (this) {
                case WHITELIST:
                    return NucLang.get(_WHITELIST).toString();
                case BLACKLIST:
                    return NucLang.get(_BLACKLIST).toString();
                default:
                    return super.toString();
            }
        }

        @Override
        public String toString() {
            return getDisplayName();
        }
    }

    /**
     * Constructor.
     *
     * <p>Uses default {@link ItemStackMatcher}.</p>
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Data node to save and load settings.
     */
    public ItemFilter(Plugin plugin, @Nullable IDataNode dataNode) {
        this(plugin, dataNode, ItemStackMatcher.getDefault());
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The data node to save and load settings from.
     * @param matcher   The {@link ItemStackMatcher} to use to match items.
     */
    public ItemFilter(Plugin plugin, @Nullable IDataNode dataNode, ItemStackMatcher matcher) {
        PreCon.notNull(plugin);
        PreCon.notNull(matcher);

        _plugin = plugin;
        _dataNode = dataNode;
        _matcher = matcher ;

        load();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the filter policy used for validating items.
     */
    public FilterPolicy getPolicy() {
        return _filter;
    }

    /**
     * Set the filter policy used for validation items.
     *
     * @param filter  The filter policy.
     */
    public void setPolicy(FilterPolicy filter) {
        PreCon.notNull(filter);

        _filter = filter;

        if (_dataNode != null) {
            _dataNode.set("policy", filter);
            _dataNode.save();
        }
    }

    /**
     * Get the {@link ItemStackMatcher} used to match items.
     */
    public ItemStackMatcher getItemStackMatcher() {
        return _matcher;
    }

    /**
     * Determine if the specified {@link org.bukkit.inventory.ItemStack} is valid.
     *
     * <p>Checks to see if the collection contains a matching {@link org.bukkit.inventory.ItemStack} as
     * determined by the collections {@link ItemStackMatcher} and the filter policy.</p>
     *
     * @param item  The item to check.
     */
    public boolean isValid(ItemStack item) {
        PreCon.notNull(item);

        MatchableItem wrapper = _filterItems.get(new MatchableItem(item, _matcher));
        if (wrapper == null)
            return _filter == FilterPolicy.BLACKLIST;

        return _filter == FilterPolicy.WHITELIST;
    }

    /**
     * Get the set of matchable items from the collection.
     */
    public Collection<MatchableItem> getMatchable() {
        return CollectionUtils.unmodifiableSet(_filterItems.keySet());
    }

    /**
     * Get the set of matchable items from the collection.
     */
    public <T extends Collection<MatchableItem>> T getMatchable(T output) {
        PreCon.notNull(output);

        output.addAll(_filterItems.keySet());
        return output;
    }

    /**
     * Add an item to the collection.
     *
     * @param itemStack  The item to add.
     */
    public boolean add(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        MatchableItem wrapper = new MatchableItem(itemStack, _matcher);
        if (_filterItems.put(wrapper, wrapper) != null) {
            save();
            return true;
        }
        return false;
    }

    /**
     * Add multiple items to the collection.
     *
     * @param itemStacks  The items to add.
     */
    public boolean add(ItemStack[] itemStacks) {
        PreCon.notNull(itemStacks);

        for (ItemStack stack : itemStacks) {
            MatchableItem wrapper = new MatchableItem(stack, _matcher);
            _filterItems.put(wrapper, wrapper);
        }

        save();

        return true;
    }

    /**
     * Remove an item from the collection.
     *
     * @param itemStack  The item to remove.
     */
    public boolean remove(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        if (_filterItems.remove(new MatchableItem(itemStack, _matcher)) != null) {
            save();
            return true;
        }
        return false;
    }

    /**
     * Remove multiple items from the collection.
     *
     * @param itemStacks  The items to remove.
     */
    public boolean remove(ItemStack[] itemStacks) {
        PreCon.notNull(itemStacks);

        for (ItemStack stack : itemStacks) {
            _filterItems.remove(new MatchableItem(stack, _matcher));
        }

        save();

        return true;
    }

    /**
     * Remove all items from the collection.
     */
    public boolean clear() {
        _filterItems.clear();

        if (_dataNode != null) {
            _dataNode.set("items", null);
            _dataNode.save();
        }
        return true;
    }

    private void save() {
        if (_dataNode == null)
            return;

        Collection<MatchableItem> wrappers = _filterItems.values();

        List<ItemStack> stacks = new ArrayList<ItemStack>(wrappers.size());

        for (MatchableItem wrapper : wrappers) {
            stacks.add(wrapper.getItem());
        }

        _dataNode.set("items", stacks.toArray(new ItemStack[stacks.size()]));
        _dataNode.save();
    }

    private void load() {
        if (_dataNode == null)
            return;

        _filter = _dataNode.getEnum("policy", _filter, FilterPolicy.class);

        ItemStack[] craftItems = _dataNode.getItemStacks("items");

        _filterItems.clear();
        if (craftItems != null && craftItems.length > 0) {
            for (ItemStack stack : craftItems) {
                MatchableItem wrapper = new MatchableItem(stack, _matcher);
                _filterItems.put(wrapper, wrapper);
            }
        }
    }
}
