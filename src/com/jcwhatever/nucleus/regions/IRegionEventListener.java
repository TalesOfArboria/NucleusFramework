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

package com.jcwhatever.nucleus.regions;

import com.jcwhatever.nucleus.regions.Region.EnterRegionReason;
import com.jcwhatever.nucleus.regions.Region.LeaveRegionReason;

import org.bukkit.entity.Player;

/**
 * Interface for an object provided by a region to notify it
 * of region related events.
 */
public interface IRegionEventListener {

    /**
     * Called when a player enters the region.
     *
     * @param player  The player that entered the region.
     * @param reason  The reason the player entered the region.
     */
    void onPlayerEnter (Player player, EnterRegionReason reason);

    /**
     * Called when a player leaves the region.
     *
     * @param player  The player that left the region.
     * @param reason  The reason the player left the region.
     */
    void onPlayerLeave (Player player, LeaveRegionReason reason);
}
