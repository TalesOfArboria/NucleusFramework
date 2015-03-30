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

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.npc.navigator.INpcNavScriptEvents;
import com.jcwhatever.nucleus.providers.npc.traits.INpcTraitTypeRegistry;
import com.jcwhatever.nucleus.storage.IDataNode;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for an NPC registry.
 */
public interface INpcRegistry extends INpcTraitTypeRegistry, INpcScriptEvents, INpcNavScriptEvents,
        IPluginOwned, INamedInsensitive, IDisposable {

    /**
     * Create a new NPC.
     *
     * @param lookupName  The lookup name unique to the owning plugin.
     * @param npcName     The NPC's display name.
     * @param type        The NPC entity type.
     *
     * @return  The new {@link INpc} instance or null if failed.
     */
    @Nullable
    INpc create(String lookupName, String npcName, EntityType type);

    /**
     * Create a new NPC.
     *
     * @param lookupName  The lookup name unique to the owning plugin.
     * @param npcName     The NPC's display name.
     * @param type        The NPC {@link EntityType} name.
     *
     * @return  The new {@link INpc} instance or null if failed.
     */
    @Nullable
    INpc create(String lookupName, String npcName, String type);

    /**
     * Create a new NPC without a lookup name.
     *
     * @param npcName  The NPC's display name.
     * @param type     The NPC entity type.
     *
     * @return  The new {@link INpc} instance or null if failed.
     */
    @Nullable
    INpc create(String npcName, EntityType type);

    /**
     * Create a new NPC without a lookup name.
     *
     * @param npcName  The NPC's display name.
     * @param type     The NPC {@link EntityType} name.
     *
     * @return  The new {@link INpc} instance or null if failed.
     */
    @Nullable
    INpc create(String npcName, String type);

    /**
     * Load an {@link INpc} from a data node.
     *
     * @param dataNode  The data node to load the NPC from.
     *
     * @return  The loaded {@link INpc} or null if failed.
     */
    @Nullable
    INpc load(IDataNode dataNode);

    /**
     * Load all {@link INpc}'s from a data node.
     *
     * @param dataNode  The data node to load from.
     *
     * @return  True if successful, otherwise false.
     */
    boolean loadAll(IDataNode dataNode);

    /**
     * Save all {@link INpc}'s to a data node.
     *
     * @param dataNode  The data node to save to.
     *
     * @return  True if successful, otherwise false.
     */
    boolean saveAll(IDataNode dataNode);

    /**
     * Get all un-disposed {@link INpc}'s in the registry.
     *
     * <p>The collection returned does not modify the registry.</p>
     */
    Collection<INpc> all();

    /**
     * Get an {@link INpc} from the registry by its lookup name.
     *
     * @param name  The lookup name.
     *
     * @return  The {@link INpc} instance or null if not found.
     */
    @Nullable
    INpc get(String name);

    /**
     * Get an {@link INpc} from the registry by its spawned {@link Entity} instance.
     *
     * @param entity  The entity to check.
     *
     * @return  The {@link INpc} instance or null if not found or the entity NPC is not
     * from the registry.
     */
    @Nullable
    INpc get(Entity entity);
}
