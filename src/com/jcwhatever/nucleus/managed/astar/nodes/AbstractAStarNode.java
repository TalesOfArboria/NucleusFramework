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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract implementation of {@link IAStarNode}.
 */
public abstract class AbstractAStarNode<N extends IAStarNode<N>>  implements IAStarNode<N> {

    protected IAStarContext<N> _context;
    protected N _parent;
    protected IAStarScore<N> _score;
    protected int _x;
    protected int _y;
    protected int _z;

    /**
     * Constructor.
     *
     * @param x  The X coordinate.
     * @param y  The Y coordinate.
     * @param z  The Z coordinate.
     */
    public AbstractAStarNode(int x, int y, int z) {
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * Constructor.
     *
     * @param parent   The parent node.
     * @param offsetX  The X offset from parent.
     * @param offsetY  The Y offset from parent.
     * @param offsetZ  The Z offset from parent.
     */
    protected AbstractAStarNode(N parent, int offsetX, int offsetY, int offsetZ) {
        _parent = parent;
        _context = parent.getContext();
        _x = parent.getX() + offsetX;
        _y = parent.getY() + offsetY;
        _z = parent.getZ() + offsetZ;
    }

    @Override
    public IAStarContext<N> getContext() {
        return _context;
    }

    @Override
    public <T extends IAStarContext<N>> void setContext(T context) {
        _context = context;
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getY() {
        return _y;
    }

    @Override
    public int getZ() {
        return _z;
    }

    @Override
    public int getOffsetX() {
        return _x - (_parent == null ? _x : _parent.getX());
    }

    @Override
    public int getOffsetY() {
        return _y - (_parent == null ? _y : _parent.getY());
    }

    @Override
    public int getOffsetZ() {
        return _z - (_parent == null ? _z : _parent.getZ());
    }

    @Override
    public N getParent() {
        return _parent;
    }

    @Override
    public void setParent(@Nullable N parent, IAStarScore<N> score) {
        PreCon.notNull(score);

        _parent = parent;
        _score = score;
    }

    @Override
    public Collection<N> getAdjacent() {
        return getAdjacent(new ArrayList<N>(9));
    }

    @Override
    public <T extends Collection<N>> T getAdjacent(T output) {

        int drop = _context.getSettings().getMaxDropHeight();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 1; y >= -drop; y--) {
                    if (x == 0 && z == 0 && y == 0)
                        continue;
                    output.add(getRelative(x, y, z));
                }
            }
        }

        return output;
    }

    @Override
    public boolean isAdjacent(N node) {
        PreCon.notNull(node);

        return Math.abs(node.getX() - getX()) <= 1 &&
                Math.abs(node.getZ() - getZ()) <= 1;
    }

    @Nullable
    @Override
    public IAStarScore<N> getScore() {
        if (_score == null && _context != null) {
            @SuppressWarnings("unchecked")
            N self = (N)this;
            _score = _context.getNodeExaminer().getScore(_parent, self);
        }
        return _score;
    }

    @Override
    public int compareTo(N o) {
        PreCon.notNull(o);

        IAStarScore<N> score = getScore();
        IAStarScore<N> otherScore = o.getScore();

        if (score == null)
            return otherScore != null ? 1 : 0;

        if (otherScore == null)
            return -1;

        return score.compareTo(otherScore);
    }

    @Override
    public int hashCode() {
        return _x ^ _y ^ _z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ICoords3Di) {
            ICoords3Di other = (ICoords3Di)obj;

            return other.getX() == _x &&
                    other.getY() == _y &&
                    other.getZ() == _z;
        }
        return false;
    }
}

