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

package com.jcwhatever.nucleus.utils.npc.traits;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.plugin.Plugin;

/*
 * 
 */
public class SimpleWaypointsTraitType  extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return Nucleus.getPlugin();
    }

    @Override
    public String getName() {
        return "SimpleWaypoints";
    }

    @Override
    public String getSearchName() {
        return "simplewaypoints";
    }

    @Override
    public NpcTrait attachTrait(INpc npc) {

        if (npc.getRegistry().get("simplewaypoints") == null) {
            npc.getRegistry().registerTrait(this);
        }

        NpcTrait trait = new SimpleWaypointsTrait(npc, this);

        npc.getTraits().add(trait);

        return trait;
    }

    @Override
    public NpcTrait attachTrait(INpc npc, NpcTrait copyFrom) {

        // TODO copy

        return attachTrait(npc);
    }
}
