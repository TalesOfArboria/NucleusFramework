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

package com.jcwhatever.nucleus.managed.resourcepacks;

import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import javax.annotation.Nullable;

/**
 * Resource pack client status.
 */
public enum ResourcePackStatus {
    /**
     * No resource pack has been sent to the client.
     */
    NO_RESOURCE (true, false),
    /**
     * Resource pack status is still pending.
     */
    PENDING     (false, false),
    /**
     * The resource pack was declined by the client.
     */
    DECLINED    (true, false),
    /**
     * The resource pack was accepted by the client.
     */
    ACCEPTED    (false, false),
    /**
     * Resource pack was successfully loaded by the client.
     */
    SUCCESS     (true, true),
    /**
     * The resource pack failed to load at the client.
      */
    FAILED      (true, false);

    private final boolean _isFinal;
    private final boolean _hasPack;

    ResourcePackStatus(boolean isFinal, boolean hasPack) {
        _isFinal = isFinal;
        _hasPack = hasPack;
    }

    /**
     * Determine if a status is a final status, as in no further changes in status are expected.
     */
    public boolean isFinal() {
        return _isFinal;
    }

    /**
     * Determine if the status indicates the player has the resource pack ready and loaded.
     */
    public boolean hasResourcePack() {
        return _hasPack;
    }

    /**
     * Convert to Bukkit event status.
     *
     * @return  The Bukkit event status or null if there is no equivalent. (PENDING, NO_RESOURCE)
     */
    @Nullable
    public PlayerResourcePackStatusEvent.Status toBukkit() {
        switch (this) {
            case SUCCESS:
                return PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED;
            case DECLINED:
                return PlayerResourcePackStatusEvent.Status.DECLINED;
            case FAILED:
                return PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD;
            case ACCEPTED:
                return PlayerResourcePackStatusEvent.Status.ACCEPTED;
            default:
                return null;
        }
    }

    /**
     * Get the {@link ResourcePackStatus} from Bukkit event status.
     *
     * @param status  The Bukkit event status.
     */
    public static ResourcePackStatus fromBukkit(PlayerResourcePackStatusEvent.Status status) {
        PreCon.notNull(status);

        switch (status) {
            case SUCCESSFULLY_LOADED:
                return ResourcePackStatus.SUCCESS;
            case DECLINED:
                return ResourcePackStatus.DECLINED;
            case FAILED_DOWNLOAD:
                return ResourcePackStatus.FAILED;
            case ACCEPTED:
                return ResourcePackStatus.ACCEPTED;
            default:
                throw new AssertionError("Unrecognized Status constant.");
        }
    }
}
