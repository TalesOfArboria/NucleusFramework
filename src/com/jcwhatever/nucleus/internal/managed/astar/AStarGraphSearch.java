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

package com.jcwhatever.nucleus.internal.managed.astar;

import com.jcwhatever.nucleus.managed.astar.IAStarContext;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.managed.astar.IAStarNodeContainer;
import com.jcwhatever.nucleus.managed.astar.IAStarResult;
import com.jcwhatever.nucleus.managed.astar.IAStarResult.ResultStatus;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer.PathableResult;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * AStar graph search implementation.
 */
class AStarGraphSearch<N extends IAStarNode<N>> {

    private static final AStarGraphSearch INSTANCE = new AStarGraphSearch();

    static <T extends IAStarNode<T>> AStarGraphSearch<T> get() {
        @SuppressWarnings("unchecked")
        AStarGraphSearch<T> result = (AStarGraphSearch<T>)INSTANCE;
        return result;
    }

    public IAStarResult<N> search(IAStarContext<N> context) {
        PreCon.notNull(context);

        IAStarNodeContainer<N> container = context.getNodeContainer();
        container.reset();
        container.open(null, context.getStart());

        LocalContext<N> localContext = new LocalContext<>(context);
        openAdjacent(context.getStart(), localContext);

        N current = null;

        // iterate until destination is found
        // or unable to continue

        long iterations = 0;
        long maxIterations = context.getSettings().getMaxIterations();

        while (container.openSize() > 0 && !container.isClosed(context.getDestination())) {

            // get and close best candidate for next node to path to.
            current = container.closeBest();
            if (current == null || context.getNodeExaminer().isDestination(current))
                break;

            // open valid adjacent nodes
            openAdjacent(current, localContext);

            iterations ++;

            // do not exceed max iterations
            if (maxIterations > 0 && iterations >= maxIterations) {
                return new AStarResult<N>(ResultStatus.ITERATIONS_EXCEEDED);
            }
        }

        return new AStarResult<N>(context, current);
    }

    /**
     * Invoked to search for and open valid adjacent nodes.
     *
     * @param node  The node to search around.
     */
    private void openAdjacent(N node, LocalContext<N> localContext) {
        node.getAdjacent(localContext.adjacent);

        for (N candidate : localContext.adjacent) {
            if (candidate.equals(node) || localContext.context.getNodeContainer().contains(candidate))
                continue;

            candidate.setContext(node.getContext());
            openCandidate(node, candidate);
        }

        localContext.adjacent.clear();
    }

    /**
     * Invoked to check a node candidate and, if valid, open it.
     */
    private void openCandidate(@Nullable N parent, N candidate) {

        // get instance of candidate node
        IAStarContext<N> context = candidate.getContext();

        PathableResult result = parent == null
                ? PathableResult.VALID
                : context.getNodeExaminer().isPathable(parent, candidate);

        switch (result) {
            case VALID:
                context.getNodeContainer().open(parent, candidate);
                break;
            case INVALID_COLUMN:
                // fall through
            case INVALID_POINT:
                break;
        }
    }

    private static class LocalContext<N extends IAStarNode<N>> {
        final List<N> adjacent = new ArrayList<>(9);
        final IAStarContext<N> context;
        LocalContext(IAStarContext<N> context) {
            this.context = context;
        }
    }
}
