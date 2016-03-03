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
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinate based AStar search implementation.
 */
class AStarCoordsSearch<N extends IAStarNode<N>> {

    private static final AStarCoordsSearch INSTANCE = new AStarCoordsSearch();

    static <T extends IAStarNode<T>> AStarCoordsSearch<T> get() {
        @SuppressWarnings("unchecked")
        AStarCoordsSearch<T> result = (AStarCoordsSearch<T>)INSTANCE;
        return result;
    }

    private AStarCoordsSearch() {}

    public IAStarResult<N> search(IAStarContext<N> context) {
        PreCon.notNull(context);

        if (Coords3Di.distanceSquared(context.getStart(),
                context.getDestination()) > context.getSettings().getRangeSquared()) {

            return new AStarResult<N>(ResultStatus.RANGE_EXCEEDED);
        }

        IAStarNodeContainer<N> container = context.getNodeContainer();
        container.reset();
        container.open(null, context.getStart());

        LocalContext localContext = new LocalContext(context);
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
     */
    private void openAdjacent(N node, LocalContext localContext) {

        // column validations, work from top down, skip columns that are false
        boolean[][] columns = new boolean[][] {
                { true, true,  true },
                { true, false, true },
                { true, true,  true }
        };

        node.getAdjacent(localContext.adjacent);

        for (N candidate : localContext.adjacent) {
            openCandidate(candidate, columns);
        }

        localContext.adjacent.clear();
    }

    /**
     * Invoked to check a node candidate and, if valid, open it.
     *
     * @param columns  Column validation array. A 3x3 array of booleans.
     */
    private void openCandidate(N candidate, boolean[][] columns) {

        int x = candidate.getOffsetX();
        int z = candidate.getOffsetZ();

        if (!columns[x + 1][z + 1])
            return;

        N parent = candidate.getParent();

        // get instance of candidate node
        IAStarContext<N> context = parent.getContext();

        // check range
        if (Coords3Di.distanceSquared(candidate, context.getStart()) > context.getSettings().getRangeSquared()) {
            columns[x + 1][z + 1] = false;
            return;
        }

        IAStarNodeExaminer.PathableResult result = context.getNodeExaminer().isPathable(parent, candidate);

        switch (result) {
            case VALID:
                context.getNodeContainer().open(parent, candidate);
                // fall through, don't check columns where a valid node was already found.
            case INVALID_COLUMN:
                columns[x + 1][z + 1] = false;
                // fall through
            case INVALID_POINT:
                break;
        }
    }

    private class LocalContext {
        final List<N> adjacent = new ArrayList<>(9);
        final IAStarContext<N> context;

        LocalContext(IAStarContext<N> context) {
            this.context = context;
        }
    }
}
