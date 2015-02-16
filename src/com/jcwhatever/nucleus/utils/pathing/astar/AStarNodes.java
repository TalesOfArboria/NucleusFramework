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


package com.jcwhatever.nucleus.utils.pathing.astar;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Collection to store {@link AStarPathNode}'s
 */
public class AStarNodes implements INodeCollection<AStarPathNode> {

    private PriorityQueue<AStarPathNode> _queue = new PriorityQueue<>(50);
    private Set<AStarPathNode> _pathNodeSet = new HashSet<AStarPathNode>(50);
    private Map<Location, AStarPathNode> _locationMap = new HashMap<>(50);

    @Override
    public int size() {
        return _pathNodeSet.size();
    }

    @Override
    public void add(AStarPathNode node) {

        if (_pathNodeSet.contains(node)) {
            _queue.remove(node);
        }

        _queue.add(node);
        _pathNodeSet.add(node);
        _locationMap.put(node.getLocation(), node);
    }

    @Nullable
    @Override
    public AStarPathNode remove() {
        AStarPathNode node = _queue.poll();
        if (node == null)
            return null;

        _pathNodeSet.remove(node);
        _locationMap.remove(node.getLocation());

        return node;
    }

    @Nullable
    @Override
    public AStarPathNode remove(Location nodeLocation) {
        AStarPathNode node = _locationMap.remove(nodeLocation);
        if (node == null)
            return null;

        _pathNodeSet.remove(node);
        _queue.remove(node);

        return node;
    }

    @Override
    @Nullable
    public AStarPathNode get(Location nodeLocation) {
        return _locationMap.get(nodeLocation);
    }

    @Override
    public boolean contains(AStarPathNode node) {
        return _pathNodeSet.contains(node);
    }

    @Override
    public boolean contains(Location nodeLocation) {
        return _locationMap.containsKey(nodeLocation);
    }

    @Override
    public void clear() {
        _queue.clear();
        _locationMap.clear();
        _pathNodeSet.clear();
    }

    @Override
    public Iterator<AStarPathNode> iterator() {
        return _queue.iterator();
    }
}
