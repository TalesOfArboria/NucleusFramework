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


package com.jcwhatever.nucleus.mixins.implemented;

import com.jcwhatever.nucleus.mixins.INamedLocation;
import com.jcwhatever.nucleus.mixins.INamedLocationDistance;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

/**
 * A basic {@code INamedLocationDistance} implementation.
 */
public class NamedLocationDistance implements INamedLocationDistance {
	private INamedLocation _namedLocation;
	private Location _target;
	private Double _distance;
	private Double _distanceSquared;

	/**
	 * Constructor.
	 *
	 * @param namedLocation  The named location source.
	 * @param target         The target.
	 */
	public NamedLocationDistance (INamedLocation namedLocation, Location target) {
		PreCon.notNull(namedLocation);
		PreCon.notNull(target);
		
		_namedLocation = namedLocation;
		_target = target;
	}
	
	@Override
    public INamedLocation getNamedLocation() {
		return _namedLocation;
	}
	
	@Override
    public Location getTarget() {
		return _target;
	}
	
	@Override
    public double getDistance() {
		if (_distance == null) {
			_distance = _namedLocation.getLocation().distance(_target);
		}
		return _distance;
	}
	
	@Override
    public double getDistanceSquared() {
		if (_distanceSquared == null) {
			_distanceSquared = _namedLocation.getLocation().distanceSquared(_target);
		}
		
		return _distanceSquared;
	}

	@Override
	public int compareTo(NamedLocationDistance distance) {
		return Double.compare(getDistanceSquared(), distance.getDistanceSquared());
	}

}
