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

import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * AStar path finding implementation.
 */
public class AStarPathFinder extends AStar<AStarPathNode> {

    private final NodeCollection<AStarPathNode> _closedNodes = new AStarNodes();
    private final NodeCollection<AStarPathNode> _openNodes = new AStarNodes();

    private Location _start;
    private Location _end;

    /**
     * Get the start location of the most current path operation.
     */
    @Override
    @Nullable
    public Location getStartLocation() {
        return _start;
    }

    /**
     * Get the end location of the most current path operation.
     */
    @Override
    @Nullable
    public Location getEndLocation() {
        return _end;
    }

    /**
     * Get a path.
     *
     * @param start       The start location.
     * @param end         The end location.
     * @param adjustment  Specify how the provided locations should be adjusted.
     *
     * @return  Empty {@code LinkedList} if path could not be found.
     */
    @Override
    public LinkedList<AStarPathNode> getPath(Location start, Location end, LocationAdjustment adjustment) {

        LinkedList<AStarPathNode> results = new LinkedList<>();

        if (adjustment == LocationAdjustment.FIND_SURFACE) {
            start = LocationUtils.findSurfaceBelow(start);
            end = LocationUtils.findSurfaceBelow(end);
        }

        if (start == null || end == null)
            return results;

        _start = start;
        _end = end;

        AStarPathNode destination = searchDestination(start, end);
        if (destination == null)
            return results;

        results.push(destination);

        destination = destination.getParentNode();

        while(destination != null) {

            results.push(destination);
            destination = destination.getParentNode();
        }

        return results;
    }

    /**
     * Get a path and return the number of blocks traversed
     * to get the the end.
     *
     * @param start  The start location.
     * @param end    The end location.
     * @param adjustment  Specify how the provided locations should be adjusted.
     *
     * @return  -1 if no path found.
     */
    @Override
    public int getPathDistance(Location start, Location end, LocationAdjustment adjustment) {

        if (adjustment == LocationAdjustment.FIND_SURFACE) {
            start = LocationUtils.findSurfaceBelow(start);
            end = LocationUtils.findSurfaceBelow(end);
        }

        if (start == null || end == null)
            return -1;

        _start = start;
        _end = end;

        PathNode destination = searchDestination(start, end);
        if (destination == null)
            return -1;

        int distance = 0;

        while (destination != null) {
            destination = destination.getParentNode();
            distance++;
        }

        return distance;
    }

    @Override
    protected NodeCollection<AStarPathNode> getOpenNodes() {
        return _openNodes;
    }

    @Override
    protected NodeCollection<AStarPathNode> getClosedNodes() {
        return _closedNodes;
    }

    @Override
    protected void addOpenNode(AStarPathNode candidate) {

        AStarPathNode open = getOpenNodes().get(candidate.getLocation());

        if (open != null && candidate.getGScore() < open.getGScore()) {
            open.setParentNode(candidate.getParentNode());
        }
        else {
            _openNodes.add(candidate);
        }
    }

    @Override
    protected AStarPathNode createStartNode(Location start, Location end) {
        return new AStarPathNode(start, end);
    }

    @Override
    protected AStarPathNode createNodeChild(AStarPathNode parent, int offsetX, int offsetY, int offsetZ) {
        return new AStarPathNode(parent, offsetX, offsetY, offsetZ);
    }

}
