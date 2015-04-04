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

package com.jcwhatever.nucleus.internal.regions;

import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;

import javax.annotation.Nullable;

public enum RegionEventReason {
    MOVE        (EnterRegionReason.MOVE,        LeaveRegionReason.MOVE),
    DEAD        (null,                          LeaveRegionReason.DEAD),
    TELEPORT    (EnterRegionReason.TELEPORT,    LeaveRegionReason.TELEPORT),
    RESPAWN     (EnterRegionReason.RESPAWN,     LeaveRegionReason.DEAD),
    JOIN_SERVER (EnterRegionReason.JOIN_SERVER, null),
    QUIT_SERVER (null,                          LeaveRegionReason.QUIT_SERVER);

    private final EnterRegionReason _enter;
    private final LeaveRegionReason _leave;

    RegionEventReason(EnterRegionReason enter, LeaveRegionReason leave) {
        _enter = enter;
        _leave = leave;
    }

    /**
     * Get the enter reason equivalent.
     */
    @Nullable
    public EnterRegionReason getEnterReason() {
        return _enter;
    }

    /**
     * Get the leave reason equivalent.
     */
    @Nullable
    public LeaveRegionReason getLeaveReason() {
        return _leave;
    }
}
