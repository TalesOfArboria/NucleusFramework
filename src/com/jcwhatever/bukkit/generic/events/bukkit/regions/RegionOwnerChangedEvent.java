package com.jcwhatever.bukkit.generic.events.bukkit.regions;

import com.jcwhatever.bukkit.generic.regions.ReadOnlyRegion;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;


public class RegionOwnerChangedEvent extends Event {
    
    private static final HandlerList _handlers = new HandlerList();
    
    ReadOnlyRegion _region;
    UUID _oldId;
    UUID _newId;
    boolean _isCancelled;
    
    RegionOwnerChangedEvent(ReadOnlyRegion region, UUID oldId, UUID newId) {
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
    
    public static RegionOwnerChangedEvent callEvent(ReadOnlyRegion region, UUID oldId, UUID newId) {
        RegionOwnerChangedEvent event = new RegionOwnerChangedEvent(region, oldId, newId);
        
        if (hasListeners()) {
            Bukkit.getPluginManager().callEvent(event);
        }
        
        return event;
    }

    public static boolean hasListeners() {
        return _handlers.getRegisteredListeners().length > 0;
    }
}

