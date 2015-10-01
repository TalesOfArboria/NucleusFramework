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

package com.jcwhatever.nucleus.utils.nms;

import com.jcwhatever.nucleus.utils.coords.IVector3D;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Interface for NucleusFramework's Minecraft entity handler.
 */
public interface INmsEntityHandler extends INmsHandler {

    /**
     * Determine if an entity is visible.
     *
     * @param entity  The entity to check.
     */
    boolean isVisible(Entity entity);

    /**
     * Set an entities visibility state.
     *
     * @param entity     The entity to set.
     * @param isVisible  True to make the entity visible to players, otherwise false.
     */
    void setVisible(Entity entity, boolean isVisible);

    /**
     * Copy the entity velocity into the specified output vector.
     *
     * @param entity  The entity.
     * @param output  The output vector.
     *
     * @return  The output vector.
     */
    Vector getVelocity(Entity entity, Vector output);

    /**
     * Copy the entity velocity into the specified output vector.
     *
     * @param entity  The entity.
     * @param output  The output vector.
     *
     * @return  The output vector.
     */
    <T extends IVector3D> T getVelocity(Entity entity, T output);
}
