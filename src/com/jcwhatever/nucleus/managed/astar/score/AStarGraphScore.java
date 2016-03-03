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
import com.jcwhatever.nucleus.utils.coords.Coords3Di;

import javax.annotation.Nullable;

/*
 * 
 */
public class AStarGraphScore<N extends IAStarNode<N>> extends AbstractAStarScore<N>
        implements IAStarScore<N> {

    private static final ScoreProvider PROVIDER_INSTANCE = new ScoreProvider();

    public static <T extends IAStarNode<T>> IAStarScoreProvider<T> getProvider() {
        @SuppressWarnings("unchecked")
        ScoreProvider<T> result = (ScoreProvider<T>)PROVIDER_INSTANCE;
        return result;
    }

    private final IAStarNode _destination;

    /**
     * Constructor.
     *
     * @param parent  The adjacent parent node to apply to the calculations.
     * @param node    The node to calculate.
     */
    public AStarGraphScore(@Nullable N parent, N node) {
        super(parent, node);

        _destination = node.getContext().getDestination();
    }

    @Override
    protected float calculateG() {

        float g = 0;
        N parent;
        N current = getNode();

        while ((parent = current.getParent()) != null) {
            g += Coords3Di.distanceSquared(parent, current);

            // move backwards
            current = parent;
        }
        return g;
    }

    /**
     * Calculate and return the H score.
     */
    @Override
    protected float calculateH() {
        return (float)(Coords3Di.distanceSquared(getNode(), _destination));
    }

    private static class ScoreProvider<N extends IAStarNode<N>> implements IAStarScoreProvider<N> {

        @Override
        public IAStarScore<N> getScore(@Nullable N parent, N node) {
            return new AStarScore<N>(parent, node);
        }
    }
}
