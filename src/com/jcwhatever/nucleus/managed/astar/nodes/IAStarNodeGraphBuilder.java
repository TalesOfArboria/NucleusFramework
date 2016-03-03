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

import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;

/**
 * Interface for an implementation that builds {@link IAStarNodeGraphBuilder}.
 */
public interface IAStarNodeGraphBuilder {

    /**
     * Add a named node.
     *
     * @param name           The name of the node.
     * @param location       The node location.
     * @param adjacentNames  The names of all nodes that are adjacent.
     *
     * @return  Self for chaining.
     */
    IAStarNodeGraphBuilder add(String name, Location location, String... adjacentNames);

    /**
     * Add a named node.
     *
     * @param name           The name of the node.
     * @param coords         The node coordinates.
     * @param adjacentNames  The names of all nodes that are adjacent.
     *
     * @return  Self for chaining.
     */
    IAStarNodeGraphBuilder add(String name, ICoords3Di coords, String... adjacentNames);

    /**
     * Add a named node.
     *
     * @param name           The name of the node.
     * @param x              The X coordinate.
     * @param y              The Y coordinate.
     * @param z              The Z coordinate.
     * @param adjacentNames  The names of all nodes that are adjacent.
     *
     * @return  Self for chaining.
     */
    IAStarNodeGraphBuilder add(String name, int x, int y, int z, String... adjacentNames);;

    /**
     * Build and return the defined node map.
     *
     * @throws IllegalStateException if any of the nodes specifies an adjacent node that was not defined.
     */
    IAStarNodeGraph build();
}
