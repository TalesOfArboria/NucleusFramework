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

import com.jcwhatever.nucleus.managed.astar.area.IPathAreaResult;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.managed.astar.interior.IInteriorFinderResult;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarGraphNode;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarNode;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraph;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraphBuilder;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;

import java.util.Collection;

/**
 * Global AStar implementation manager.
 */
public interface IAStarManager {

    /**
     * Create a new named node map builder.
     */
    IAStarNodeGraphBuilder getNamedNodeMapBuilder();

    /**
     * Create a new named node map builder.
     *
     * @param capacity  The expected number of nodes that will be added.
     */
    IAStarNodeGraphBuilder getNamedNodeMapBuilder(int capacity);

    /**
     * Create a new named node map.
     *
     * @param nodes  The nodes to map.
     */
    IAStarNodeGraph getNamedNodeMap(Collection<? extends AStarGraphNode> nodes);

    /**
     * Create a new coordinate settings instance.
     */
    IAStarSettings createSettings();

    /**
     * Create a new coordinate search context using custom nodes.
     *
     * @param start        The start node.
     * @param destination  The destination node.
     * @param settings     The settings to use.
     * @param examiner     The node examiner to use.
     */
    <T extends IAStarNode<T>> IAStarContext<T> createContext(
            T start, T destination,
            IAStarSettings settings, IAStarNodeExaminer<T> examiner);

    /**
     * Perform a coordinate path search for the specified search context.
     *
     * <p>The search implementation used is dependant upon which mixin the node type
     * uses. The node type may not have a mixin, or it may use {@link ICoords3Di}.</p>
     *
     * <p>When the node does not implement {@link ICoords3Di}, the implementation searches
     * for adjacent nodes solely based on the result of the {@link IAStarNode#getAdjacent}
     * methods.</p>
     *
     * @param context  The search context.
     *
     * @return  The path results.
     */
    <T extends IAStarNode<T>> IAStarResult<T> search(IAStarContext<T> context);

    /**
     * Perform a world based coordinate path search from the specified start to the
     * specified destination location using default settings.
     *
     * @param start        The start location.
     * @param destination  The destination location.
     *
     * @return  The path results.
     */
    IAStarResult<AStarNode> search(Location start, Location destination);

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
    IAStarResult<AStarNode> search(Location start, Location destination,
                                   IAStarSettings settings);

    /**
     * Search for valid path destinations around the specified path start point.
     *
     * @param start     The path start location.
     * @param settings  The validator settings to use.
     */
    IPathAreaResult searchArea(Location start, IAStarSettings settings);

    /**
     * Search for valid path destinations around the specified path start point.
     *
     * @param start     The path start location.
     * @param settings  The validator settings to use.
     * @param examiner  The node examiner to use.
     */
    IPathAreaResult searchArea(Location start,
                               IAStarSettings settings,
                               IAStarNodeExaminer<AStarNode> examiner);

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
    IInteriorFinderResult searchInterior(Location start, IRegionSelection boundaries);
}
