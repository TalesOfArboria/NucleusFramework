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

import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.managed.entity.meta.IEntityMetaContext;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Implementation of {@link IEntityMetaContext}
 */
class EntityMetaContext implements IEntityMetaContext {

    private final Plugin _plugin;
    private final Map<Entity, Map<String, Object>> _metaMap = new WeakHashMap<>(35);
    private final Map<UUID, Map<String, Object>> _playerMetaMap;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    EntityMetaContext(Plugin plugin) {
        _plugin = plugin;
        _playerMetaMap = new PlayerMap<Map<String, Object>>(plugin, 10);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public void set(Entity entity, String key, @Nullable Object value) {
        PreCon.notNull(entity);
        PreCon.notNull(key);

        Map<String, Object> meta = entity instanceof Player
                ? _playerMetaMap.get(entity.getUniqueId())
                : _metaMap.get(entity);

        if (meta == null) {
            meta = new HashMap<>(10);
            _metaMap.put(entity, meta);
        }

        if (value == null) {
            meta.remove(key);
        }
        else {
            meta.put(key, value);
        }
    }

    @Nullable
    @Override
    public <T> T get(Entity entity, String key) {
        PreCon.notNull(entity);
        PreCon.notNull(key);

        Map<String, Object> meta = entity instanceof Player
                ? _playerMetaMap.get(entity.getUniqueId())
                : _metaMap.get(entity);

        if (meta == null)
            return null;

        @SuppressWarnings("unchecked")
        T result = (T)meta.get(key);

        return result;
    }

    @Override
    public boolean has(Entity entity, String key) {
        PreCon.notNull(entity);
        PreCon.notNull(key);

        Map<String, Object> meta = entity instanceof Player
                ? _playerMetaMap.get(entity.getUniqueId())
                : _metaMap.get(entity);

        return meta != null && meta.containsKey(key);
    }

    @Override
    public void copy(Entity source, Entity target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        Map<String, Object> sourceMeta = source instanceof Player
                ? _playerMetaMap.get(source.getUniqueId())
                : _metaMap.get(source);

        if (sourceMeta == null)
            return;

        Map<String, Object> targetMeta = target instanceof Player
                ? _playerMetaMap.get(target.getUniqueId())
                : _metaMap.get(target);

        if (targetMeta == null) {
            targetMeta = new HashMap<>(10);
            _metaMap.put(target, targetMeta);
        }

        targetMeta.putAll(sourceMeta);
    }

    @Override
    public Map<String, Object> getAll(Entity entity) {
        PreCon.notNull(entity);

        Map<String, Object> meta = entity instanceof Player
                ? _playerMetaMap.get(entity.getUniqueId())
                : _metaMap.get(entity);

        if (meta == null)
            return new HashMap<>(0);

        return new HashMap<>(meta);
    }
}
