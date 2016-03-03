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
import com.jcwhatever.nucleus.managed.astar.nodes.IAStarNodeGraph;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of {@link IAStarNodeGraph}.
 */
class AStarNodeGraph implements IAStarNodeGraph {

    private Map<String, AStarGraphNode> _map = new HashMap<>(10);
    private final MutableCoords3Di _coordMatcher = new MutableCoords3Di();

    AStarNodeGraph(Collection<? extends AStarGraphNode> nodes) {
        for (AStarGraphNode node : nodes) {
            _map.put(node.getName(), node);
        }
    }

    @Override
    public AStarGraphNode get(String name) {
        PreCon.notNull(name);

        return _map.get(name);
    }

    @Override
    @Nullable
    public AStarGraphNode getClosest(Location source, double radius) {
        return getClosest(source, radius, null);
    }

    @Override
    @Nullable
    public AStarGraphNode getClosest(Location source, double radius,
                                     @Nullable IValidator<AStarGraphNode> validator) {
        PreCon.notNull(source);

        Coords3Di coords = matcher(source.getBlockX(), source.getBlockY(), source.getBlockZ(), _coordMatcher);
        return getClosest(coords, radius, validator);
    }

    @Override
    @Nullable
    public AStarGraphNode getClosest(ICoords3Di source, double radius) {
        return getClosest(source, radius, null);
    }

    @Override
    @Nullable
    public AStarGraphNode getClosest(ICoords3Di source, double radius,
                                     @Nullable IValidator<AStarGraphNode> validator) {

        PreCon.notNull(source);
        PreCon.positiveNumber(radius);

        AStarGraphNode closest = null;
        double closestDistSq = 0D;
        double radiusSq = radius * radius;

        for (AStarGraphNode node : _map.values()) {
            double distanceSq = Coords3Di.distanceSquared(source, node);
            if (distanceSq > radiusSq)
                continue;

            if (closest == null || distanceSq < closestDistSq) {

                if (validator != null && !validator.isValid(node))
                    continue;

                closest = node;
                closestDistSq = distanceSq;
            }
        }

        return closest;
    }

    @Override
    public Collection<AStarGraphNode> getAll() {
        return _map.values();
    }

    @Override
    public <T extends Collection<AStarGraphNode>> T getAll(T output) {
        PreCon.notNull(output);

        output.addAll(_map.values());
        return output;
    }

    @Override
    public Collection<String> getNames() {
        return getNames(new ArrayList<String>(_map.size()));
    }

    @Override
    public <T extends Collection<String>> T getNames(T output) {
        PreCon.notNull(output);

        output.addAll(_map.keySet());
        return output;
    }

    private Coords3Di matcher(int x, int y, int z, MutableCoords3Di output) {
        output.setX(x);
        output.setY(y);
        output.setZ(z);
        return output;
    }

    @Override
    public Iterator<AStarGraphNode> iterator() {
        return _map.values().iterator();
    }
}
