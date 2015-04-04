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

package com.jcwhatever.nucleus.internal.providers.jail;

import com.jcwhatever.nucleus.providers.jail.IJailSession;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Nucleus implementation of {@link IJailSession}.
 */
class NucleusJailSession implements IJailSession {

    private final NucleusJail _jail;
    private final UUID _playerId;
    private final MetaStore _meta = new MetaStore();

    private Date _expires;
    private Location _releaseLocation;
    private boolean _isReleased;

    NucleusJailSession(NucleusJail jail, UUID playerId, Date expires) {
        PreCon.notNull(jail);
        PreCon.notNull(playerId);
        PreCon.notNull(expires);

        _jail = jail;
        _playerId = playerId;
        _expires = expires;
    }

    @Override
    public NucleusJail getJail() {
        return _jail;
    }

    @Override
    public UUID getPlayerId() {
        return _playerId;
    }

    @Override
    public Date getExpiration() {
        if (_jail.isDisposed())
            _expires = null;

        return _expires != null ? _expires : new Date();
    }

    @Override
    public boolean isReleased() {
        return _isReleased;
    }

    @Override
    public boolean isExpired() {
        return _expires == null || _expires.compareTo(new Date()) <= 0;
    }

    @Override
    public boolean release() {
        dispose();
        return _jail.getProvider().release(_playerId);
    }

    @Nullable
    @Override
    public Location getReleaseLocation() {
        return _releaseLocation;
    }

    @Override
    public void setReleaseLocation(Location location) {
        PreCon.notNull(location);

        _releaseLocation = location;
    }

    @Override
    public MetaStore getMeta() {
        return _meta;
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
