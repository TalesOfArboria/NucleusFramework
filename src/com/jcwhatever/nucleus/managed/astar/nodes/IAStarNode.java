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

import com.jcwhatever.nucleus.managed.astar.IAStarContext;
import com.jcwhatever.nucleus.managed.astar.score.IAStarScore;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * AStar node coordinate.
 */
public interface IAStarNode<N extends IAStarNode<N>> extends Comparable<N>, ICoords3Di {

    /**
     * Get the nodes search context.
     */
    IAStarContext<N> getContext();

    /**
     * Set the nodes search context.
     *
     * @param context  The context.
     */
    <T extends IAStarContext<N>> void setContext(T context);

    /**
     * Get the nodes current parent.
     */
    N getParent();

    /**
     * Set the nodes parent and score.
     *
     * @param parent  The parent.
     * @param score   The nodes score.
     */
    void setParent(@Nullable N parent, IAStarScore<N> score);

    /**
     * Get all adjacent nodes.
     */
    Collection<N> getAdjacent();

    /**
     * Add all adjacent nodes to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<N>> T getAdjacent(T output);

    /**
     * Determine if a node is adjacent on the X and Z axis.
     *
     * @param node  The node to check.
     */
    boolean isAdjacent(N node);

    /**
     * Get the nodes current score.
     */
    @Nullable
    IAStarScore<N> getScore();

    /**
     * Get X coordinates relative to parent.
     */
    int getOffsetX();

    /**
     * Get Y coordinates relative to parent.
     */
    int getOffsetY();

    /**
     * Get Z coordinates relative to parent.
     */
    int getOffsetZ();

    /**
     * Get a node relative to this node.
     *
     * @param offsetX  The X offset value.
     * @param offsetY  The Y offset value.
     * @param offsetZ  The Z offset value.
     */
    N getRelative(int offsetX, int offsetY, int offsetZ);
}
