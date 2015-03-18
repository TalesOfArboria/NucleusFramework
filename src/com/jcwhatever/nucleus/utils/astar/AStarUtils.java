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

package com.jcwhatever.nucleus.utils.astar;

import com.jcwhatever.nucleus.utils.Coords3Di;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.basic.AStarNodeContainer;
import com.jcwhatever.nucleus.utils.astar.basic.AStarWorldExaminer;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * {@link AStar} convenience utilities.
 *
 * @see AStar
 */
public class AStarUtils {

    private AStarUtils() {}

    /**
     * Create a new {@link AStar} instance that uses an {@link AStarWorldExaminer}.
     *
     * @param world  The {@link org.bukkit.World} the examiner is for.
     *
     * @return  A new {@link AStar} instance.
     */
    public static AStar getAStar(World world) {
        PreCon.notNull(world);

        AStarWorldExaminer examiner = new AStarWorldExaminer(world);
        return new AStar(examiner);
    }

    /**
     * Perform an A-Star search using the specified {@link AStar} instance and
     * {@link org.bukkit.Location}'s.
     *
     * <p>Converts locations to block coordinates</p>
     *
     * <p>Uses {@link AStarNodeContainer} for search.</p>
     *
     * @param astar        The {@link AStar} instance to use.
     * @param start        The start {@link org.bukkit.Location}.
     * @param destination  The destination {@link org.bukkit.Location}.
     *
     * @return  The {@link AStarResult} returned by the {@link AStar} instance.
     *
     * @see AStar#search
     */
    public static AStarResult search(AStar astar, Location start, Location destination) {
        PreCon.notNull(astar);
        PreCon.notNull(start);
        PreCon.notNull(destination);

        start = LocationUtils.getBlockLocation(start);
        destination = LocationUtils.getBlockLocation(destination);

        return astar.search(Coords3Di.fromLocation(start), Coords3Di.fromLocation(destination),
                new AStarNodeContainer(astar.getExaminer()));
    }

    /**
     * Perform an A-Star search using the specified {@link AStar} instance and
     * {@link org.bukkit.Location}'s.
     *
     * <p>Finds nearest surface below specified locations.</p>
     *
     * <p>Converts locations to block coordinates</p>
     *
     * <p>Uses {@link AStarNodeContainer} for search.</p>
     *
     * @param astar        The {@link AStar} instance to use.
     * @param start        The start {@link org.bukkit.Location}.
     * @param destination  The destination {@link org.bukkit.Location}.
     *
     * @return  The {@link AStarResult} returned by the {@link AStar} instance.
     *
     * @see AStar#search
     */
    public static AStarResult searchSurface(AStar astar, Location start, Location destination) {
        PreCon.notNull(astar);
        PreCon.notNull(start);
        PreCon.notNull(destination);

        start = LocationUtils.getBlockLocation(start);
        destination = LocationUtils.getBlockLocation(destination);

        LocationUtils.findSurfaceBelow(start, start);
        LocationUtils.findSurfaceBelow(destination, destination);

        return astar.search(Coords3Di.fromLocation(start), Coords3Di.fromLocation(destination),
                new AStarNodeContainer(astar.getExaminer()));
    }
 }
