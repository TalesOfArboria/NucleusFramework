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
import com.jcwhatever.nucleus.managed.astar.IAStarSettings;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;

/**
 * Implementation of {@link IAStarContext}.
 */
class AStarContext<N extends IAStarNode<N>> implements IAStarContext<N> {

    private final N _start;
    private final N _destination;
    private final AStarNodeContainer<N> _container;
    private final IAStarNodeExaminer<N> _examiner;
    private final IAStarSettings _settings;

    AStarContext(N start, N destination, IAStarNodeExaminer<N> examiner, IAStarSettings settings) {
        _start = start;
        _destination = destination;
        _container = new AStarNodeContainer<N>();
        _examiner = examiner;
        _settings = settings;

        start.setContext(this);
        destination.setContext(this);
    }

    @Override
    public N getStart() {
        return _start;
    }

    @Override
    public N getDestination() {
        return _destination;
    }

    @Override
    public IAStarNodeContainer<N> getNodeContainer() {
        return _container;
    }

    @Override
    public IAStarNodeExaminer<N> getNodeExaminer() {
        return _examiner;
    }

    @Override
    public IAStarSettings getSettings() {
        return _settings;
    }
}
