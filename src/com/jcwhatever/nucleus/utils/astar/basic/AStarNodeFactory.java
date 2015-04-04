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

import com.jcwhatever.nucleus.utils.astar.AStarContext;
import com.jcwhatever.nucleus.utils.astar.AStarNode;
import com.jcwhatever.nucleus.utils.astar.IAStarNodeFactory;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;

/**
 * Basic implementation of an {@link IAStarNodeFactory}.
 */
public class AStarNodeFactory implements IAStarNodeFactory {

    @Override
    public AStarNode createNode(AStarContext context, int x, int y, int z) {
        return new AStarNode(context, new Coords3Di(x, y, z));
    }

    @Override
    public AStarNode createNode(AStarContext context, ICoords3Di coords) {
        return new AStarNode(context, coords);
    }

    @Override
    public AStarNode createNode(AStarContext context, AStarNode parent,
                                int offsetX, int offsetY, int offsetZ) {
        return new AStarNode(context, new Coords3Di(parent.getCoords(), offsetX, offsetY, offsetZ));
    }
}
