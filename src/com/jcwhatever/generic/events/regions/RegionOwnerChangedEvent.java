/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.generic.events.regions;

import com.jcwhatever.generic.mixins.ICancellable;
import com.jcwhatever.generic.regions.IRegion;
import com.jcwhatever.generic.regions.ReadOnlyRegion;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Called when a regions owner is changed.
 */
public class RegionOwnerChangedEvent extends Event implements Cancellable, ICancellable {
    
    private static final HandlerList _handlers = new HandlerList();
    
    private final ReadOnlyRegion _region;
    private final UUID _oldId;
    private UUID _newId;
    private boolean _isCancelled;
    
    public RegionOwnerChangedEvent(ReadOnlyRegion region,
                                   @Nullable UUID oldId, @Nullable UUID newId) {
        PreCon.notNull(region);

        _region = region;
        _oldId = oldId;
        _newId = newId;
    }

    /**
     * Get the region whose owner is changed.
     */
    public IRegion getRegion() {
        return _region;
    }

    /**
     * Get the Id of the previous region owner.
     *
     * @return  Null if there was no previous owner.
     */
    @Nullable
    public UUID getOldOwnerId() {
        return _oldId;
    }

    /**
     * Get the ID of the new region owner.
     *
     * @return  Null if the region owner is being cleared.
     */
    @Nullable
    public UUID getNewOwnerId() {
        return _newId;
    }
        
    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }
     
    public static HandlerList getHandlerList() {
        return _handlers;
    }
}

