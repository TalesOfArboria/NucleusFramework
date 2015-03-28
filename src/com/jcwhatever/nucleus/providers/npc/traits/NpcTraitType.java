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

package com.jcwhatever.nucleus.providers.npc.traits;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent.NpcDespawnReason;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * NPC Trait provider. Represents a single type of trait. Used to create
 * instances of the trait type for specific {@link INpc} instances.
 */
public abstract class NpcTraitType implements INamed, IPluginOwned {

    private final Plugin _plugin;
    private final String _name;
    private final String _lookupName;

    private NpcTraitRegistration _registration;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the trait.
     */
    public NpcTraitType(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _plugin = plugin;
        _name = name;
        _lookupName = getPlugin() == Nucleus.getPlugin()
                ? getName()
                : getPlugin().getName() + ':' + getName();
    }

    @Override
    public final Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public final String getName() {
        return _name;
    }

    /**
     * Get the trait types lookup name.
     *
     * <p>This is the name used to reference the trait.</p>
     */
    public final String getLookupName() {
        return _lookupName;
    }

    /**
     * Create a new instance of the trait for a specific
     * {@link INpc} instance and attach it.
     *
     * <p>Attempts to add a pooled trait from the provider. If one is not available or
     * the provider does not implement trait pooling, a new trait is created by invoking
     * {@link #createTrait}.</p>
     *
     * @param npc  The npc instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     */
    public NpcTrait addToNpc(INpc npc) {
        PreCon.notNull(npc);

        if (npc.getRegistry().get(getName()) == null)
            npc.getRegistry().registerTrait(this);

        // add pooled trait
        NpcTrait trait = _registration.addPooled(this, npc);
        if (trait == null) {
            // create new trait
            trait = createTrait(npc);
            npc.getTraits().add(trait);
        }

        return trait;
    }

    /**
     * Determine if the trait is registered with the NPC provider.
     */
    public final boolean isRegistered() {
        return _registration != null;
    }

    /**
     * Invoked by the NPC provider when the trait is registered.
     *
     * <p>For NPC provider use only.</p>
     *
     * @param registration  The providers registration.
     */
    public final void onRegister(NpcTraitRegistration registration) {
        PreCon.notNull(registration);
        PreCon.isValid(_registration == null, "Trait already registered.");

        _registration = registration;
    }

    /**
     * Invoked to create a new instance of the trait for an {@link INpc}.
     *
     * @param npc  The {@link INpc} to initially create the trait for. A reference to the argument
     *             should not be held in the returned NpcTrait. It should only be used to make initial
     *             trait setup decisions. Use the traits {@link NpcTrait#getNpc} method to get the
     *             traits NPC or the {@link INpc} argument in the {@link NpcTrait#onAdd} method.
     */
    protected abstract NpcTrait createTrait(INpc npc);

    /**
     * Used by the provider to access protected trait methods.
     */
    public static abstract class NpcTraitRegistration {

        /**
         * Invoked by the provider when the trait is added to an NPC.
         *
         * @param npc    The NPC.
         * @param trait  The trait being added.
         */
        public void onAdd(INpc npc, NpcTrait trait) {
            PreCon.notNull(npc);
            PreCon.notNull(trait);

            checkRegistration(trait);
            trait.init(npc);
        }

        /**
         * Invoked by the provider when the trait is removed from an NPC.
         *
         * @param trait  The trait being removed.
         */
        public void onRemove(NpcTrait trait) {
            PreCon.notNull(trait);

            checkRegistration(trait);
            trait.onRemove();
        }

        /**
         * Invoked by the provider when the NPC the trait is added to is spawned.
         *
         * @param trait   The trait.
         * @param reason  The reason the NPC is spawning.
         */
        public void onSpawn(NpcTrait trait, NpcSpawnReason reason) {
            PreCon.notNull(reason);

            checkRegistration(trait);
            trait.onSpawn(reason);
        }

        /**
         * Invoked by the provider when the NPC the trait is added to is despawned.
         *
         * @param trait   The trait.
         * @param reason  The reason the NPC is despawning.
         */
        public void onDespawn(NpcTrait trait, NpcDespawnReason reason) {
            PreCon.notNull(trait);
            PreCon.notNull(reason);

            checkRegistration(trait);
            trait.onDespawn(reason);
        }

        /**
         * Invoked to add a pooled {@link NpcTrait} instance from the NPC provider to
         * the specified {@link INpc}.
         *
         * @param type  The trait type of the required trait instance.
         * @param npc   The npc to add the trait to.
         *
         * @return  The added {@link NpcTrait} instance or null if type was not found or
         * NPC provider does not implement trait pooling.
         */
        @Nullable
        protected abstract NpcTrait addPooled(NpcTraitType type, INpc npc);

        private void checkRegistration(NpcTrait trait) {

            NpcTraitRegistration registration = trait.getType()._registration;
            if (registration != null && registration != this)
                throw new IllegalStateException("Registration mismatch.");
        }
    }
}
