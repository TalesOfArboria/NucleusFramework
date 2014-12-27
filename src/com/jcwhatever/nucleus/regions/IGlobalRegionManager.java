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

package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.regions.Region.PriorityType;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * Global region manager interface
 */
public interface IGlobalRegionManager {

    /**
     * Get number of regions registered.
     */
    int getRegionCount();

    /**
     * Get a list of regions that contain the specified location.
     *
     * @param location  The location to check.
     */
    List<IRegion> getRegions(Location location);

    /**
     * Get a list of regions that contain the specified location.
     *
     * @param world  The world to check.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     */
    List<IRegion> getRegions(World world, int x, int y, int z);

    /**
     * Get a list of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param location  The location to check.
     */
    List<IRegion> getListenerRegions(Location location);

    /**
     * Get a list of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param world  The world to check.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     */
    List<IRegion> getListenerRegions(World world, int x, int y, int z);

    /**
     * Get a list of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param location      The location to check.
     * @param priorityType  The priority sorting type of the returned list.
     */
    List<IRegion> getListenerRegions(Location location, PriorityType priorityType);

    /**
     * Get a list of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param world         The world to check.
     * @param x             The X coordinates.
     * @param y             The Y coordinates.
     * @param z             The Z coordinates.
     * @param priorityType  The priority sorting type of the returned list.
     */
    List<IRegion> getListenerRegions(World world, int x, int y, int z, PriorityType priorityType);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param chunk  The chunk to check.
     */
    Set<IRegion> getRegionsInChunk(Chunk chunk);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunks X coordinates.
     * @param z      The chunks Z coordinates.
     */
    Set<IRegion> getRegionsInChunk(World world, int x, int z);

    /**
     * Get all regions that player is currently in.
     *
     * @param p  The player to check.
     */
    List<IRegion> getPlayerRegions(Player p);
}
