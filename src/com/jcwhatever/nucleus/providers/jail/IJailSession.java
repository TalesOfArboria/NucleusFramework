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

package com.jcwhatever.nucleus.providers.jail;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IMeta;

import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Interface for a jail session.
 */
public interface IJailSession extends IMeta, IDisposable {

    /**
     * Get the sessions owning jail.
     */
    IJail getJail();

    /**
     * Get the Minecraft ID of the imprisoned player.
     */
    UUID getPlayerId();

    /**
     * Get the session expiration date.
     *
     * <p>This is when the player will be or was released from prison.</p>
     */
    Date getExpiration();

    /**
     * Determine if the player has been released from the session.
     */
    boolean isReleased ();

    /**
     * Determine if the session is expired.
     */
    boolean isExpired();

    /**
     * Release the player from the prison session.
     */
    boolean release();

    /**
     * Get the location the player should be released at.
     *
     * <p>Provides a higher specificity of location than the release location specified
     * in {@link IJail#getReleaseLocation}.</p>
     *
     * @return  The release location or null if a specific location is not set. If null, the location
     * specified in {@link IJail#getReleaseLocation} should be used.
     */
    @Nullable
    Location getReleaseLocation();

    /**
     * Set the release location.
     *
     * @param location  The location to send the player when released.
     */
    void setReleaseLocation(Location location);
}
