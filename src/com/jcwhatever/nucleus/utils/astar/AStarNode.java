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

import com.jcwhatever.nucleus.utils.Coords3D;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Represents a single path node/coordinate.
 *
 * @see AStar
 */
public class AStarNode implements Comparable<AStarNode> {

    private final AStarContext _context;
    private final Coords3D _coords;
    private final double _offsetX;
    private final double _offsetY;
    private final double _offsetZ;

    private AStarNode _parentNode;
    private IAStarScore _score;

    /**
     * Constructor.
     *
     * @param context      The search context.
     * @param startCoords  The node coordinates.
     */
    public AStarNode(AStarContext context, Coords3D startCoords) {
        PreCon.notNull(context);

        _context = context;
        _coords = startCoords;
        _parentNode = null;
        _offsetX = startCoords.getX() - context.getStartCoords().getX();
        _offsetY = startCoords.getY() - context.getStartCoords().getY();
        _offsetZ = startCoords.getZ() - context.getStartCoords().getZ();
    }

    /**
     * Get the nodes search context.
     */
    public AStarContext getContext() {
        return _context;
    }

    /**
     * Get the nodes coordinates.
     */
    public Coords3D getCoords() {
        return _coords;
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

        return Math.abs(node.getCoords().getFloorX() - getCoords().getFloorX()) <= 1 &&
                Math.abs(node.getCoords().getFloorZ() - getCoords().getFloorZ()) <= 1;
    }

    /**
     * Create a new node relative to the current one.
     *
     * @param offsetX  The X axis offset from the current node.
     * @param offsetY  The Y axis offset from the current node.
     * @param offsetZ  The Z axis offset from the current node.
     */
    public AStarNode getRelative(double offsetX, double offsetY, double offsetZ) {
        Coords3D coords = new Coords3D(_coords, offsetX, offsetY, offsetZ);
        return new AStarNode(_context, coords);
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
}
