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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * Abstract implementation of the AStar algorithm.
 *
 * <p>Provides a large portion of the core implementation.</p>
 *
 * @param <T>  The {@link IPathNode} type.
 */
public abstract class AStar<T extends IPathNode> {

    private int _maxTravelDistance = -1;
    private int _maxRange = 20;
    private int _maxDropHeight = 3;
    private int _maxIterations = -1;
    private int _entityHeight = 2;
    private DoorPathMode _doorMode = DoorPathMode.OPEN;

    /**
     * Specifies how doorways are handled.
     */
    public enum DoorPathMode  {
        /**
         * Path through open doorways
         */
        OPEN,

        /**
         * Always path through doorways even if they
         * are closed.
         */
        IGNORE_CLOSED,

        /**
         * Never path through doorways even if they
         * are open.
         */
        IGNORE_OPEN,
    }

    /**
     * Specifies how supplied path locations
     * should be adjusted.
     */
    public enum LocationAdjustment {
        /**
         * Used supplied locations as is.
         */
        NONE,

        /**
         * Find a proper surface below from the provided
         * locations.
         */
        FIND_SURFACE
    }

    /**
     * Get the max range setting.
     */
    public int getMaxRange() {
        return _maxRange;
    }

    /**
     * Set the max range.
     *
     * @param maxRange  The max range.
     */
    public void setMaxRange(int maxRange) {
        _maxRange = maxRange;
    }

    /**
     * Get the max height the path can drop down from.
     */
    public int getMaxDropHeight() {
        return _maxDropHeight;
    }

    /**
     * Set the max height the path can drop down from.
     *
     * @param maxHeight  The max height.
     */
    public void setMaxDropHeight(int maxHeight) {
        _maxDropHeight = (byte)maxHeight;
    }

    /**
     * Get the maximum number of iterations that are allowed.
     */
    public int getMaxIterations() {
        return _maxIterations;
    }

    /**
     * Set the maximum number of iterations that are allowed.
     *
     * @param iterations  The max iterations.
     */
    public void setMaxIterations(int iterations) {
        _maxIterations = iterations;
    }

    /**
     * Get the block height of the entity the path
     * is being created for.
     */
    public int getEntityHeight() {
        return _entityHeight;
    }

    /**
     * Set the block height of the entity the path
     * is being created for.
     *
     * @param height  The entity height.
     */
    public void setEntityHeight(int height) {
        PreCon.greaterThanZero(height);

        _entityHeight = height;
    }

    /**
     * Get the max path travel distance.
     */
    public int getMaxTravelDistance() {
        return _maxTravelDistance;
    }

    /**
     * Set the max path travel distance.
     *
     * @param maxTravelDistance  The maximum distance in number of blocks.
     */
    public void setMaxTravelDistance(int maxTravelDistance) {
        _maxTravelDistance = maxTravelDistance;
    }

    /**
     * Determine if doors should be checked to see if they are
     * open.
     */
    public DoorPathMode getDoorPathMode() {
        return _doorMode;
    }

    /**
     * Set door path mode.
     *
     * @param doorMode  The path mode to set.
     */
    public void setDoorPathMode(DoorPathMode doorMode) {
        _doorMode = doorMode;
    }

    /**
     * Get the start location of the current path.
     */
    @Nullable
    public abstract Location getStartLocation();

    /**
     * Get the end location of the current path.
     */
    public abstract Location getEndLocation();

    /**
     * Get a path from the specified start location to the
     * specified end location.
     *
     * @param start  The start location.
     * @param end    The end location.
     * @param adjustment  Specify how the provided locations should be adjusted.
     *
     * @return  Empty {@link java.util.LinkedList} if no path was found.
     */
    public abstract LinkedList<T> getPath(Location start, Location end, LocationAdjustment adjustment);

    /**
     * Get a path from the specified start location to the
     * specified end location and return the number of
     * blocks traversed to get there.
     *
     * @param start  The start location.
     * @param end    The end location.
     * @param adjustment  Specify how the provided locations should be adjusted.
     *
     * @return  -1 if no path was found.
     */
    public abstract int getPathDistance(Location start, Location end, LocationAdjustment adjustment);

    /**
     * Called to get the open nodes collection.
     */
    protected abstract INodeCollection<T> getOpenNodes();

    /**
     * Called to get the closed nodes collection.
     */
    protected abstract INodeCollection<T> getClosedNodes();

    /**
     * Called to add a node candidate to the open nodes collection.
     *
     * @param candidate  The candidate to add.
     */
    protected abstract void addOpenNode(T candidate);

    /**
     * Called to create a new instance of a start node.
     *
     * @param start  The path start location.
     * @param end    The path end location.
     */
    protected abstract T createStartNode(Location start, Location end);

    /*
     * Called to create a child node for the specified parent node
     *
     * Offsets are offsets from parent
     */
    protected abstract T createNodeChild(T parent, int offsetX, int offsetY, int offsetZ);



