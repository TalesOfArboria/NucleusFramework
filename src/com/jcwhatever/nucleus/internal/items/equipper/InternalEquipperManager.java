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

package com.jcwhatever.nucleus.internal.items.equipper;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.items.equipper.handler.InternalDefaultEquipper;
import com.jcwhatever.nucleus.managed.items.equipper.IEquipper;
import com.jcwhatever.nucleus.managed.items.equipper.IEquipperContext;
import com.jcwhatever.nucleus.managed.items.equipper.IEquipperManager;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Internal implementation of {@link IEquipperManager}.
 */
public final class InternalEquipperManager implements IEquipperManager {

    private static final IEquipper DEFAULT_EQUIPPER = new InternalDefaultEquipper();

    private final Map<Plugin, IEquipperContext> _contexts = new WeakHashMap<>(10);
    private final IEquipperContext _globalContext =
            new InternalEquipperContext(Nucleus.getPlugin(), DEFAULT_EQUIPPER);

    @Override
    public IEquipperContext getContext(Plugin plugin) {
        PreCon.notNull(plugin, "plugin");

        IEquipperContext context = _contexts.get(plugin);

        if (context == null) {
            context = new InternalEquipperContext(plugin, DEFAULT_EQUIPPER);
            _contexts.put(plugin, context);
        }

        return context;
    }

    @Override
    public IEquipper getEquipper(EntityType type) {
        return _globalContext.getEquipper(type);
    }
}
