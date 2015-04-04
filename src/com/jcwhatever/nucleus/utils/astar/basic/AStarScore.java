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

package com.jcwhatever.nucleus.utils.astar.basic;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.AStarNode;
import com.jcwhatever.nucleus.utils.astar.IAStarScore;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

import javax.annotation.Nullable;

/**
 * Basic implementation of {@link IAStarScore}.
 */
public class AStarScore implements IAStarScore {

    private final AStarNode _node;
    private final AStarNode _parent;
    private final AStarNode _destination;

    private boolean _isCalculated;
    private float _g;
    private float _h;

    /**
     * Constructor.
     *
     * @param parent  The adjacent parent node to apply to the calculations.
     * @param node    The node to calculate.
     */
    public AStarScore(@Nullable AStarNode parent, AStarNode node) {
        PreCon.notNull(node);
        PreCon.isValid(parent != node, "parent and node cannot be the same.");

        _node = node;
        _parent = parent;
        _destination = node.getContext().getDestination();
    }

    @Override
    public AStarNode getParent() {
        return _parent;
    }

    @Override
    public AStarNode getNode() {
        return _node;
    }

    @Override
    public float getG() {
        calculate();

        return _g;
    }

    @Override
    public float getH() {
        calculate();

        return _h;
    }

    @Override
    public float getF() {
        calculate();

        return _g + _h;
    }

    /**
     * Invoked before getting a score value to ensure
     * the calculations have been made.
     */
    protected void calculate() {

        if (_isCalculated)
            return;

        _isCalculated = true;

        setG(calculateG());
        setH(calculateH());
    }

    /**
     * Calculate and return the G score.
     */
    protected float calculateG() {

        float g = 0;
        AStarNode parentNode;
        AStarNode currentNode = _node;

        while ((parentNode = getParentNode(currentNode)) != null) {

            ICoords3Di parent = parentNode.getCoords();
            ICoords3Di current = currentNode.getCoords();

            int deltaX = Math.abs(current.getX() - parent.getX());
            int deltaY = Math.abs(current.getY() - parent.getY());
            int deltaZ = Math.abs(current.getZ() - parent.getZ());

            if (deltaX == 1 && deltaY == 1 && deltaZ == 1) {
                g += 0.7;
            } else if (((deltaX == 1 || deltaZ == 1) && deltaY == 1) ||
                    ((deltaX == 1 || deltaZ == 1) && deltaY == 0)) {
                g += 0.4;
            } else {
                g += 0.1;
            }

            // move backwards
            currentNode = parentNode;
        }
        return g;
    }

    /**
     * Calculate and return the H score.
     */
    protected float calculateH() {
        return (float)(Coords3Di.distanceSquared(_node.getCoords(), _destination.getCoords()));
    }

    /**
     * Set the G score.
     *
     * @param g  The G score.
     */
    protected void setG(float g) {
        _g = g;
    }

    /**
     * Set the H score.
     *
     * @param h  The H score.
     */
    protected void setH(float h) {
        _h = h;
    }

    @Override
    public int hashCode() {
        return _node.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AStarScore && ((AStarScore) obj)._node.equals(_node);
    }

    @Override
    public int compareTo(IAStarScore other) {
        float otherF = other.getF();
        float f = getF();

        if (f > otherF)
            return 1;
        if (f < otherF)
            return -1;

        return 0;
    }

    @Nullable
    private AStarNode getParentNode(AStarNode node) {
        if (node == _node)
            return _parent;

        return node.getParent();
    }
}
