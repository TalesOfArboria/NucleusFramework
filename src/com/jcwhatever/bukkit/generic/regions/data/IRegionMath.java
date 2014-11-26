/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.regions.data;

import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;

/**
 * Represents basic math for a region
 */
public interface IRegionMath {

    /**
     * Determine if the regions cuboid points have been set.
     */
    boolean isDefined();

    /**
     * Get the world the region is in.
     */
    @Nullable
    World getWorld();

    /**
     * Get the cuboid regions first point location.
     *
     * <p>Note: If the location is set but the world it's for is not
     * loaded, the World value of location may be null.</p>
     */
    Location getP1();

    /**
     * Get the cuboid regions seconds point location.
     *
     * <p>Note: If the location is set but the world it's for is not
     * loaded, the World value of location may be null.</p>
     */
    Location getP2();

    /**
     * Get the cuboid regions lower point location.
     */
    Location getLowerPoint();

    /**
     * Get the cuboid regions upper point location.
     */
    Location getUpperPoint();

    /**
     * Get the smallest X axis coordinates
     * of the region.
     */
    int getXStart();

    /**
     * Get the smallest Y axis coordinates
     * of the region.
     */
    int getYStart();

    /**
     * Get the smallest Z axis coordinates
     * of the region.
     */
    int getZStart();

    /**
     * Get the largest X axis coordinates
     * of the region.
     */
    int getXEnd();

    /**
     * Get the largest Y axis coordinates
     * of the region.
     */
    int getYEnd();

    /**
     * Get the largest Z axis coordinates
     * of the region.
     */
    int getZEnd();

    /**
     * Get the X axis width of the region.
     */
    int getXWidth();

    /**
     * Get the Z axis width of the region.
     */
    int getZWidth();

    /**
     * Get the Y axis height of the region.
     */
    int getYHeight();

    /**
     * Get the number of blocks that make up the width of the
     * region on the X axis.
     */
    int getXBlockWidth();

    /**
     * Get the number of blocks that make up the width of the
     * region on the Z axis.
     */
    int getZBlockWidth();

    /**
     * Get the number of blocks that make up the height of the
     * region on the Y axis.
     */
    int getYBlockHeight();

    /**
     * Get the total volume of the region.
     */
    long getVolume();

    /**
     * Get the center location of the region.
     */
    Location getCenter();

    /**
     * Get the smallest X axis coordinates from the chunks
     * the region intersects with.
     */
    int getChunkX();

    /**
     * Get the smallest Z axis coordinates from the chunks
     * the region intersects with.
     */
    int getChunkZ();

    /**
     * Get the number of chunks that comprise the chunk width
     * on the X axis of the region.
     */
    int getChunkXWidth();

    /**
     * Get the number of chunks that comprise the chunk width
     * on the Z axis of the region.
     */
    int getChunkZWidth();

    /**
     * Determine if the region is 1 block tall.
     */
    boolean isFlatHorizontal();

    /**
     * Determine if the region is 1 block wide on the
     * X or Z axis and is not 1 block tall.
     */
    boolean isFlatVertical();
}
