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
import com.jcwhatever.nucleus.managed.astar.IAStarResult;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link IAStarResult}.
 */
class AStarResult<N extends IAStarNode<N>> implements IAStarResult<N> {

    private final LinkedList<N> _nodes = new LinkedList<>();
    private final ResultStatus _status;

    /**
     * Constructor.
     *
     * <p>Used for failed searches only.</p>
     *
     * @param status  The failed result status.
     */
    AStarResult(ResultStatus status) {
        PreCon.notNull(status);
        PreCon.isValid(status != ResultStatus.RESOLVED, "Incorrect constructor for RESOLVED status.");

        _status = status;
    }

    /**
     * Constructor.
     *
     * @param context    The search context.
     * @param finalNode  The last node closed in the search, if any.
     */
    public AStarResult(IAStarContext<N> context, @Nullable N finalNode) {
        PreCon.notNull(context);

        _status = finalNode == null
                ? ResultStatus.UNRESOLVABLE
                : getResult(context, finalNode, _nodes);
    }

    @Override
    public ResultStatus getStatus() {
        return _status;
    }

    @Override
    public boolean hasNext() {
        return !_nodes.isEmpty();
    }

    @Override
    public N remove() {
        return _nodes.removeFirst();
    }

    @Override
    public List<N> values() {
        return _nodes;
    }

    @Override
    public int getPathDistance() {
        if (_status != ResultStatus.RESOLVED)
            return -1;

        return _nodes.size();
    }

    private ResultStatus getResult(IAStarContext<N> context, N finalNode, LinkedList<N> nodes) {

        N current = finalNode;

        while (current != null) {
            if (current instanceof AStarNode) {
                @SuppressWarnings("unchecked")
                N adjusted = (N)((AStarNode)current).surfaceAdjust(1);
                nodes.addFirst(adjusted);
            }
            else {
                nodes.addFirst(current);
            }
            current = current.getParent();
        }

        if (nodes.isEmpty()) {
            return ResultStatus.UNRESOLVABLE;
        }
        else {

            boolean hasStart = nodes.peekFirst().equals(context.getStart());
            //noinspection EqualsBetweenInconvertibleTypes
            N destination = context.getDestination();
            boolean hasEnd = destination instanceof AStarNode
                    ? new Coords3Di(context.getDestination(), 0, 1, 0).equals(finalNode)
                    : destination.equals(finalNode);

            return hasStart && hasEnd
                    ? ResultStatus.RESOLVED
                    : ResultStatus.UNRESOLVABLE;
        }
    }
}
