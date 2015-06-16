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

package com.jcwhatever.nucleus.managed.teleport;

import com.jcwhatever.nucleus.utils.observer.future.IFuture;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Scheduled teleport future.
 */
public interface IScheduledTeleport extends IFuture {

    /**
     * Get the player that will be teleported.
     */
    Player getPlayer();

    /**
     * Get the location the player will be teleported to.
     */
    Location getLocation();

    /**
     * Copy the values of the location the player will be teleported to
     * into the specified output location.
     *
     * @param output  The output location.
     *
     * @return  The output location.
     */
    Location getLocation(Location output);

    /**
     * Cancel the teleport.
     *
     * <p>If the teleport is already cancelled or executed, nothing happens.</p>
     */
    void cancel();
}
