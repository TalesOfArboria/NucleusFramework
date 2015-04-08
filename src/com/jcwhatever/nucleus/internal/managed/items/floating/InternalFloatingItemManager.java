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

package com.jcwhatever.nucleus.internal.managed.items.floating;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItem;
import com.jcwhatever.nucleus.managed.items.floating.IFloatingItemManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Manages floating items.
 */
public final class InternalFloatingItemManager implements IFloatingItemManager, Listener {

    private final Map<Plugin, FloatingItemContext> _contexts = new WeakHashMap<>(25);

    public InternalFloatingItemManager() {
        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    @Nullable
    public IFloatingItem add(Plugin plugin, String name, ItemStack itemStack) {
        return add(plugin, name, itemStack, null);
    }

    @Override
    @Nullable
    public IFloatingItem add(Plugin plugin, String name, ItemStack itemStack,
                             @Nullable Location location) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(itemStack);

        return context(plugin).add(name, itemStack, location);
    }

    @Override
    public boolean contains(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        return context(plugin).contains(name);
    }

    @Nullable
    @Override
    public IFloatingItem get(Plugin plugin, String name) {
        PreCon.notNull(name);
        PreCon.notNullOrEmpty(name);

        return context(plugin).get(name);
    }

    @Override
    public Collection<IFloatingItem> getAll(Plugin plugin) {
        PreCon.notNull(plugin);

        return context(plugin).getAll();
    }

    @Override
    public boolean remove(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        FloatingItemContext context = _contexts.get(plugin);
        return context != null && context.remove(name);
    }

    private FloatingItemContext context(Plugin plugin) {

        FloatingItemContext context = _contexts.get(plugin);
        if (context == null) {

            IDataNode dataNode = DataStorage.get(plugin, new DataPath("nucleus.floating-items"));
            dataNode.load();

            context = new FloatingItemContext(plugin, dataNode);
            _contexts.put(plugin, context);
        }

        return context;
    }
}

