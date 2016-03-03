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

package com.jcwhatever.nucleus.managed.astar.score;

import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Simple implementation of {@link IAStarScore}.
 */
public abstract class AbstractAStarScore<N extends IAStarNode> implements IAStarScore<N> {

    private final N _parent;
    private final N _node;

    private boolean _isCalculated;
    private float _g;
    private float _h;

    /**
     * Constructor.
     *
     * @param parent  The adjacent parent node to apply to the calculations.
     * @param node    The node to calculate.
     */
    public AbstractAStarScore(@Nullable N parent, N node) {
        PreCon.notNull(node);
        PreCon.isValid(parent != node, "parent and node cannot be the same.");

        _node = node;
        _parent = parent;
    }

    @Override
    public N getParent() {
        return _parent;
    }

    @Override
    public N getNode() {
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

    @Override
    public int hashCode() {
        return _node.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractAStarScore && ((AbstractAStarScore) obj)._node.equals(_node);
    }

    @Override
    public int compareTo(IAStarScore o) {
        float otherF = o.getF();
        float f = getF();

        if (f > otherF)
            return 1;
        if (f < otherF)
            return -1;

        return 0;
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
        N parent;
        N current = _node;

        while ((parent = getParentNode(current)) != null) {

            g += 0.1;

            // move backwards
            current = parent;
        }
        return g;
    }

    /**
     * Calculate and return the H score.
     */
    protected float calculateH() {
        return 0;
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

    @Nullable
    private N getParentNode(N node) {
        if (node == _node)
            return _parent;

        @SuppressWarnings("unchecked")
        N result = (N)node.getParent();
        return result;
    }
}
