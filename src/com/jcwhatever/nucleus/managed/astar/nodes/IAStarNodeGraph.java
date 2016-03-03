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

import com.jcwhatever.nucleus.utils.coords.ICoords3Di;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A named node mapping.
 *
 * <p>Used to define named node relationships.</p>
 */
public interface IAStarNodeGraph extends Iterable<AStarGraphNode>{

    /**
     * Get a node in the map by name.
     *
     * @param name  The case sensitive name of the node.
     *
     * @return  The node or null if not found.
     */
    @Nullable
    AStarGraphNode get(String name);

    /**
     * Get the node closest to the specified source within the specified
     * radius.
     *
     * @param source  The source location.
     * @param radius  The radius.
     *
     * @return  The node or null if none found.
     */
    @Nullable
    AStarGraphNode getClosest(Location source, double radius);

    /**
     * Get the node closest to the specified source within the specified
     * radius.
     *
     * @param source     The source location.
     * @param radius     The radius.
     * @param validator  Optional validator to customize node filtering.
     *
     * @return  The node or null if none found.
     */
    @Nullable
    AStarGraphNode getClosest(Location source, double radius,
                                     @Nullable IValidator<AStarGraphNode> validator);

    /**
     * Get the node closest to the specified source within the specified
     * radius.
     *
     * @param source  The source coordinates.
     * @param radius  The radius.
     *
     * @return  The node or null if none found.
     */
    @Nullable
    AStarGraphNode getClosest(ICoords3Di source, double radius);

    /**
     * Get the node closest to the specified source within the specified
     * radius.
     *
     * @param source     The source coordinates.
     * @param radius     The radius limit.
     * @param validator  Optional validator to customize node filtering.
     *
     * @return  The node or null if none found.
     */
    @Nullable
    AStarGraphNode getClosest(ICoords3Di source, double radius,
                                     @Nullable IValidator<AStarGraphNode> validator);

    /**
     * Get all nodes in the map.
     */
    Collection<AStarGraphNode> getAll();

    /**
     * Add all nodes in the map to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<AStarGraphNode>> T getAll(T output);

    /**
     * Get the names of all nodes in the map.
     */
    Collection<String> getNames();

    /**
     * Add the names of all nodes in the map to the specified output collection.
     *
     * @param output  The output collection.
     *
     * @return  The output collection.
     */
    <T extends Collection<String>> T getNames(T output);
}
