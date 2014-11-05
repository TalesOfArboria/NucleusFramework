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


package com.jcwhatever.bukkit.generic.pathing.astar;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * A collection of path nodes.
 *
 * @param <T>  Path node type.
 */
public interface NodeCollection<T extends PathNode> {

    /**
     * Get the number of nodes in the collection.
     */
    int size();

    /**
     * Add a node to the collection.
     *
     * @param node  The node to add.
     */
    void add(T node);

    /**
     * Remove a node from the collection.
     * <p>
     *     Should return the best candidate node.
     * </p>
     */
    @Nullable
    T remove();

    /**
     * Remove a specific node from the collection.
     *
     * @param nodeLocation  The node location.
     */
    @Nullable
    T remove(Location nodeLocation);

    /**
     * Get a node from the collection.
     *
     * @param nodeLocation  The node location.
     */
    @Nullable
    T get(Location nodeLocation);

    /**
     * Determine if the collection contains a node.
     *
     * @param node  The node to check.
     */
    boolean contains(T node);

    /**
     * Determine if the collection contains a node.
     *
     * @param nodeLocation  The location of the node to check.
     */
    boolean contains(Location nodeLocation);

    /**
     * Clear all nodes from the collection.
     */
    void clear();

    /**
     * Get an iterator from the collection.
     */
    Iterator<T> iterator();
}
