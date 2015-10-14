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

package com.jcwhatever.nucleus.events.teleport;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.managed.teleport.ITeleportResult;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

/**
 * Called when a teleport via Nucleus is completed.
 *
 * @see com.jcwhatever.nucleus.managed.teleport.Teleporter
 * @see com.jcwhatever.nucleus.managed.teleport.ITeleportManager
 */
public class TeleportEvent extends EntityEvent {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), TeleportEvent.class);

    private final ITeleportResult _result;

    /**
     * Constructor.
     *
     * @param teleported      The entity teleported.
     * @param teleportResult  The teleport results.
     */
    public TeleportEvent(Entity teleported, ITeleportResult teleportResult) {
        super(teleported);
        PreCon.notNull(teleported);
        PreCon.notNull(teleportResult);

        _result = teleportResult;
    }

    /**
     * Get the teleport result;
     */
    public ITeleportResult getResult() {
        return _result;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

