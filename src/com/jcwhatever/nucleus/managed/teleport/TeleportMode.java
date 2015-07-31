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

/**
 * Related entity teleport mode.
 */
public enum TeleportMode {
    /**
     * Only the target entity/player is teleported.
     *
     * <p>Leashed and mounted mobs/players are left behind.</p>
     */
    TARGET_ONLY (false, false),
    /**
     * The target and any mounted mobs/players are teleported.
     *
     * <p>Leashed mobs are left behind.</p>
     */
    MOUNTS (true, false),
    /**
     * The target and any leashed mobs are teleported.
     *
     * <p>Mounted mobs/players are left behind.</p>
     */
    LEASHED (false, true),
    /**
     * The target, mounted mobs/players, and leashed mobs are teleported.
     */
    MOUNTS_AND_LEASHED (true, true);

    private final boolean _mounts;
    private final boolean _leashed;

    TeleportMode(boolean mounts, boolean leashed) {
        _mounts = mounts;
        _leashed = leashed;
    }

    public boolean isMountsTeleport() {
        return _mounts;
    }

    public boolean isLeashTeleport() {
        return _leashed;
    }
}
