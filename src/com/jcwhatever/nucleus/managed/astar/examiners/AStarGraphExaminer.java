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

package com.jcwhatever.nucleus.managed.astar.examiners;

import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.managed.astar.score.AStarGraphScore;
import com.jcwhatever.nucleus.managed.astar.score.IAStarScore;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * A simple node examiner implementation of {@link IAStarNodeExaminer}.
 */
public class AStarGraphExaminer<N extends IAStarNode<N>> implements IAStarNodeExaminer<N> {

    private static final AStarGraphExaminer INSTANCE = new AStarGraphExaminer();

    public static <T extends IAStarNode<T>> AStarGraphExaminer<T> get() {
        @SuppressWarnings("unchecked")
        AStarGraphExaminer<T> result = (AStarGraphExaminer<T>)INSTANCE;
        return result;
    }

    @Override
    public boolean isDestination(N node) {
        return node.getContext().getDestination().equals(node);
    }

    @Override
    public PathableResult isPathable(N from, N to) {
        PreCon.notNull(from);
        PreCon.notNull(to);

        //noinspection unchecked
        return to.isAdjacent(from) && (from.getParent() == null || !from.getParent().equals(to))
                ? PathableResult.VALID
                : PathableResult.INVALID_POINT;
    }

    @Override
    public IAStarScore<N> getScore(@Nullable N parent, N node) {
        return new AStarGraphScore<N>(parent, node);
    }
}
