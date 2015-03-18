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

import com.jcwhatever.nucleus.utils.Coords3Di;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Stores context info related to a specific A-Star path search.
 *
 * @see AStar
 */
public class AStarContext {

    private final AStar _astar;
    private final IAStarNodeContainer _container;

    private final Coords3Di _startCoords;
    private final Coords3Di _destinationCoords;

    private final AStarNode _start;
    private final AStarNode _destination;

    /**
     * Constructor.
     *
     * @param astar               The {@link AStar} engine instance.
     * @param container           The node container.
     * @param startCoords         The search start coordinates.
     * @param destinationCoords   The search destination coordinates.
     */
    public AStarContext(AStar astar, IAStarNodeContainer container,
                        Coords3Di startCoords, Coords3Di destinationCoords) {
        PreCon.notNull(astar);
        PreCon.notNull(container);
        PreCon.notNull(startCoords);
        PreCon.notNull(destinationCoords);

        _astar = astar;
        _container = container;
        _startCoords = startCoords;
        _destinationCoords = destinationCoords;
        _start = new AStarNode(this, startCoords);
        _destination = new AStarNode(this, destinationCoords);
    }

    /**
     * Get the {@link AStar} instance.
     */
    public AStar getAstar() {
        return _astar;
    }

    /**
     * Get the node container.
     */
    public IAStarNodeContainer getContainer() {
        return _container;
    }

    /**
     * Get the start node.
     */
    public AStarNode getStart() {
        return _start;
    }

    /**
     * Get the destination node.
     */
    public AStarNode getDestination() {
        return _destination;
    }

    /**
     * Get the start coordinates.
     */
    public Coords3Di getStartCoords() {
        return _startCoords;
    }

    /**
     * Get the destination coordinates.
     */
    public Coords3Di getDestinationCoords() {
        return _destinationCoords;
    }
}
