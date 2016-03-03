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

import com.jcwhatever.nucleus.managed.astar.IAStarSettings;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Implementation of {@link IAStarSettings}.
 */
class AStarSettings implements IAStarSettings {

    private double _range = 18;
    private int _maxDropHeight = 4;
    private boolean _isSurfaceSearch = true;
    private long _maxIterations = 8500;

    @Override
    public long getMaxIterations() {
        return _maxIterations;
    }

    @Override
    public AStarSettings setMaxIterations(long max) {
        PreCon.positiveNumber(max);

        _maxIterations = max;
        return this;
    }

    @Override
    public double getRange() {
        return _range;
    }

    @Override
    public double getRangeSquared() {
        return _range * _range;
    }

    @Override
    public AStarSettings setRange(double range) {
        PreCon.positiveNumber(range);
        _range = range;
        return this;
    }

    @Override
    public int getMaxDropHeight() {
        return _maxDropHeight;
    }

    @Override
    public AStarSettings setMaxDropHeight(int height) {
        PreCon.positiveNumber(height);

        _maxDropHeight = height;
        return this;
    }

    @Override
    public boolean isSurfaceSearch() {
        return _isSurfaceSearch;
    }

    @Override
    public AStarSettings setSurfaceSearch(boolean isSurfaceSearch) {
        _isSurfaceSearch = isSurfaceSearch;
        return this;
    }
}
