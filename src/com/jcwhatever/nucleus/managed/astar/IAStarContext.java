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

package com.jcwhatever.nucleus.managed.astar;

import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;

/**
 * Provides context information for use with a single AStar search operation
 * using {@link IAStarNode}.
 */
public interface IAStarContext<N extends IAStarNode<N>> {

    /**
     * Get the start node.
     */
    N getStart();

    /**
     * Get the destination node.
     */
    N getDestination();

    /**
     * Get the node container.
     */
    IAStarNodeContainer<N> getNodeContainer();

    /**
     * Get the examiner.
     */
    IAStarNodeExaminer<N> getNodeExaminer();

    /**
     * Get the path finding settings.
     */
    IAStarSettings getSettings();
}
