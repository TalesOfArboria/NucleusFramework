package com.jcwhatever.bukkit.generic.pathing.astar;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Collection to store {@code AStarPathNode}'s
 */
public class AStarNodes implements NodeCollection<AStarPathNode> {

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
