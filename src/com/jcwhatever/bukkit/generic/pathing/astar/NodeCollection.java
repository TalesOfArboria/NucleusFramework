package com.jcwhatever.bukkit.generic.pathing.astar;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * A collection of path nodes.
 *
 * @param <T>  Path node type.
 */
public interface NodeCollection<T extends PathNode> {

    /**
     * Get the number of nodes in the collection.
     */
    int size();

    /**
     * Add a node to the collection.
     *
     * @param node  The node to add.
     */
    void add(T node);

    /**
     * Remove a node from the collection.
     * <p>
     *     Should return the best candidate node.
     * </p>
     */
    @Nullable
    T remove();

    /**
     * Remove a specific node from the collection.
     *
     * @param nodeLocation  The node location.
     */
    @Nullable
    T remove(Location nodeLocation);

    /**
     * Get a node from the collection.
     *
     * @param nodeLocation  The node location.
     */
    @Nullable
    T get(Location nodeLocation);

    /**
     * Determine if the collection contains a node.
     *
     * @param node  The node to check.
     */
    boolean contains(T node);

    /**
     * Determine if the collection contains a node.
     *
     * @param nodeLocation  The location of the node to check.
     */
    boolean contains(Location nodeLocation);

    /**
     * Clear all nodes from the collection.
     */
    void clear();

    /**
     * Get an iterator from the collection.
     */
    Iterator<T> iterator();
}
