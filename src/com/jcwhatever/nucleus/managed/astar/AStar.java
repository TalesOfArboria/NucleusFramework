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

package com.jcwhatever.nucleus.managed.astar;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.astar.area.IPathAreaResult;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.managed.astar.interior.IInteriorFinderResult;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarGraphNode;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarNode;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraph;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraphBuilder;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.coords.ICoords2D;
import com.jcwhatever.nucleus.utils.coords.ICoords2Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;

import java.util.Collection;

/**
 * Static convenience methods for global AStar manager.
 */
public final class AStar {

    private AStar() {}

    /**
     * Create a new named node map builder.
     */
    public static IAStarNodeGraphBuilder getNamedNodeMapBuilder() {
        return manager().getNamedNodeMapBuilder();
    }

    /**
     * Create a new named node map.
     *
     * @param nodes  The nodes to map.
     */
    public static IAStarNodeGraph getNamedNodeMap(Collection<? extends AStarGraphNode> nodes) {
        return manager().getNamedNodeMap(nodes);
    }

    /**
     * Create a new coordinate settings instance.
     */
    public static IAStarSettings createSettings() {
        return manager().createSettings();
    }

    /**
     * Create a new coordinate search context using custom nodes.
     *
     * @param start        The start coordinates.
     * @param destination  The destination coordinates.
     * @param settings     The settings to use.
     * @param examiner     The node examiner to use.
     */
    public static <T extends IAStarNode<T>> IAStarContext<T> createContext(
            T start, T destination, IAStarSettings settings,
            IAStarNodeExaminer<T> examiner) {

        return manager().createContext(start, destination, settings, examiner);
    }

    /**
     * Perform a coordinate path search for the specified search context.
     *
     * <p>The search implementation used is dependant upon which mixin the node type
     * uses. The node type may not have a mixin, or it may use {@link ICoords2Di},
     * {@link ICoords2D}, {@link ICoords3Di}, or {@link ICoords3D} </p>
     *
     * @param context  The search context.
     *
     * @return  The path results.
     */
    public static <T extends IAStarNode<T>> IAStarResult<T> search(IAStarContext<T> context) {
        return manager().search(context);
    }

    /**
     * Perform a world based coordinate path search from the specified start to the
     * specified destination location using default settings.
     *
     * @param start        The start location.
     * @param destination  The destination location.
     *
     * @return  The path results.
     */
    public static IAStarResult<AStarNode> search(Location start, Location destination) {
        return manager().search(start, destination);
    }

    /**
     * Perform a world based coordinate path search from the specified start to the
     * specified destination location using default settings.
     *
     * @param start        The start location.
     * @param destination  The destination location.
     * @param settings     The settings to use.
     *
     * @return  The path results.
     */
    public static IAStarResult<AStarNode> search(Location start, Location destination,
                                                 IAStarSettings settings) {
        return manager().search(start, destination, settings);
    }

    /**
     * Search for valid path destinations around the specified path start point.
     *
     * @param start     The path start location.
     * @param settings  The validator settings to use.
     */
    public static IPathAreaResult searchArea(Location start, IAStarSettings settings) {
        return manager().searchArea(start, settings);
    }

    /**
     * Search for valid path destinations around the specified path start point.
     *
     * @param start     The path start location.
     * @param settings  The validator settings to use.
     * @param examiner  The node examiner to use.
     */
    public static IPathAreaResult searchArea(Location start, IAStarSettings settings,
                                             IAStarNodeExaminer<AStarNode> examiner) {

        return manager().searchArea(start, settings, examiner);
    }

    /**
     * Search for air blocks within a region without moving
     * outside of structural boundaries.
     *
     * <p>The structure must be completely enclosed with no block open to the exterior.</p>
     *
     * <p>Does not search through doors even if they are open.</p>
     *
     * @param start       The location to start the search from.
     * @param boundaries  The region boundaries to prevent searching endlessly into the world.
     */
    public static IInteriorFinderResult searchInterior(Location start, IRegionSelection boundaries) {
        return manager().searchInterior(start, boundaries);
    }

    private static IAStarManager manager() {
        return Nucleus.getAStarManager();
    }
}
