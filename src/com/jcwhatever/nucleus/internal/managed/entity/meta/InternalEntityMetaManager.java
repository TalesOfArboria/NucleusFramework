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

package com.jcwhatever.nucleus.internal.managed.entity.meta;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaContext;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaManager;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link IEntityMetaManager}.
 */
public class InternalEntityMetaManager implements IEntityMetaManager, Listener {

    private final Map<Plugin, EntityMetaContext> _contexts = new HashMap<>(35);

    /**
     * Constructor.
     */
    public InternalEntityMetaManager() {
        Bukkit.getPluginManager().registerEvents(this, Nucleus.getPlugin());
    }

    @Override
    public IEntityMetaContext getContext(Plugin plugin) {
        PreCon.notNull(plugin);

        EntityMetaContext context = _contexts.get(plugin);
        if (context == null) {
            context = new EntityMetaContext(plugin);
            _contexts.put(plugin, context);
        }

        return context;
    }

    @Override
    public void set(Plugin plugin, Entity entity, String key, @Nullable Object value) {
        PreCon.notNull(plugin);

        getContext(plugin).set(entity, key, value);
    }

    @Nullable
    @Override
    public <T> T get(Plugin plugin, Entity entity, String key) {
        PreCon.notNull(plugin);

        return getContext(plugin).get(entity, key);
    }

    @Override
    public boolean has(Plugin plugin, Entity entity, String key) {
        PreCon.notNull(plugin);

        return getContext(plugin).has(entity, key);
    }

    @Override
    public void copy(Plugin plugin, Entity source, Entity target) {
        PreCon.notNull(plugin);

        getContext(plugin).copy(source, target);
    }

    @Override
    public Map<String, Object> getAll(Plugin plugin, Entity entity) {
        PreCon.notNull(plugin);

        return getContext(plugin).getAll(entity);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPluginDisabled(PluginDisableEvent event) {
        _contexts.remove(event.getPlugin());
    }
}
