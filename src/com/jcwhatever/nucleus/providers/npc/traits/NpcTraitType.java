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

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * NPC Trait provider. Represents a single type of trait. Used to create
 * instances of the trait type for specific {@link INpc} instances.
 */
public abstract class NpcTraitType implements INamedInsensitive, IPluginOwned {

    /**
     * Create a new instance of the trait for a specific
     * {@code INpc} instance and attach it.
     *
     * @param npc  The npc instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     */
    public NpcTrait attachTrait(INpc npc) {
        PreCon.notNull(npc);

        if (npc.getRegistry().get(getName()) == null) {
            npc.getRegistry().registerTrait(this);
        }

        return npc.getTraits().add(getName());
    }

    /**
     * Create a new instance of the trait for a specific {@code INpc}
     * instance, copy the settings from another trait, and attach it.
     *
     * @param npc       The npc instance.
     * @param copyFrom  The {@code INpcTrait} instance to copy settings from into the new instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     *
     * @throws java.lang.IllegalArgumentException  if the {@code copyFrom} argument is invalid.
     */
    public abstract NpcTrait attachTrait(INpc npc, NpcTrait copyFrom);
}
