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

package com.jcwhatever.bukkit.generic.utils.entity;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Tracks an entity and keeps the latest instance of the entity.
 *
 * <p>Entity objects are discarded when the chunk they are in is unloaded
 * making it unacceptable to hold long term references to the entity.</p>
 */
public final class TrackedEntity {

    private final UUID _uuid;
    private Entity _recent;
    private World _world;
    //private boolean _isChunkLoaded;
    private boolean _isDisposed;

    private List<ITrackedEntityHandler> _handlers = new ArrayList<>(5);

    /**
     * Constructor.
     *
     * @param entity  The entity to track.
     */
    TrackedEntity(Entity entity) {
        PreCon.notNull(entity);

        _uuid = entity.getUniqueId();
        _world = entity.getWorld();
        _recent = entity;

        //_isChunkLoaded = entity.getLocation().getChunk().isLoaded();
    }

    /**
     * The entities unique id.
     */
    public UUID getUniqueId() {
        return _uuid;
    }

    /**
     * Get the most recent entity instance.
     */
    public Entity getEntity() {
        return _recent;
    }

    /**
     * Get the world the entity is in.
     */
    public World getWorld() {
        return _world;
    }

    /**
     * Determine if the chunk the entity is in
     * is loaded.
     */
    public boolean isChunkLoaded() {
        return _recent.getLocation().getChunk().isLoaded();//ent_isChunkLoaded;
    }

    /**
     * Add a handler that is called whenever the
     * {@code Entity} instance is changed.
     *
     * @param handler  The handler to add.
     */
    public void addHandler(ITrackedEntityHandler handler) {
        PreCon.notNull(handler);

        _handlers.add(handler);
    }

    /**
     * Remove a handler that is called whenever the
     * {@code Entity} instance is changed.
     *
     * @param handler  The handler to remove.
     */
    public void removeHandler(ITrackedEntityHandler handler) {
        PreCon.notNull(handler);

        _handlers.remove(handler);
    }

    /**
     * Determine if the tracked object is disposed.
     */
    public boolean isDisposed() {
        return _isDisposed;
    }


    void dispose() {
        if (_isDisposed)
            return;

        _isDisposed = true;

        for (ITrackedEntityHandler handler : _handlers) {
            handler.onChanged(this);
        }

        _world = null;
        _handlers = null;

        EntityUtils._entityTracker.untrackEntity(this);
    }

    void setWorld(World world) {
        _world = world;
    }

    void onChunkLoad() {
        //_isChunkLoaded = true;

        for (ITrackedEntityHandler handler : _handlers) {
            handler.onChanged(this);
        }
    }

    void onChunkUnload() {
        //_isChunkLoaded = false;

        for (ITrackedEntityHandler handler : _handlers) {
            handler.onChanged(this);
        }
    }

    void setEntity(Entity entity) {
        _recent = entity;

        for (ITrackedEntityHandler handler : _handlers) {
            handler.onChanged(this);
        }
    }

    /**
     * A transient handler to track when the {@code Entity} instance
     * is changed.
     */
    public interface ITrackedEntityHandler {
        void onChanged(TrackedEntity trackedEntity);
    }
}
