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
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * NPC Trait provider. Represents a single type of trait. Used to create
 * instances of the trait type for specific {@link INpc} instances.
 */
public abstract class NpcTraitType implements INamed, IPluginOwned {

    private String _lookupName;

    /**
     * Get the trait types lookup name.
     *
     * <p>This is the name used to reference the trait.</p>
     */
    public final String getLookupName() {

        if (_lookupName == null) {

            _lookupName = getPlugin() == Nucleus.getPlugin()
                    ? getName()
                    : getPlugin().getName() + ':' + getName();
        }

        return _lookupName;
    }

    /**
     * Create a new instance of the trait for a specific
     * {@link INpc} instance and attach it.
     *
     * @param npc  The npc instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     */
    public NpcTrait attachTrait(INpc npc) {
        return attachTrait(npc, null);
    }

    /**
     * Create a new instance of the trait for a specific {@link INpc}
     * instance, copy the settings from another trait, and attach it.
     *
     * @param npc       The npc instance.
     * @param copyFrom  The {@link INpc} to copy settings from into the new trait instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     */
    public NpcTrait attachTrait(INpc npc, @Nullable INpc copyFrom) {
        PreCon.notNull(npc);

        if (npc.getRegistry().get(getName()) == null)
            npc.getRegistry().registerTrait(this);

        NpcTrait trait = createTrait(npc);

        INpcTraits traits = copyFrom != null
                ? copyFrom.getTraits()
                : npc.getTraits();

        trait.load(traits.getTraitNode(trait));

        npc.getTraits().add(trait);

        return trait;
    }

    /**
     * Invoked to create a new instance of the trait for an {@link INpc}.
     *
     * @param npc  The {@link INpc} to create the trait for.
     */
    protected abstract NpcTrait createTrait(INpc npc);
}
