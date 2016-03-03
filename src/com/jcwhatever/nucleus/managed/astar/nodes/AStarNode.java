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

package com.jcwhatever.nucleus.managed.astar.nodes;

import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;

/**
 * Implementation of {@link IAStarNode}.
 */
public class AStarNode extends AbstractAStarNode<AStarNode> {

    /**
     * Constructor.
     *
     * @param coords  The node coordinates.
     */
    public AStarNode(ICoords3Di coords) {
        super(coords.getX(), coords.getY(), coords.getZ());
    }

    /**
     * Constructor.
     *
     * @param coords  The node coordinates.
     */
    public AStarNode(ICoords3D coords) {
        super(coords.getFloorX(), coords.getFloorY(), coords.getFloorZ());
    }

    /**
     * Constructor.
     *
     * @param location  The node coordinates.
     */
    public AStarNode(Location location) {
        super(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Constructor.
     *
     * @param x  The X coordinate.
     * @param y  The Y coordinate.
     * @param z  The Z coordinate.
     */
    public AStarNode(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * Constructor.
     *
     * @param parent   The parent node.
     * @param offsetX  The X offset from parent.
     * @param offsetY  The Y offset from parent.
     * @param offsetZ  The Z offset from parent.
     */
    protected AStarNode(AStarNode parent, int offsetX, int offsetY, int offsetZ) {
        super(parent, offsetX, offsetY, offsetZ);
    }

    @Override
    public AStarNode getRelative(int offsetX, int offsetY, int offsetZ) {
        return new AStarNode(this, offsetX, offsetY, offsetZ);
    }

    /**
     * Adjust the Y coordinate.
     *
     * <p>Intended for use after search is completed and the results are being constructed. Allows
     * adjusting the coordinate for path purposes.</p>
     *
     * @param offsetY  The offset from the current value.
     *
     * @return  Self for chaining.
     */
    public AStarNode surfaceAdjust(int offsetY) {
        _y += offsetY;
        return this;
    }
}
