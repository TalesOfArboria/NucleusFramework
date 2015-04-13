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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

import javax.annotation.Nullable;

/**
 * Represents a single path node/coordinate.
 *
 * @see AStar
 */
public class AStarNode implements Comparable<AStarNode> {

    private AStarContext _context;
    private ICoords3Di _coords;
    private int _offsetX;
    private int _offsetY;
    private int _offsetZ;

    private AStarNode _parentNode;
    private IAStarScore _score;

    /**
     * Constructor.
     *
     * @param context      The search context.
     * @param coords  The node coordinates.
     */
    public AStarNode(AStarContext context, ICoords3Di coords) {
        PreCon.notNull(context);
        PreCon.notNull(coords);

        init(context, coords);
    }

    /**
     * Constructor.
     */
    protected AStarNode() {}

    /**
     * Get the nodes search context.
     */
    public AStarContext getContext() {
        return _context;
    }

    /**
     * Get the nodes coordinates.
     */
    public ICoords3Di getCoords() {
        return _coords;
    }

    /**
     * Get the coordinates adjusted for final result.
     */
    public ICoords3Di getAdjustedCoords() {
        return new Coords3Di(_coords, 0, 1, 0);
    }

    /**
     * Get the nodes path score.
     *
     * @return  The score or null if not set.
     */
    @Nullable
    public IAStarScore getScore() {
        return _score;
    }

    /**
     * Get the nodes adjacent pathing parent.
     */
    @Nullable
    public AStarNode getParent() {
        return _parentNode;
    }

    /**
     * Set the nodes adjacent pathing parent.
     *
     * @param parent  The parent node.
     * @param score   The score of the new relationship.
     */
    public void setParent(@Nullable AStarNode parent, IAStarScore score) {
        PreCon.notNull(score, "score");
        PreCon.isValid(parent != this, "cannot set a node to be a parent of itself");
        PreCon.isValid(this != _context.getStart() || parent == null, "the start node cannot have a parent set");
        PreCon.isValid(parent == null || isAdjacent(parent), "parent node must be adjacent");

        _score = score;
        _parentNode = parent;
    }

    /**
     * Determine if a nodes coordinates are adjacent on the X and Z axis.
     *
     * @param node  The node to check.
     */
    public boolean isAdjacent(AStarNode node) {
        PreCon.notNull(node);

        ICoords3Di coords = node.getCoords();

        return Math.abs(coords.getX() - _coords.getX()) <= 1 &&
                Math.abs(coords.getZ() - _coords.getZ()) <= 1;
    }

    /**
     * Create a new node relative to the current one.
     *
     * @param offsetX  The X axis offset from the current node.
     * @param offsetY  The Y axis offset from the current node.
     * @param offsetZ  The Z axis offset from the current node.
     */
    public AStarNode getRelative(int offsetX, int offsetY, int offsetZ) {
        return _context.getContainer().getNodeFactory().createNode(
                _context, this, offsetX, offsetY, offsetZ);
    }

    @Override
    public int compareTo(AStarNode other) {

        IAStarScore score = getScore();
        IAStarScore otherScore = other.getScore();

        if (score == null)
            return otherScore != null ? 1 : 0;

        if (otherScore == null)
            return -1;

        return score.compareTo(otherScore);
    }

    @Override
    public int hashCode() {
        return _coords.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AStarNode) {
            AStarNode node = (AStarNode)obj;
            return _coords.equals(node._coords) &&
                    _offsetX == node._offsetX &&
                    _offsetY == node._offsetY &&
                    _offsetZ == node._offsetZ;
        }
        return false;
    }

    /**
     * Initialize the node.
     *
     * @param context  The node context.
     * @param coords   The node coords.
     */
    protected void init(@Nullable AStarContext context, @Nullable ICoords3Di coords) {

        _context = context;
        _coords = coords;
        _parentNode = null;

        if (context != null && coords != null) {
            _offsetX = coords.getX() - context.getStartCoords().getX();
            _offsetY = coords.getY() - context.getStartCoords().getY();
            _offsetZ = coords.getZ() - context.getStartCoords().getZ();
        }
        else {
            _offsetX = 0;
            _offsetY = 0;
            _offsetZ = 0;
        }

        _parentNode = null;
        _score = null;
    }
}
