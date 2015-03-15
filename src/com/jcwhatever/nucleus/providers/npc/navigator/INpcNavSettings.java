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

package com.jcwhatever.nucleus.providers.npc.navigator;

/**
 * Interface for an NPC's navigator settings.
 */
public interface INpcNavSettings {

    /**
     * Get the NPC pathing speed.
     */
    double getSpeed();

    /**
     * Set the pathing NPC speed.
     *
     * @param speed  The speed.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings setSpeed(double speed);

    /**
     * Get the target tolerance.
     *
     * <p>This is the radius around the NPC target destination that the
     * NPC must be within in order to consider the path completed.</p>
     */
    double getTolerance();

    /**
     * Set the target tolerance.
     *
     * <p>This is the radius around the NPC target destination that the
     * NPC must be within in order to consider the path completed.</p>
     *
     * @param tolerance  The radius tolerance.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings setTolerance(double tolerance);

    /**
     * Determine if the NPC avoids water.
     */
    boolean avoidsWater();

    /**
     * Make the NPC path to avoid water.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings avoidWater();

    /**
     * Make the NPC path to ignore/not-avoid water.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings ignoreWater();

    /**
     * Get the number of ticks to wait for an NPC to move before assuming it is stuck
     * and running the navigation timeout handler.
     */
    int getTimeout();

    /**
     * Set the number of ticks to wait for an NPC to move before assuming it is stuck
     * and running the navigation timeout handler.
     *
     * @param ticks  The number of ticks.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings setTimeout(int ticks);

    /**
     * Get the timeout handler used when the NPC is unable to move and
     * causes navigator timeout.
     *
     * @return  The timeout handler.
     */
    INpcNavTimeout getTimeoutHandler();

    /**
     * Set the timeout handler used when the NPC is unable to move and
     * causes navigator timeout.
     *
     * @param timeoutHandler  The timeout handler to use.
     *
     * @return  Self for chaining.
     */
    INpcNavSettings setTimeoutHandler(INpcNavTimeout timeoutHandler);
}
