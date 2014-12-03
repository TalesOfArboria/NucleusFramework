/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.mixins.implemented;

import com.jcwhatever.bukkit.generic.mixins.INamedLocation;
import com.jcwhatever.bukkit.generic.mixins.INamedLocationDistance;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Location;

/**
 * A basic named location implementation.
 */
public class NamedLocation implements INamedLocation {

	private String _name;
	private String _searchName;
	private Location _location;
	
	public NamedLocation(String name, Location location) {
		PreCon.notNullOrEmpty(name);
		PreCon.notNull(location);
		
		_name = name;
		_location = location;
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
	@Override
	public Location getLocation() {
		return _location;
	}
	
	@Override
	public INamedLocationDistance getDistance(Location location) {
		return new NamedLocationDistance(this, location);
	}
	
}
