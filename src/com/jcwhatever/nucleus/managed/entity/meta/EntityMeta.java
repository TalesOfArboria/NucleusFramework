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

package com.jcwhatever.nucleus.managed.entity.meta;

import com.jcwhatever.nucleus.Nucleus;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Static convenience methods for interacting with the {@link IEntityMetaManager} implementation.
 */
public final class EntityMeta {

    private EntityMeta() {}

    /**
     * Get a meta context for the specified plugin.
     *
     * <p>Returns a cached context if one is already created.</p>
     *
     * @param plugin  The plugin the context is for.
     */
    public static IEntityMetaContext getContext(Plugin plugin) {
        return manager().getContext(plugin);
    }

    /**
     * Set an entities meta data.
     *
     * @param plugin  The plugin context.
     * @param entity  The entity.
     * @param key     The meta key.
     * @param value
     */
    public static void set(Plugin plugin, Entity entity, String key, @Nullable Object value) {
        manager().set(plugin, entity, key, value);
    }

    /**
     * Get meta data from an entity.
     *
     * @param plugin  The plugin context.
     * @param entity  The entity.
     * @param key     The meta key.
     */
    @Nullable
    public static <T> T get(Plugin plugin, Entity entity, String key) {
        return manager().get(plugin, entity, key);
    }

    /**
     * Determine if an entity has a meta key.
     *
     * @param plugin  The plugin context.
     * @param entity  The entity.
     * @param key     The meta key to check.
     */
    public static boolean has(Plugin plugin, Entity entity, String key) {
        return manager().has(plugin, entity, key);
    }

    /**
     * Copy meta from one entity to another.
     *
     * <p>Existing meta in the target entity is not touched unless there is a matching
     * key in the source entity, in which case, the source entity meta will overwrite
     * the target entity meta.</p>
     *
     * @param plugin  The plugin context.
     * @param source  The entity to copy meta data from.
     * @param target  The entity to copy meta data to.
     */
    public static void copy(Plugin plugin, Entity source, Entity target) {
        manager().copy(plugin, source, target);
    }

    /**
     * Get a new map of meta data stored in an entity.
     *
     * @param plugin  The plugin context.
     * @param entity  The entity to get meta data from.
     *
     * @return  A new map of meta data.
     */
    public static Map<String, Object> getAll(Plugin plugin, Entity entity) {
        return manager().getAll(plugin, entity);
    }

    private static IEntityMetaManager manager() {
        return Nucleus.getEntityMeta();
    }
}
