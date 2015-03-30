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
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;

/**
 * Interface for an object that accepts script event subscribers.
 *
 * <p>The implementing type represents the scope of the subscription. For example,
 * a subscriber attached to a {@link INpcRegistry} will receive updates
 * for all NPC's created by the registry whereas a subscriber attached to an
 * {@link INpc} will receive updates only for the NPC it is attached to and only
 * for the lifetime of the NPC.</p>
 */
public interface INpcScriptEvents {

    /**
     * Attach a subscriber to be updated when an {@link INpc} is spawned.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcSpawn(IScriptUpdateSubscriber<NpcSpawnEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is despawned.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcDespawn(IScriptUpdateSubscriber<NpcDespawnEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcClick(IScriptUpdateSubscriber<NpcClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is right clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcRightClick(IScriptUpdateSubscriber<NpcRightClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is left clicked.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcLeftClick(IScriptUpdateSubscriber<NpcLeftClickEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is targeted by another entity.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcEntityTarget(IScriptUpdateSubscriber<NpcTargetedEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is damaged.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcDamage(IScriptUpdateSubscriber<NpcDamageEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is damaged by a block.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcDamageByBlock(IScriptUpdateSubscriber<NpcDamageByBlockEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} is damaged by an entity.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcDamageByEntity(IScriptUpdateSubscriber<NpcDamageByEntityEvent> subscriber);

    /**
     * Attach a subscriber to be updated when an {@link INpc} dies.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    INpcScriptEvents onNpcDeath(IScriptUpdateSubscriber<NpcDeathEvent> subscriber);
}
