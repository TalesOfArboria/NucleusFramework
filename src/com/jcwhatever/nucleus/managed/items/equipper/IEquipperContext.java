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

package com.jcwhatever.nucleus.managed.items.equipper;

import com.jcwhatever.nucleus.mixins.IPluginOwned;

import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;

/**
 * An entity item equipper that is separate from the global equipper context.
 */
public interface IEquipperContext extends IPluginOwned {

    /**
     * Set the equipper to use when an equipper is not set for an entity type.
     *
     * @param equipper  The {@link IEquipper} to use.
     */
    void setDefaultEquipper(IEquipper equipper);

    /**
     * Set equipper used for the specified entity type..
     *
     * @param type      The entity type the equipper is for.
     * @param equipper  The equipper or null to remove.
     */
    void setEquipper(EntityType type, @Nullable IEquipper equipper);

    /**
     * Remove equipper from the specified entity type.
     *
     * <p>The default equipper is used.</p>
     *
     * @param type  The entity type the equipper is for.
     */
    void clearEquipper(EntityType type);

    /**
     * Get an equipper for the specified entity type.
     *
     * @param type  The entity type.
     *
     * @return  The entity type specific equipper or the default equipper.
     */
    IEquipper getEquipper(EntityType type);
}
