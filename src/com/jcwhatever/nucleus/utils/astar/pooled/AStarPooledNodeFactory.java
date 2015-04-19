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

import com.jcwhatever.nucleus.utils.astar.AStar;
import com.jcwhatever.nucleus.utils.astar.AStarContext;
import com.jcwhatever.nucleus.utils.astar.AStarNode;
import com.jcwhatever.nucleus.utils.astar.AStarResult;
import com.jcwhatever.nucleus.utils.astar.IAStarNodeFactory;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolElementFactory;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolRecycleHandler;
import com.jcwhatever.nucleus.utils.performance.pool.SimplePool;

/**
 * A {@link com.jcwhatever.nucleus.utils.astar.IAStarNodeFactory} implementation
 * that pools nodes and their coordinates.
 *
 * <p>Should not be used except in controlled isolation and when warranted. When used,
 * the {@link Coords3Di} in the {@link AStarResult} are actually pooled {@link MutableCoords3Di}
 * instances which may change values the next time an {@link AStar} search is invoked using the
 * same {@link AStarPooledNodeFactory}. The results of the previous {@link AStar} search
 * must no longer be in use before using the pooled factory again.</p>
 *
 * <p>In addition, if any of the coordinates from the result need to be stored, they should
 * be copied.</p>
 */
public class AStarPooledNodeFactory implements IAStarNodeFactory {

    private static final IPoolElementFactory<MutableCoords3Di> COORDS_FACTORY =
            new IPoolElementFactory<MutableCoords3Di>() {
                @Override
                public MutableCoords3Di create() {
                    return new MutableCoords3Di();
                }
            };

    private static final IPoolElementFactory<AStarPooledNode> NODE_FACTORY =
            new IPoolElementFactory<AStarPooledNode>() {
                @Override
                public AStarPooledNode create() {
                    return new AStarPooledNode();
                }
            };

    private static final IPoolRecycleHandler<AStarPooledNode> NODE_RECYCLER =
            new IPoolRecycleHandler<AStarPooledNode>() {
                @Override
                public void onRecycle(AStarPooledNode element) {
                    element.init(null, null);
                }
            };

    private final SimplePool<MutableCoords3Di> _coordsPool =
            new SimplePool<MutableCoords3Di>(100, COORDS_FACTORY);

    private final SimplePool<AStarPooledNode> _nodePool =
            new SimplePool<AStarPooledNode>(100, NODE_FACTORY, NODE_RECYCLER);

    /**
     * Get the max number of objects to pool.
     *
     * <p>The default value is -1 (unlimited).</p>
     *
     * @return  The max number or -1 for unlimited.
     */
    public int getMaxSize() {
        return _nodePool.maxSize();
    }

    /**
     * Set the max number of objects to pool.
     *
     * @param maxSize  The max number of objects to pool or -1 for unlimited.
     */
    public void setMaxSize(int maxSize) {
        _nodePool.setMaxSize(maxSize);
        _coordsPool.setMaxSize(maxSize);
    }

    /**
     * Clear all objects from the pool.
     */
    public void clearPool() {
        _nodePool.clear();
        _coordsPool.clear();
    }

    @Override
    public AStarNode createNode(AStarContext context, int x, int y, int z) {

        MutableCoords3Di coords = createCoords(x, y, z);
        return createNode(context, coords);
    }

    @Override
    public AStarNode createNode(AStarContext context, ICoords3Di coords) {

        MutableCoords3Di mCoords = createCoords(coords.getX(), coords.getY(), coords.getZ());
        return createNode(context, mCoords);
    }

    @Override
    public AStarNode createNode(AStarContext context, AStarNode parent,
                                int offsetX, int offsetY, int offsetZ) {

        ICoords3Di parentCoords = parent.getCoords();

        int x = parentCoords.getX() + offsetX;
        int y = parentCoords.getY() + offsetY;
        int z = parentCoords.getZ() + offsetZ;

        MutableCoords3Di mCoords = createCoords(x, y, z);

        return createNode(context, mCoords);
    }

    protected void pool(AStarPooledNode node) {

        ICoords3Di coords = node.getCoords();
        if (coords instanceof MutableCoords3Di) {
            _coordsPool.recycle((MutableCoords3Di) coords);
        }

        _nodePool.recycle(node);
    }

    private MutableCoords3Di createCoords(int x, int y, int z) {

        MutableCoords3Di coords = _coordsPool.retrieve();
        assert coords != null;

        coords.setX(x);
        coords.setY(y);
        coords.setZ(z);
        return coords;
    }

    private AStarPooledNode createNode(AStarContext context, MutableCoords3Di coords) {

        AStarPooledNode node = _nodePool.retrieve();
        assert node != null;

        node.init(context, coords);
        return node;
    }
}
