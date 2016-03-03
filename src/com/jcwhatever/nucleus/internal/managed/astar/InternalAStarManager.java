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

package com.jcwhatever.nucleus.internal.managed.astar;

import com.jcwhatever.nucleus.managed.astar.IAStarContext;
import com.jcwhatever.nucleus.managed.astar.IAStarManager;
import com.jcwhatever.nucleus.managed.astar.IAStarResult;
import com.jcwhatever.nucleus.managed.astar.IAStarSettings;
import com.jcwhatever.nucleus.managed.astar.area.IPathAreaResult;
import com.jcwhatever.nucleus.managed.astar.examiners.AStarWorldExaminer;
import com.jcwhatever.nucleus.managed.astar.examiners.IAStarNodeExaminer;
import com.jcwhatever.nucleus.managed.astar.interior.IInteriorFinderResult;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarGraphNode;
import com.jcwhatever.nucleus.managed.astar.nodes.AStarNode;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarGraphNode;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraph;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraphBuilder;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNode;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import org.bukkit.Location;

import java.util.Collection;

/**
 * Implementation of {@link IAStarManager}.
 */
public class InternalAStarManager implements IAStarManager {

    @Override
    public IAStarNodeGraphBuilder getNamedNodeMapBuilder() {
        return new AStarNodeGraphBuilder();
    }

    @Override
    public IAStarNodeGraphBuilder getNamedNodeMapBuilder(int capacity) {
        return new AStarNodeGraphBuilder(capacity);
    }

    @Override
    public IAStarNodeGraph getNamedNodeMap(Collection<? extends AStarGraphNode> nodes) {
        return new AStarNodeGraph(nodes);
    }

    @Override
    public IAStarSettings createSettings() {
        return new AStarSettings();
    }

    @Override
    public <T extends IAStarNode<T>> IAStarContext<T> createContext(
            T start, T destination, IAStarSettings settings,
            IAStarNodeExaminer<T> examiner) {

        return new AStarContext<T>(start, destination, examiner, settings);
    }

    @Override
    public <T extends IAStarNode<T>> IAStarResult<T> search(IAStarContext<T> context) {
        PreCon.notNull(context);

        return context.getStart() instanceof IAStarGraphNode
                ? AStarGraphSearch.<T>get().search(context)
                : AStarCoordsSearch.<T>get().search(context);
    }

    @Override
    public IAStarResult<AStarNode> search(Location start, Location destination) {

        return search(start, destination, new AStarSettings());
    }

    @Override
    public IAStarResult<AStarNode> search(Location start, Location destination,
                                          IAStarSettings settings) {
        PreCon.notNull(start, "start");
        PreCon.notNull(destination, "destination");
        PreCon.notNull(settings, "settings");
        PreCon.notNull(start.getWorld(), "start world");

        Location startBelow = LocationUtils.findSurfaceBelow(start);
        Location destBelow = LocationUtils.findSurfaceBelow(destination);

        AStarNode startNode = new AStarNode(startBelow);
        AStarNode destNode = new AStarNode(destBelow);

        AStarContext<AStarNode> context =
                new AStarContext<AStarNode>(startNode, destNode,
                        new AStarWorldExaminer<AStarNode>(start.getWorld()), settings);

        return AStarCoordsSearch.<AStarNode>get().search(context);
    }

    @Override
    public IPathAreaResult searchArea(Location start, IAStarSettings settings) {
        return PathAreaFinder.get().search(start, settings);
    }

    @Override
    public IPathAreaResult searchArea(Location start, IAStarSettings settings,
                                      IAStarNodeExaminer<AStarNode> examiner) {

        return PathAreaFinder.<AStarNode>get().search(settings, examiner, start);
    }

    @Override
    public IInteriorFinderResult searchInterior(Location start, IRegionSelection boundaries) {
        return InteriorFinder.get().search(start, boundaries);
    }
}
