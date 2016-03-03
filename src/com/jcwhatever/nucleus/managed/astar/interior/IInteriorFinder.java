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

package com.jcwhatever.nucleus.managed.astar.interior;

import com.jcwhatever.nucleus.providers.regionselect.IRegionSelection;
import org.bukkit.Location;

/**
 * Interface for a finder that gets air block locations inside an enclosed space.
 */
public interface IInteriorFinder {

    /**
     * Search for air blocks within a region without moving
     * outside of structural boundaries.
     *
     * <p>The structure must be completely enclosed with no block open to the exterior.</p>
     *
     * <p>Does not search through doors even if they are open.</p>
     *
     * @param start       The location to start the search from.
     * @param boundaries  The region boundaries to prevent searching endlessly into the world.
     */
    IInteriorFinderResult search(Location start, IRegionSelection boundaries);
}
