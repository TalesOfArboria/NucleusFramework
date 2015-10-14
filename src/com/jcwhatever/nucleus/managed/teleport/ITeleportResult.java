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

/**
 * Results of a non-scheduled teleport.
 */
public interface ITeleportResult extends ITeleportInfo {

    enum Status {
        PENDING,
        SUCCESS,
        CANCELLED
    }

    /**
     * Get the current status of the teleport operation.
     */
    Status getStatus();

    /**
     * Determine if teleport is a success.
     *
     * <p>Returns the most up to date status of success, regardless of the result
     * of {@link #getStatus()}. For instance, if {@link #getStatus()} returns pending, but
     * the entity has been successfully teleported, the result will be true.</p>
     */
    boolean isSuccess();

    /**
     * Determine if the teleport has been cancelled.
     */
    boolean isCancelled();

    /**
     * Get the location the entity was teleported from.
     */
    Location getFrom();

    /**
     * Copy the location the entity was teleported from to the specified output location.
     *
     * @param output  The output location.
     *
     * @return  The output location.
     */
    Location getFrom(Location output);

    /**
     * Get the location the entity was teleported to.
     */
    Location getTo();

    /**
     * Copy the location the entity was teleported to to the specified output location.
     *
     * @param output  The output location.
     *
     * @return  The output location.
     */
    Location getTo(Location output);

    /**
     * Get a future which can be used if the teleport requires scheduled tasks to complete.
     */
    IFuture getFuture();
}
