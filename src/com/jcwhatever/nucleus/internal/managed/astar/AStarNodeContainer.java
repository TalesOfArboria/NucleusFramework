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

import com.jcwhatever.nucleus.managed.astar.IAStarNodeContainer;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.managed.astar.score.IAStarScore;
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Implementation of {@link IAStarNodeContainer}
 */
class AStarNodeContainer<N extends IAStarNode<N>> implements IAStarNodeContainer<N> {

    private final TreeMap<N, N> _open = new TreeMap<>();
    private final Set<N> _closed = new HashSet<>(35);

    @Override
    public void reset() {
        _open.clear();
        _closed.clear();
    }

    @Override
    public int openSize() {
        return _open.size();
    }

    @Override
    public int closeSize() {
        return _closed.size();
    }

    @Override
    public void open(@Nullable N parent, N node) {
        PreCon.notNull(node);

        if (parent != null) {

            if (!parent.getContext().equals(node.getContext()))
                throw new IllegalArgumentException("parent and node arguments are from different contexts.");
        }

        IAStarScore<N> score = node.getContext().getNodeExaminer().getScore(parent, node);

        N open = _open.remove(node);
        if (open != null) {

            IAStarScore<N> openScore = open.getScore();
            float newG = score.getG();

            if (openScore == null || newG < openScore.getG()) {
                node.setParent(parent, score);
                _open.put(node, node);
            }
            else {
                _open.put(open, open);
            }
        }
        else {
            node.setParent(parent, score);
            _open.put(node, node);
        }
    }

    @Override
    public boolean isOpen(N node) {
        return _open.containsKey(node);
    }

    @Override
    public boolean isClosed(N node) {
        return _closed.contains(node);
    }

    @Override
    public boolean contains(N node) {
        return _open.containsKey(node) || _closed.contains(node);
    }

    @Nullable
    @Override
    public N closeBest() {
        if (_open.isEmpty())
            return null;

        N node = _open.pollFirstEntry().getKey();
        _closed.add(node);

        return node;
    }
}
