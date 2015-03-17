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
import com.jcwhatever.nucleus.utils.astar.IAStarExaminer;
import com.jcwhatever.nucleus.utils.astar.IAStarNodeContainer;
import com.jcwhatever.nucleus.utils.astar.IAStarScore;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nullable;

/**
 * Basic implementation of {@link IAStarNodeContainer}.
 */
public class AStarNodeContainer implements IAStarNodeContainer {

    private final IAStarExaminer _examiner;
    private final TreeMap<AStarNode, AStarNode> _open = new TreeMap<>();
    private final Set<AStarNode> _closed = new HashSet<>(35);

    public AStarNodeContainer(IAStarExaminer examiner) {
        PreCon.notNull(examiner);

        _examiner = examiner;
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
    public void open(@Nullable AStarNode parent, AStarNode node) {
        PreCon.notNull(node);

        IAStarScore score = _examiner.getScore(parent, node);

        AStarNode open = _open.remove(node);
        if (open != null) {

            IAStarScore openScore = open.getScore();
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
    public boolean isOpen(AStarNode node) {
        return _open.containsKey(node);
    }

    @Override
    public boolean isClosed(AStarNode node) {
        return _closed.contains(node);
    }

    @Override
    public boolean contains(AStarNode node) {
        return _open.containsKey(node) || _closed.contains(node);
    }

    @Override
    @Nullable
    public AStarNode closeBest() {
        if (_open.isEmpty())
            return null;

        AStarNode node = _open.pollFirstEntry().getKey();
        _closed.add(node);

        return node;
    }
}
