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

package com.jcwhatever.nucleus.utils.astar.pooled;

import com.jcwhatever.nucleus.utils.astar.AStarContext;
import com.jcwhatever.nucleus.utils.astar.AStarNode;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;

/**
 * An {@link AStarNode} for use with {@link AStarPooledNodeFactory}.
 *
 * <p>See documentation in {@link AStarPooledNodeFactory} for more information about
 * the problems of using pooling.</p>
 */
public class AStarPooledNode extends AStarNode {

    private MutableCoords3Di _coords;

    /**
     * Constructor.
     *
     * @param context     The search context.
     * @param coords The node coordinates.
     */
    public AStarPooledNode(AStarContext context, MutableCoords3Di coords) {
        super(context, coords);

        _coords = coords;
    }

    @Override
    public ICoords3Di getAdjustedCoords() {

        if (_coords != null) {
            _coords.setY(_coords.getY() + 1);

            return _coords;
        }
        else {
            return super.getAdjustedCoords();
        }
    }

    @Override
    protected void init(AStarContext context, ICoords3Di startCoords) {
        super.init(context, startCoords);

        _coords = startCoords instanceof MutableCoords3Di
                ? (MutableCoords3Di) startCoords
                : null;
    }
}
