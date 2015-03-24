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

import org.bukkit.plugin.Plugin;

/**
 * NPC Trait provider. Represents a single type of trait. Used to create
 * instances of the trait type for specific {@link INpc} instances.
 */
public abstract class NpcTraitType implements INamed, IPluginOwned {

    private final Plugin _plugin;
    private final String _name;
    private final String _lookupName;

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
     * @param npc  The npc instance.
     *
     * @return  The newly created instance or the one the NPC already had.
     */
    public NpcTrait attachTrait(INpc npc) {
        PreCon.notNull(npc);

        if (npc.getRegistry().get(getName()) == null)
            npc.getRegistry().registerTrait(this);

        NpcTrait trait = createTrait(npc);

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
