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


package com.jcwhatever.nucleus.jail;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Represents a players session in a jail.
 */
public final class JailSession implements IDisposable {

    private final Jail _jail;
    private final UUID _playerId;
    private Date _expires;

    private boolean _isReleased;

    /**
     * Constructor.
     *
     * @param jail      The owning jail manager.
     * @param playerId  The id of the player.
     * @param expires   The time the jail session will expire. (Player release date)
     */
    public JailSession(Jail jail, UUID playerId, Date expires) {
        PreCon.notNull(jail);
        PreCon.notNull(playerId);
        PreCon.notNull(expires);

        _jail = jail;
        _playerId = playerId;
        _expires = expires;
    }

    /**
     * Get the owning jail.
     */
    public Jail getJail() {
        return _jail;
    }

    /**
     * Get the id of the player.
     */
    public UUID getPlayerId() {
        return _playerId;
    }

    /**
     * Get the session expiration date.
     */
    public Date getExpiration() {
        if (_jail.isDisposed())
           _expires = null;

        return _expires != null ? _expires : new Date();
    }

    /**
     * Determine if the player has been released
     * by the warden.
     */
    public boolean isReleased () {
        return _isReleased;
    }

    /**
     * Determine if the session is expired.
     */
    public boolean isExpired() {
        return _expires == null || _expires.compareTo(new Date()) <= 0;
    }

    /**
     * Release the player from jail.
     */
    public boolean release() {
        dispose();
        return Nucleus.getJailManager().release(_playerId);
    }

    /**
     * Get the location the player is released at.
     */
    @Nullable
    public Location getReleaseLocation() {
        return _jail.getReleaseLocation();
    }

    @Override
    public boolean isDisposed() {
        return _isReleased;
    }

    @Override
    public void dispose() {
        _isReleased = true;
        _expires = null;
    }
}
