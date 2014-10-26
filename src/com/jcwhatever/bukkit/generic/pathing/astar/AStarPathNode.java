/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.pathing.astar;

import com.jcwhatever.bukkit.generic.extended.MaterialExt;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nullable;

/**
 * {@code AStarPathNode} implementation of the
 * {@code PathNode} interface.
 */
public class AStarPathNode implements PathNode {

    private final Location _start;
    private final Location _end;
    private final int _xOffset;
    private final int _yOffset;
    private final int _zOffset;
    private final int _hash;

    private int _xParentOffset;
    private int _yParentOffset;
    private int _zParentOffset;
    private int _g = -1;
    private long _h = -1;
    private AStarPathNode _parentNode;

    private boolean _hCalculated;
    private boolean _gCalculated;

    private Location _location;
    private Material _material;

    /**
     * Constructor.
     *
     * <p>Creates the start node.</p>
     *
     * @param start   The path start location.
     * @param end     The path end location.
     */
    public AStarPathNode(Location start, Location end) {
        PreCon.notNull(start);
        PreCon.notNull(end);

        _xOffset = 0;
        _yOffset = 0;
        _zOffset = 0;
        _location = start;
        _start = start;
        _end = end;
        _hash = 0;

        setParentNode(null);
    }

    /**
     * Constructor.
     *
     * <p>Creates a child node.</p>
     *
     * @param xParentOffset  The X axis offset from the parent location.
     * @param yParentOffset  The Y axis offset from the parent location.
     * @param zParentOffset  The Z axis offset from the parent location.
     * @param parent         The parent path node.
     */
    public AStarPathNode(AStarPathNode parent, int xParentOffset, int yParentOffset, int zParentOffset) {
        PreCon.notNull(parent);

        _xOffset = parent.getXStartOffset() + xParentOffset;
        _yOffset = parent.getYStartOffset() + yParentOffset;
        _zOffset = parent.getZStartOffset() + zParentOffset;
        _start = parent.getStartLocation();
        _end = parent.getEndLocation();
        _hash = _xOffset ^ _yOffset ^ _zOffset;

        setParentNode(parent);
    }

    @Override
    public AStarPathNode getParentNode() {
        return _parentNode;
    }

    public void setParentNode(@Nullable AStarPathNode node) {
        _parentNode = node;

        _hCalculated = false;
        _gCalculated = false;

        _xParentOffset = _parentNode == null
                ? 0
                : _xOffset - _parentNode.getXStartOffset();

        _yParentOffset = _parentNode == null
                ? 0
                : _yOffset - _parentNode.getYStartOffset();

        _zParentOffset = _parentNode == null
                ? 0
                : _zOffset - _parentNode.getZStartOffset();
    }

    @Override
    public int getXParentOffset() {
        return _xParentOffset;
    }

    @Override
    public int getYParentOffset() {
        return _yParentOffset;
    }

    @Override
    public int getZParentOffset() {
        return _zParentOffset;
    }

    /**
     * Get the X axis offset from the start location X axis.
     */
    @Override
    public int getXStartOffset() {
        return _xOffset;
    }

    /**
     * Get the Y axis offset from the start location Y axis.
     */
    @Override
    public int getYStartOffset() {
        return _yOffset;
    }

    /**
     * Get the Z axis offset from the start location Z axis.
     */
    @Override
    public int getZStartOffset() {
        return _zOffset;
    }

    /**
     * Get the start location of the path.
     */
    @Override
    public Location getStartLocation() {
        return _start;
    }

    @Override
    public Location getEndLocation() {
        return _end;
    }

    /**
     * Get the node location.
     */
    @Override
    public Location getLocation() {
        if (_location == null)
            _location = AStarUtils.getNodeLocation(_start, _xOffset, _yOffset, _zOffset);

        return _location;
    }

    /**
     * Get the node G score.
     */
    @Override
    public int getGScore() {

        if (!_gCalculated) {
            _g = _parentNode == null ? 0 : AStarUtils.getGScore(this);
            _gCalculated = true;
        }

        return _g;
    }

    /**
     * Get the node H score.
     */
    @Override
    public long getHScore() {

        if (!_hCalculated) {
            _h = _parentNode == null ? 0 : AStarUtils.getHScore(this);
            _hCalculated = true;
        }

        return _h;
    }

    /**
     * Get the node F score.
     */
    @Override
    public long getFScore() {
        return getGScore() + getHScore();
    }

    /**
     * Get the node material.
     */
    @Override
    public Material getMaterial() {
        if (_material == null)
            _material = getLocation().getBlock().getType();

        return _material;
    }

    /**
     * Determine if the node is a transparent material.
     */
    @Override
    public boolean isTransparent() {
        return MaterialExt.isTransparent(getMaterial());
    }

    /**
     * Determine if the node is a surface.
     */
    @Override
    public boolean isSurface() {
        return MaterialExt.isSurface(getMaterial());
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AStarPathNode) {
            AStarPathNode node = (AStarPathNode)obj;

            return node._xOffset == _xOffset &&
                   node._yOffset == _yOffset &&
                   node._zOffset == _zOffset;
        }
        return false;
    }

    @Override
    public int compareTo(PathNode other) {
        if (other.getFScore() < getFScore())
            return 1;
        if (other.getFScore() > getFScore())
            return -1;

        return 0;
    }



}
