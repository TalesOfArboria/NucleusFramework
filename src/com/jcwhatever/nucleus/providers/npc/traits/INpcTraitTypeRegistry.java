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

import javax.annotation.Nullable;

/**
 * An interface for a type that allows registering trait types.
 */
public interface INpcTraitTypeRegistry {

    /**
     * Register a trait type that can be used by NPC's owned by the
     * trait registry implementation and any owned child registries.
     *
     * @param traitType  The trait type to register.
     *
     * @return  Self for chaining.
     */
    INpcTraitTypeRegistry registerTrait(INpcTraitType traitType);

    /**
     * Determine if a trait is registered.
     *
     * @param name  The name of the trait. The trait name is the
     *              name of the plugin that owns the trait followed by a
     *              colon and the trait name. (i.e. MyPlugin:MyTrait). If the
     *              trait is owned by NucleusFramework, the name is simply the trait name.
     */
    boolean isTraitRegistered(String name);

    /**
     * Get a registered trait type.
     *
     * @param name  The name of the trait. The trait name is the
     *              name of the plugin that owns the trait followed by a
     *              colon and the trait name. (i.e. MyPlugin:MyTrait). If the
     *              trait is owned by NucleusFramework, the name is simply the trait name.
     *
     * @return  The trait type or null if not found.
     */
    @Nullable
    INpcTraitType getTraitType(String name);
}
