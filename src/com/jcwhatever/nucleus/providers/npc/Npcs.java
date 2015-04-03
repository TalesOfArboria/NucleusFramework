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

package com.jcwhatever.nucleus.providers.npc;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Static convenience methods for accessing the NPC provider.
 */
public class Npcs {

    private Npcs() {
    }

    /**
     * Determine if an NPC provider is installed.
     */
    public static boolean hasProvider() {
        return Nucleus.getProviders().getNpcs() != null;
    }

    /**
     * Create a transient registry.
     *
     * @param plugin The owning plugin.
     * @param name   The name of the registry unique to the owning plugin.
     *
     * @return The new or existing registry or null if failed.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasProvider} returns false.
     */
    @Nullable
    public static INpcRegistry createRegistry(Plugin plugin, String name) {
        return provider().createRegistry(plugin, name);
    }

    /**
     * Determine if an entity is an NPC.
     *
     * <p>Can be used even if no provider is installed.</p>
     *
     * @param entity The {@link org.bukkit.entity.Entity} to check.
     */
    public static boolean isNpc(Entity entity) {
        return entity.hasMetadata("NPC") || (hasProvider() && provider().isNpc(entity));
    }

    /**
     * Get an NPC from an entity. If the entity is an NPC from any registry
     * created by the provider, the entities {@link com.jcwhatever.nucleus.providers.npc.INpc} instance will
     * be returned.
     *
     * @param entity The possible NPC {@link org.bukkit.entity.Entity}.
     *
     * @return The NPC or null if the entity is not an NPC.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasProvider} returns false.
     */
    @Nullable
    public static INpc getNpc(Entity entity) {
        return provider().getNpc(entity);
    }

    /**
     * Get the installed NPC provider.
     *
     * @throws java.lang.UnsupportedOperationException if {@link #hasProvider} returns false.
     */
    public static INpcProvider provider() {
        PreCon.supported(hasProvider());

        return Nucleus.getProviders().getNpcs();
    }
}
