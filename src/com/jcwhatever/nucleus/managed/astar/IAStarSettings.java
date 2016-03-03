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

package com.jcwhatever.nucleus.managed.astar;

/**
 * AStar search settings for World coordinate based nodes.
 */
public interface IAStarSettings {

    /**
     * Get the max amount of iterations that can be performed.
     *
     * <p>A value of -1 indicates infinite iterations allowed.</p>
     */
    long getMaxIterations();

    /**
     * Set the max amount of iterations that can be performed.
     *
     * @param max  The max amount. -1 for infinite.
     *
     * @return  Self for chaining.
     */
    IAStarSettings setMaxIterations(long max);

    /**
     * Get the search range.
     */
    double getRange();

    /**
     * Get the search range squared.
     */
    double getRangeSquared();

    /**
     * Set the search range.
     *
     * @param range  The range.
     *
     * @return  Self for chaining.
     */
    IAStarSettings setRange(double range);

    /**
     * Get the max drop height.
     */
    int getMaxDropHeight();

    /**
     * Set the max drop height.
     *
     * @param height  The max height.
     *
     * @return  Self for chaining.
     */
    IAStarSettings setMaxDropHeight(int height);

    /**
     * Determine if start and end location are adjusted to
     * search on a walkable surface.
     */
    boolean isSurfaceSearch();

    /**
     * Set if start and end coords should be adjusted to path on
     * a walkable surface.
     *
     * @param isSurfaceSearch  True to search on walkable surface, otherwise false.
     *
     * @return  Self for chaining.
     */
    IAStarSettings setSurfaceSearch(boolean isSurfaceSearch);
}
