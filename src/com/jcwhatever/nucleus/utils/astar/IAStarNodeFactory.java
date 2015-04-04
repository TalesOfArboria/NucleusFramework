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

package com.jcwhatever.nucleus.utils.astar;

import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

/**
 * A factory to instantiate new {@link AStarNode} instances.
 */
public interface IAStarNodeFactory {

    /**
     * Create a new {@link AStarNode}.
     *
     * @param context  The node context.
     * @param x        The X coordinates.
     * @param y        The Y coordinates.
     * @param z        The Z coordinates.
     */
    AStarNode createNode(AStarContext context, int x, int y, int z);

    /**
     * Create a new {@link AStarNode}.
     *
     * @param context  The node context.
     * @param coords   The node coordinates.
     */
    AStarNode createNode(AStarContext context, ICoords3Di coords);

    /**
     * Create a new {@link AStarNode}.
     *
     * @param context  The node context.
     * @param parent   The parent node to get base coordinates from.
     * @param offsetX  The X axis offset from the parent X axis coordinates.
     * @param offsetY  The Y axis offset from the parent Y axis coordinates.
     * @param offsetZ  The Z axis offset from the parent Z axis coordinates.
     */
    AStarNode createNode(AStarContext context, AStarNode parent,
                         int offsetX, int offsetY, int offsetZ);
}
