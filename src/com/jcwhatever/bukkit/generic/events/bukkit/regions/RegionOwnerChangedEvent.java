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


package com.jcwhatever.bukkit.generic.events.bukkit.regions;

import com.jcwhatever.bukkit.generic.regions.ReadOnlyRegion;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;
import javax.annotation.Nullable;


public class RegionOwnerChangedEvent extends Event {
    
    private static final HandlerList _handlers = new HandlerList();
    
    ReadOnlyRegion _region;
    UUID _oldId;
    UUID _newId;
    boolean _isCancelled;
    
    public RegionOwnerChangedEvent(ReadOnlyRegion region, @Nullable UUID oldId, @Nullable UUID newId) {
        _region = region;
        _oldId = oldId;
        _newId = newId;
    }
    
    public ReadOnlyRegion getRegion() {
        return _region;
    }
    
    public UUID getOldOwnerId() {
        return _oldId;
    }
    
    public UUID getNewOwnerId() {
        return _newId;
    }
        
    public boolean isCancelled() {
        return _isCancelled;
    }
    
    public void setIsCancelled(boolean isCancelled) {
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

