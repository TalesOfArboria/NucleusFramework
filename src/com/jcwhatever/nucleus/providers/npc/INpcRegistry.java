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
import com.jcwhatever.nucleus.providers.npc.events.NpcClickEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDamageByBlockEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDamageByEntityEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDamageEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDeathEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcLeftClickEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcRightClickEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcTargetedEvent;
import com.jcwhatever.nucleus.providers.npc.traits.INpcTraitTypeRegistry;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for an NPC registry.
 */
public interface INpcRegistry extends INpcTraitTypeRegistry, IPluginOwned,
        INamedInsensitive, IDisposable {

    /**
     * Create a new NPC.
     *
     * @param name     The name unique to the owning plugin.
     * @param npcName  The NPC's display name.
     * @param type     The NPC entity type.
     *
     * @return  The new {@code INpc} instance or null if failed.
     */
    @Nullable
    INpc create(String name, String npcName, EntityType type);

    /**
     * Get all un-disposed {@code INpc}'s in the registry.
     */
    Collection<INpc> all();

    /**
     * Get an {@code INpc} from the registry by its unique name.
     *
     * @param name  The unique name.
     *
     * @return  The {@code INpc} instance or null if not found.
     */
    @Nullable
    INpc get(String name);

    /**
     * Get an {@code INpc} from the registry by its spawned
     * {@code Entity} instance.
     *
     * @param entity  The entity to check.
     *
     * @return  The {@code INpc} instance or null if not found.
     */
    @Nullable
    INpc get(Entity entity);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry starts navigating.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNavStart(IUpdateSubscriber<INpc> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry has its navigation paused.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNavPause(IUpdateSubscriber<INpc> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry cancels navigation.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNavCancel(IUpdateSubscriber<INpc> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry completes navigation.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNavComplete(IUpdateSubscriber<INpc> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry gets stuck during navigation and times out.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNavTimeout(IUpdateSubscriber<INpc> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is spawned.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcSpawn(IUpdateSubscriber<NpcSpawnEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is despawned.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcDespawn(IUpdateSubscriber<NpcDespawnEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcClick(IUpdateSubscriber<NpcClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is right clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcRightClick(IUpdateSubscriber<NpcRightClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is left clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcLeftClick(IUpdateSubscriber<NpcLeftClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is targeted by another entity.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcEntityTarget(IUpdateSubscriber<NpcTargetedEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is damaged.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcDamage(IUpdateSubscriber<NpcDamageEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is damaged by a block.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcDamageByBlock(IUpdateSubscriber<NpcDamageByBlockEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry is damaged by an entity.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcDamageByEntity(IUpdateSubscriber<NpcDamageByEntityEvent> subscriber);

    /**
     * Attach a subscriber to be updated whenever an {@code INpc} created
     * by the registry dies.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcRegistry onNpcDeath(IUpdateSubscriber<NpcDeathEvent> subscriber);
}
