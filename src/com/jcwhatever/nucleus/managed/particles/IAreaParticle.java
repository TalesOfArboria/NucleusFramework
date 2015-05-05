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

package com.jcwhatever.nucleus.managed.particles;

/**
 * Mixin for a particle than has a setting for the size of the area to
 * spawn particles in when more than 1 particle count is spawned.
 *
 * <p>The area values are radius values.</p>
 */
public interface IAreaParticle {

    /**
     * Get the area size on the X axis.
     */
    double getXArea();

    /**
     * Get the area size on the Y axis.
     */
    double getYArea();

    /**
     * Get the area size on the Z axis.
     */
    double getZArea();

    /**
     * Set the area size on the X axis.
     *
     * @param areaSize  The area size.
     */
    void setXArea(double areaSize);

    /**
     * Set the area size on the Y axis.
     *
     * @param areaSize  The area size.
     */
    void setYArea(double areaSize);

    /**
     * Set the area size on the Z axis.
     *
     * @param areaSize  The area size.
     */
    void setZArea(double areaSize);

    /**
     * Set the area size of the X, Y and Z axis.
     *
     * @param areaSize  The area size.
     */
    void setArea(double areaSize);
}
