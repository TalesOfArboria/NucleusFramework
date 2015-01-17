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


package com.jcwhatever.nucleus.utils.validate;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of worlds used to blacklist or
 * whitelist worlds for various purposes.
 */
public class WorldValidator extends StoredValidationSet<String> {

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public WorldValidator(IDataNode dataNode) {
        super(dataNode);
    }

    /**
     * Determine if a world is considered valid.
     *
     * @param world  The world to check.
     */
    public boolean isValid(World world) {
        PreCon.notNull(world);

        return super.isValid(world.getName());
    }

    /**
     * Add a world to the collection.
     *
     * @param world  The world to add.
     */
    public boolean add(World world) {
        PreCon.notNull(world);

        return super.add(world.getName());
    }

    /**
     * Remove a world from the collection.
     *
     * @param world  The world to removed.
     */
    public boolean remove(World world) {
        PreCon.notNull(world);

        return super.remove(world.getName());
    }

    @Override
    protected String getElementNodeName(Object element) {
        if (element instanceof String)
            return (String)element;
        else if (element instanceof World) {
            return ((World) element).getName();
        }
        return null;
    }

    @Override
    protected void saveElement(String element, IDataNode dataNode) {
        _dataNode.set("worlds", new ArrayList<>(this));
        _dataNode.save();
    }

    @Override
    protected String loadElement(IDataNode dataNode) {
        return null; // not used
    }

    @Override
    protected void load() {
        _policy = _dataNode.getEnum("policy", _policy, ValidationPolicy.class);

        clear();
        List<String> worldNames = _dataNode.getStringList("worlds", null);
        if (worldNames != null && !worldNames.isEmpty()) {
            addAll(worldNames);
        }
    }

}
