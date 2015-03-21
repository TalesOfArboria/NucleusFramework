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


package com.jcwhatever.nucleus.utils.coords;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * A basic named location implementation.
 */
public class NamedLocation extends Location implements INamedInsensitive {

	private String _name;
	private String _searchName;

    /**
     * Constructor.
     *
     * @param name      The name of the location.
     * @param location  The location to copy values from.
     */
	public NamedLocation(String name, Location location) {
        this(name, location.getWorld(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
	}

    /**
     * Constructor.
     *
     * @param name   The name of the location.
     * @param world  The world.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     */
    public NamedLocation(String name, World world, double x, double y, double z) {
        this(name, world, x, y, z, 0.0f, 0.0f);
    }

    /**
     * Constructor.
     *
     * @param name   The name of the location.
     * @param world  The world.
     * @param x      The x coordinates.
     * @param y      The y coordinates.
     * @param z      The z coordinates.
     * @param yaw    The yaw angle.
     * @param pitch  The pitch angle.
     */
    public NamedLocation(String name, World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);

        PreCon.notNullOrEmpty(name);

        _name = name;
    }

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getSearchName() {
		if (_searchName == null)
			_searchName = _name.toLowerCase();
		
		return _searchName;
	}
}
