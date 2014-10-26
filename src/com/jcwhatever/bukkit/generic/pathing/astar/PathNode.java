/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.pathing.astar;

import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Stores information about a path node location.
 */
public interface PathNode extends Comparable<PathNode> {

    /**
     * Get the parent node.
     */
    @Nullable
    public PathNode getParentNode();

    /**
     * Get the X axis offset from the parent location X axis.
     */
    public int getXParentOffset();

    /**
     * Get the Y axis offset from the parent location Y axis.
     */
    public int getYParentOffset();

    /**
     * Get the Z axis offset from the parent location Z axis.
     */
    public int getZParentOffset();

    /**
     * Get the X axis offset from the start location X axis.
     */
    public int getXStartOffset();

    /**
     * Get the Y axis offset from the start location Y axis.
     */
    public int getYStartOffset();

    /**
     * Get the Z axis offset from the start location Z axis.
     */
    public int getZStartOffset();

    /**
     * Get the start location of the path.
     */
    public Location getStartLocation();

    /**
     * Get the end location of the path.
     */
    public Location getEndLocation();

    /**
     * Get the node location.
     */
    public Location getLocation();

    /**
     * Get the G score.
     */
    public int getGScore();

    /**
     * Get the H score.
     */
    public long getHScore();

    /**
     * Get the F score.
     */
    public long getFScore();

    /**
     * Get the material of the block at the
     * location the path node represents.
     */
    public Material getMaterial();

    /**
     * Determine if the location is a transparent
     * block that entities can move through.
     */
    public boolean isTransparent();

    /**
     * Determine if the location is a block
     * that entities can walk on.
     */
    public boolean isSurface();
}