    /**
     *  Called to find a path to the destination node. If a path is not found,
     *  null is returned. Otherwise, the destination path node is returned
     *  and the path can be found by traversing its parent nodes.
     */
    @Nullable
    protected T searchDestination(Location start, Location end) {

        // validate range
        if (start.distance(end) > getMaxRange()) {
            return null;
        }

        getOpenNodes().clear();
        getClosedNodes().clear();

        T startNode = createStartNode(start, end);

        // Add start node to open nodes
        getOpenNodes().add(startNode);

        // Add valid adjacent nodes to open list
        findCandidates(startNode);

        T current = null;

        // iterate until destination is found
        // or unable to continue

        int iterations = 0;
        int maxIterations = getMaxIterations();
        while (canSearch()) {

            // get candidate/open node closest to destination
            current = pickCandidate();
            if (current == null)
                break;

            // search for more candidate nodes
            findCandidates(current);

            iterations ++;

            if (maxIterations > 0 && iterations >= maxIterations) {
                break;
            }
        }

        // no valid nodes found
        if (current == null) {
            return null;
        }

        // see if destination node was reached
        if (!getClosedNodes().contains(end)) {
            return null;
        }

        return current;
    }


    /**
     * Called to find all nodes adjacent to the specified node that
     * are valid next in path locations and add them to the
     * open nodes map. These valid nodes are referred to as
     * candidates.
     *
     * <p>Candidates are added to the open nodes collection.</p>
     *
     * @param node  The node to search, is parent to candidates
     */
    protected void findCandidates(T node) {

        // column validations, work from top down, skip columns that are false
        boolean[][] columns = getNewSearchColumns();
        int dropHeight = -(getMaxDropHeight());

        for (byte y = 1; y >= dropHeight; y--) {
            for (byte x = -1; x <= 1; x++) {
                for (byte z = -1; z <= 1; z++) {

                    if (!columns[x + 1][z + 1])
                        continue;

                    // get instance of candidate node
                    T candidate = createNodeChild(node, x, y, z);

                    if (!validateCandidate(candidate, columns))
                        continue;

                    addOpenNode(candidate);

                }
            }
        }
    }

    /**
     * Determine if a node is a possible path candidate.
     *
     * @param candidate        The candidate node.
     * @param columns          Validation columns.
     */
    protected boolean validateCandidate(T candidate, boolean[][] columns) {

        @SuppressWarnings("unchecked") T candidateParent = (T)candidate.getParentNode();
        if (candidateParent == null)
            return true; // null parent means start location

        int x = candidate.getXParentOffset();
        int y = candidate.getYParentOffset();
        int z = candidate.getZParentOffset();
        int maxRange = getMaxRange();

        // check if candidate is already closed
        if (getClosedNodes().contains(candidate)) {
            return false;
        }

        Material material = candidate.getMaterial();

        // check x & z range
        if ((maxRange - Math.abs(candidateParent.getXStartOffset()) < 0) ||
                (maxRange - Math.abs(candidateParent.getZStartOffset()) < 0)) {

            invalidateColumn(columns, x, z, material);
            return false;
        }

        // check y range
        if ((maxRange - Math.abs(candidateParent.getYStartOffset()) < 0)) {
            return false;
        }

        // Check for diagonal obstruction
        if (x != 0 && z != 0 && y >= 0) {
            T diagX = createNodeChild(candidateParent, x, y, 0),
                    diagZ = createNodeChild(candidateParent, 0, y, z);

            boolean isXValid = AStarUtils.hasRoomForEntity(diagX.getLocation(), getEntityHeight(), getDoorPathMode());
            boolean isZValid = AStarUtils.hasRoomForEntity(diagZ.getLocation(), getEntityHeight(), getDoorPathMode());

            // prevent walking through diagonal walls
            if(y == 0 && !isXValid && !isZValid ) {
                invalidateColumn(columns, x, z, material);
                return false;
            }

            // prevent walking through corners
            if (!isXValid || !isZValid) {
                return false;
            }
        }

        // check candidate to see if its valid
        if (!isValid(candidate)) {

            // invalidate column if material is NOT transparent
            if (!candidate.isTransparent()) {
                invalidateColumn(columns, x, z, material);
            }

            return false;
        }

        return true;
    }

    /**
     * Called to pick a candidate from the open nodes collection
     * with the lowest F score.
     */
    @Nullable
    protected T pickCandidate() {

        T candidate = getOpenNodes().remove();

        if (candidate != null) {
            closeNode(candidate);
        }

        return candidate;
    }

    /**
     * Called to close a node and remove it from the open
     * node collection.
     *
     * @param node  The node to close.
     */
    protected void closeNode(T node) {
        getOpenNodes().remove(node.getLocation());
        getClosedNodes().add(node);
    }

    /**
     * Called to check if open list is empty, if it is no path has been found
     */
    protected boolean canSearch() {
        return getOpenNodes().size() != 0 && !getClosedNodes().contains(getEndLocation()) &&
                (_maxTravelDistance == -1 || getClosedNodes().size() <= _maxTravelDistance);
    }

    /**
     * Called to get a new search column validation array.
     */
    protected boolean[][] getNewSearchColumns() {
        return new boolean[][] {
                { true, true,  true },
                { true, false, true },
                { true, true,  true }
        };
    }

    /*
     * Called to invalidate a search column.
     */
    protected void invalidateColumn(boolean[][] columns, int x, int z, Material material) {

        if (Materials.isOpenable(material))
            return;

        columns[x + 1][z + 1] = false;
    }

    /**
     * Called to determine if a path node is a valid path location.
     */
    protected boolean isValid(T node) {

        // The block must be a surface
        return node.isSurface() &&
                AStarUtils.hasRoomForEntity(node.getLocation(), getEntityHeight(), getDoorPathMode());
    }
}
