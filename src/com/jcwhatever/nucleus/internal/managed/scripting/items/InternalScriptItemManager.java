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

package com.jcwhatever.nucleus.internal.managed.scripting.items;

import com.jcwhatever.nucleus.managed.scripting.items.IScriptItem;
import com.jcwhatever.nucleus.managed.scripting.items.IScriptItemManager;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Manages {@link InternalScriptItem}'s
 */
public final class InternalScriptItemManager
        extends NamedInsensitiveDataManager<IScriptItem> implements IScriptItemManager {

    /**
     * Constructor.
     *
     * @param dataNode  The data node where {@link ItemStack} are stored.
     */
    public InternalScriptItemManager(IDataNode dataNode) {
        super(dataNode, true);
    }

    @Override
    @Nullable
    public IScriptItem add(String name, ItemStack item) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(item);

        if (contains(name))
            return null;

        InternalScriptItem scriptItem = new InternalScriptItem(name, item);

        add(scriptItem);

        return scriptItem;
    }

    @Nullable
    @Override
    protected InternalScriptItem load(String name, IDataNode itemNode) {

        ItemStack[] items = itemNode.getItemStacks("");
        if (items == null || items.length == 0)
            return null;

        return new InternalScriptItem(name, items[0]);
    }

    @Nullable
    @Override
    protected void save(IScriptItem item, IDataNode itemNode) {
        itemNode.set("", item.getItem());
    }
}
