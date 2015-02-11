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

package com.jcwhatever.nucleus.providers.npc.navigator.flock;

import java.util.Collection;

/**
 * Interface that represents a flocking strategy.
 */
public interface INpcFlocker {

    /**
     * Get the flock behavior.
     */
    Collection<INpcFlockBehavior> getBehaviors();

    /**
     * Set the flock behavior.
     *
     * @param behavior  The behavior.
     *
     * @return  Self for chaining.
     */
    INpcFlocker addBehavior(INpcFlockBehavior behavior);

    /**
     * Remove a flock behavior.
     *
     * @param behavior  The behavior.
     *
     * @return  True if found and removed.
     */
    boolean removeBehavior(INpcFlockBehavior behavior);

    /**
     * Clear flock behaviors.
     *
     * @return  Self for chaining.
     */
    INpcFlocker clearBehaviors();

    /**
     * Get the flock finder.
     */
    INpcFlockFinder getFlockFinder();

    /**
     * Set the flock finder.
     *
     * @param finder  The flock finder.
     */
    INpcFlocker setFlockFinder(INpcFlockFinder finder);
}
