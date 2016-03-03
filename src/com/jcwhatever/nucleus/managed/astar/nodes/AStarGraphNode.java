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

package com.jcwhatever.nucleus.managed.astar.nodes;

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ICoords3D;
import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a nameable {@link IAStarNode} that uses the {@link IAStarGraphNode}.
 */
public class AStarGraphNode extends AbstractAStarNode<AStarGraphNode> implements IAStarGraphNode, INamed {

    private final Map<String, AStarGraphNode> _adjacent = new HashMap<>(10);
    private final String _name;

    /**
     * Constructor.
     *
     * @param coords  The node coordinates.
     */
    public AStarGraphNode(String name, ICoords3Di coords) {
        super(coords.getX(), coords.getY(), coords.getZ());
        _name = name;
    }

    /**
     * Constructor.
     *
     * @param coords  The node coordinates.
     */
    public AStarGraphNode(String name, ICoords3D coords) {
        super(coords.getFloorX(), coords.getFloorY(), coords.getFloorZ());
        _name = name;
    }

    /**
     * Constructor.
     *
     * @param location  The node coordinates.
     */
    public AStarGraphNode(String name, Location location) {
        super(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        _name = name;
    }

    /**
     * Constructor.
     *
     * @param vector  The node coordinates.
     */
    public AStarGraphNode(String name, Vector vector) {
        super(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
        _name = name;
    }

    /**
     * Constructor.
     *
     * @param x  The X coordinate.
     * @param y  The Y coordinate.
     * @param z  The Z coordinate.
     */
    public AStarGraphNode(String name, int x, int y, int z) {
        super(x, y, z);
        _name = name;
    }

    /**
     * Constructor.
     *
     * @param parent   The parent node.
     * @param offsetX  The X offset from parent.
     * @param offsetY  The Y offset from parent.
     * @param offsetZ  The Z offset from parent.
     */
    protected AStarGraphNode(AStarGraphNode parent, int offsetX, int offsetY, int offsetZ) {
        super(parent, offsetX, offsetY, offsetZ);
        _name = parent.getName() + "_x" + offsetX + 'y' + offsetY + 'z' + offsetZ;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public AStarGraphNode getRelative(int offsetX, int offsetY, int offsetZ) {
        return new AStarGraphNode(this, offsetX, offsetY, offsetZ);
    }

    @Override
    public Collection<AStarGraphNode> getAdjacent() {
        return _adjacent.values();
    }

    @Override
    public <T extends Collection<AStarGraphNode>> T getAdjacent(T output) {
        PreCon.notNull(output);

        output.addAll(_adjacent.values());
        return output;
    }

    @Override
    public boolean isAdjacent(AStarGraphNode node) {
        PreCon.notNull(node);

        return _adjacent.containsKey(node.getName());
    }

    public boolean isAdjacent(String name) {
        PreCon.notNull(name);

        return _adjacent.containsKey(name);
    }

    @Nullable
    public AStarGraphNode getAdjacent(String name) {
        PreCon.notNull(name);

        return _adjacent.get(name);
    }

    public void addAdjacent(AStarGraphNode node) {
        PreCon.notNull(node);

        _adjacent.put(node.getName(), node);
    }

    @Nullable
    public AStarGraphNode removeAdjacent(AStarGraphNode node) {
        PreCon.notNull(node);

        return _adjacent.remove(node.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AStarGraphNode
                && ((AStarGraphNode) obj).getName().equals(getName());
    }
}

