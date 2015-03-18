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

import com.jcwhatever.nucleus.regions.options.RegionPriority.PriorityType;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Global region manager interface
 */
public interface IGlobalRegionManager {

    /**
     * Get number of regions registered.
     */
    int getRegionCount();

    /**
     * Determine if there is at least one region at the specified location
     * of the specified type.
     *
     * @param location     The location to check.
     * @param regionClass  The class of the region type to check.
     */
    <T extends IRegion> boolean hasRegion(Location location, Class<T> regionClass);

    /**
     * Determine if there is at least one region at the specified location
     * of the specified type.
     *
     * @param world        The world to check in.
     * @param x            The X coordinates to check.
     * @param y            The Y coordinates to check.
     * @param z            The Z coordinates to check.
     * @param regionClass  The class of the region type to check.
     */
    <T extends IRegion> boolean hasRegion(World world, int x, int y, int z, Class<T> regionClass);

    /**
     * Get a region created by the specified {@link org.bukkit.plugin.Plugin} with
     * the specified name.
     *
     * @param plugin  The regions owning plugin.
     * @param name    The name of the region.
     *
     * @return  The {@link IRegion} or null if not found.
     */
    @Nullable
    IRegion getRegion(Plugin plugin, String name);

    /**
     * Get a list of regions that contain the specified location.
     *
     * @param location  The location to check.
     */
    List<IRegion> getRegions(Location location);

    /**
     * Get a list of regions that contain the specified location.
     *
     * @param location     The location to check.
     * @param regionClass  The class of the regions to get.
     */
    <T extends IRegion> List<T> getRegions(Location location, Class<T> regionClass);

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
     * Get a list of regions that contain the specified location.
     *
     * @param world        The world to check.
     * @param x            The x coordinates.
     * @param y            The y coordinates.
     * @param z            The z coordinates.
     * @param regionClass  The class of the region type to get.
     */
    <T extends IRegion> List<T> getRegions(World world, int x, int y, int z, Class<T> regionClass);

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
     * @param location     The location to check.
     * @param regionClass  The class of the region type to get.
     */
    <T extends IRegion> List<T> getListenerRegions(Location location, Class<T> regionClass);

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
     * @param world        The world to check.
     * @param x            The x coordinates.
     * @param y            The y coordinates.
     * @param z            The z coordinates.
     * @param regionClass  The class of the region type to get.
     */
    <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z, Class<T> regionClass);

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
     * @param location      The location to check.
     * @param priorityType  The priority sorting type of the returned list.
     * @param regionClass   The class of the regions to get.
     */
    <T extends IRegion> List<T> getListenerRegions(Location location, PriorityType priorityType,
                                                   Class<T> regionClass);

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
     * Get a list of regions that the specified location
     * is inside of and are player watchers/listeners.
     *
     * @param world         The world to check.
     * @param x             The X coordinates.
     * @param y             The Y coordinates.
     * @param z             The Z coordinates.
     * @param priorityType  The priority sorting type of the returned list.
     * @param regionClass   The class of the region type to get.
     */
    <T extends IRegion> List<T> getListenerRegions(World world, int x, int y, int z,
                                                   PriorityType priorityType, Class<T> regionClass);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param chunk  The chunk to check.
     */
    List<IRegion> getRegionsInChunk(Chunk chunk);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param chunk        The chunk to check.
     * @param regionClass  The class of the region type to get.
     */
    <T extends IRegion> List<T> getRegionsInChunk(Chunk chunk, Class<T> regionClass);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param world  The world the chunk is in.
     * @param x      The chunks X coordinates.
     * @param z      The chunks Z coordinates.
     */
    List<IRegion> getRegionsInChunk(World world, int x, int z);

    /**
     * Get all regions that intersect with the specified chunk.
     *
     * @param world        The world the chunk is in.
     * @param x            The chunks X coordinates.
     * @param z            The chunks Z coordinates.
     * @param regionClass  The class of the region type to get.
     */
    <T extends IRegion> List<T> getRegionsInChunk(World world, int x, int z, Class<T> regionClass);

    /**
     * Get all regions that a player is currently in.
     *
     * @param player  The player to check.
     */
    List<IRegion> getPlayerRegions(Player player);

    /**
     * Causes the {@link IGlobalRegionManager} to "forget" that a player is
     * already in the specified region. Useful if the region enter event
     * needs to be re-fired for a player.
     *
     * @param player   The player to forget.
     * @param region   The region to forget the player is in.
     */
    public void forgetPlayer(Player player, IRegion region);
}
