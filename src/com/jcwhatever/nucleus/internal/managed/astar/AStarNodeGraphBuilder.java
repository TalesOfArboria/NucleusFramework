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

import com.jcwhatever.nucleus.managed.astar.nodes.AStarGraphNode;
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraphBuilder;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IAStarNodeGraphBuilder}.
 */
class AStarNodeGraphBuilder implements IAStarNodeGraphBuilder {

    private final Map<String, NodeDefinition> _definitions;

    AStarNodeGraphBuilder() {
        this(25);
    }

    public AStarNodeGraphBuilder(int capacity) {
        _definitions = new HashMap<>(capacity);
    }

    @Override
    public AStarNodeGraphBuilder add(String name, Location location, String... adjacentNames) {
        return add(name, location.getBlockX(), location.getBlockY(), location.getBlockZ(), adjacentNames);
    }

    @Override
    public AStarNodeGraphBuilder add(String name, ICoords3Di coords, String... adjacentNames) {
        return add(name, coords.getX(), coords.getY(), coords.getZ(), adjacentNames);
    }

    @Override
    public AStarNodeGraphBuilder add(String name, int x, int y, int z, String... adjacentNames) {
        AStarGraphNode node = new AStarGraphNode(name, x, y, z);
        NodeDefinition definition = new NodeDefinition(node, adjacentNames);
        _definitions.put(name, definition);
        return this;
    }

    @Override
    public AStarNodeGraph build() {

        List<AStarGraphNode> nodes = new ArrayList<>(_definitions.size());

        for (NodeDefinition definition : _definitions.values()) {

            for (String name : definition.adjacentNames) {
                NodeDefinition adjDef = _definitions.get(name);
                if (adjDef == null) {
                    throw new IllegalStateException("Could not add node named '" + name
                            + "' to node '" + definition.node.getName()
                            + "' because it was not defined in the builder.");
                }

                if (adjDef.node.equals(definition.node))
                    continue;

                definition.node.addAdjacent(adjDef.node);
            }

            nodes.add(definition.node);
        }

        return new AStarNodeGraph(nodes);
    }

    private static class NodeDefinition {
        AStarGraphNode node;
        String[] adjacentNames;

        NodeDefinition(AStarGraphNode node, String[] adjacentNames) {
            this.node = node;
            this.adjacentNames = adjacentNames;
        }
    }
}
