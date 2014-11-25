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

package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.regions.Region.EnterRegionReason;
import com.jcwhatever.bukkit.generic.regions.Region.LeaveRegionReason;

import org.bukkit.entity.Player;

/**
 * Represents a callback that handles region events.
 */
public interface IRegionEventHandler {

    /**
     * Determine if the handler can handle player enter
     * event for the specified player.
     *
     * @param player  The player who is entering the region.
     */
    boolean canDoPlayerEnter(Player player, EnterRegionReason reason);

    /**
     * Determine if the handler can handle player leave
     * event for the specified player.
     *
     * @param player  The player who is leaving the region.
     */
    boolean canDoPlayerLeave(Player player, LeaveRegionReason reason);

    /**
     * Called when a player enters the region and
     * the {@code canDoPlayerEnter} method returned true.
     *
     * @param player  The player entering the region.
     */
    void onPlayerEnter(Player player, EnterRegionReason reason);

    /**
     * Called when a player leaves the region and
     * the {@code canDoPlayerLeave} method returned true.
     *
     * @param player  The player leaving the region.
     */
    void onPlayerLeave(Player player, LeaveRegionReason reason);
}
