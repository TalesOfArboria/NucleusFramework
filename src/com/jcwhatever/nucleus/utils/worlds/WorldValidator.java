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


package com.jcwhatever.nucleus.utils.worlds;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages a collection of worlds used to blacklist or
 * whitelist worlds for various purposes.
 */
public class WorldValidator {

    private final IDataNode _worldNode;
    private WorldValidationPolicy _validationPolicy = WorldValidationPolicy.BLACKLIST;
    private Set<String> _worlds = new HashSet<String>(10);

    /**
     * Constructor.
     *
     * @param dataNode  The managers data node.
     */
    public WorldValidator(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _worldNode = dataNode;

        loadSettings();
    }

    /**
     * Determine if a world is considered valid.
     *
     * @param world  The world to check.
     */
    public boolean isValidWorld(World world) {
        PreCon.notNull(world);

        boolean result = false;

        if (_validationPolicy == WorldValidationPolicy.BLACKLIST)
            result = !_worlds.contains(world.getName());

        else if (_validationPolicy == WorldValidationPolicy.WHITELIST)
            result = _worlds.contains(world.getName());

        return result;
    }

    /**
     * Get the names of the worlds in the collection.
     */
    public List<String> getWorlds() {
        return new ArrayList<String>(_worlds);
    }

    /**
     * Get the mode used to determine how worlds in the collection
     * are used for validation.
     */
    public WorldValidationPolicy getPolicy() {
        return _validationPolicy;
    }

    /**
     * Set the mode used to determine how worlds in the collection
     * are used for validation.
     *
     * @param validationPolicy  The world validation mode.
     */
    public void setMode(WorldValidationPolicy validationPolicy) {
        PreCon.notNull(validationPolicy);

        _validationPolicy = validationPolicy;
        _worldNode.set("mode", validationPolicy);
        _worldNode.saveAsync(null);
    }

    /**
     * Add a world to the collection.
     *
     * @param world  The world to add.
     */
    public boolean addWorld(World world) {
        PreCon.notNull(world);

        if (_worlds.add(world.getName())) {
            _worldNode.set("worlds", new ArrayList<String>(_worlds));
            _worldNode.saveAsync(null);

            return true;
        }
        return false;
    }

    /**
     * Remove a world from the collection.
     *
     * @param world  The world to removed.
     */
    public boolean removeWorld(World world) {
        PreCon.notNull(world);

        return removeWorld(world.getName());
    }

    /**
     * Remove a world from the collection.
     *
     * @param worldName  The name of the world.
     */
    public boolean removeWorld(String worldName) {
        PreCon.notNullOrEmpty(worldName);

        if (!_worlds.contains(worldName))
            return false;

        if (_worlds.remove(worldName)) {
            _worldNode.set("worlds", new ArrayList<String>(_worlds));
            _worldNode.saveAsync(null);

            return true;
        }

        return false;
    }

    // initial load of settings
    private void loadSettings() {
        _validationPolicy = _worldNode.getEnum("policy", _validationPolicy, WorldValidationPolicy.class);

        _worlds.clear();
        List<String> worldNames = _worldNode.getStringList("worlds", null);
        if (worldNames != null && !worldNames.isEmpty()) {
            _worlds.addAll(worldNames);
        }
    }

}
