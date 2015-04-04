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

package com.jcwhatever.nucleus.internal.providers.kits;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.Provider;
import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.IKitContext;
import com.jcwhatever.nucleus.providers.kits.IKitProvider;
import com.jcwhatever.nucleus.providers.kits.IModifiableKit;
import com.jcwhatever.nucleus.storage.DataPath;
import com.jcwhatever.nucleus.providers.storage.DataStorage;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of a kit provider.
 */
public final class NucleusKitProvider extends Provider implements IKitProvider {

    public static final String NAME = "NucleusKits";

    private final NucleusKitContext _globalContext;
    private final Map<Plugin, NucleusKitContext> _contexts = new WeakHashMap<>(25);

    /**
     * Constructor.
     */
    public NucleusKitProvider() {

        IDataNode dataNode = DataStorage.get(Nucleus.getPlugin(), new DataPath("kits"));
        dataNode.load();

        _globalContext = new NucleusKitContext(Nucleus.getPlugin(), dataNode);
    }

    @Override
    public Plugin getPlugin() {
        return Nucleus.getPlugin();
    }

    @Override
    public IKitContext pluginContext(Plugin plugin) {
        PreCon.notNull(plugin);

        if (plugin.equals(_globalContext.getPlugin()))
            return _globalContext;

        NucleusKitContext context = _contexts.get(plugin);
        if (context == null) {

            IDataNode dataNode = DataStorage.get(plugin, new DataPath("nucleus.kits"));
            dataNode.load();

            context = new NucleusKitContext(plugin, dataNode);
            _contexts.put(plugin, context);
        }

        return context;
    }

    @Override
    public IKit add(String name) {
        return _globalContext.add(name);
    }

    @Override
    public IModifiableKit modifyKit(IKit kit) {
        return _globalContext.modifyKit(kit);
    }

    @Override
    public boolean contains(String name) {
        return _globalContext.contains(name);
    }

    @Nullable
    @Override
    public IKit get(String name) {
        return _globalContext.get(name);
    }

    @Override
    public Collection<IKit> getAll() {
        return _globalContext.getAll();
    }

    @Override
    public boolean remove(String name) {
        return _globalContext.remove(name);
    }
}
