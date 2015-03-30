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

package com.jcwhatever.nucleus.utils.entity;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ChunkInfo;
import com.jcwhatever.nucleus.utils.coords.ChunkUtils;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords2Di;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

/**
 * Tracks an entity and keeps the latest instance of the entity.
 *
 * <p>Entity objects are discarded when the chunk they are in is unloaded
 * making it unacceptable to hold long term references to the entity.</p>
 *
 * @see EntityUtils#trackEntity
 */
public final class TrackedEntity {

    private final UUID _uuid;
    private Entity _recent;
    private World _world;
    private boolean _isDisposed;

    private final NamedUpdateAgents _updateAgents = new NamedUpdateAgents();
    private final Location _entityLocation = new Location(null, 0, 0, 0);
    private final MutableCoords2Di _currentChunk = new MutableCoords2Di();

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
    public synchronized Entity getEntity() {
        return _recent;
    }

    /**
     * Get the world the entity is in.
     */
    public synchronized World getWorld() {
        return _world;
    }

    /**
     * Determine if the chunk the entity is in
     * is loaded.
     */
    public synchronized boolean isChunkLoaded() {

        Location location = _recent.getLocation(_entityLocation);
        Coords2Di chunk = ChunkUtils.getChunkCoords(location, _currentChunk);

        return _world.isChunkLoaded(chunk.getX(), chunk.getZ());
    }

    /**
     * Add an event handler to handle when the chunk the entity
     * is in is unloaded.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    public synchronized TrackedEntity onUnload(UpdateSubscriber<ChunkInfo> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.getAgent("onUnload").addSubscriber(subscriber);

        return this;
    }

    /**
     * Add an event handler to track when the entity instance is
     * updated.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    public synchronized TrackedEntity onUpdate(UpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.getAgent("onUpdate").addSubscriber(subscriber);

        return this;
    }

    /**
     * Add an event handler to handle when the entity is removed.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    public synchronized TrackedEntity onDeath(UpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        _updateAgents.getAgent("onDeath").addSubscriber(subscriber);

        return this;
    }

    /**
     * Determine if the tracked object is disposed.
     */
    public synchronized boolean isDisposed() {
        return _isDisposed;
    }

    /**
     * Dispose the tracked object. Not public because it should
     * only be disposed if the entity is removed.
     */
    synchronized void dispose() {
        if (_isDisposed)
            return;

        _isDisposed = true;

        _updateAgents.update("onDeath", getEntity());

        _world = null;
        _updateAgents.disposeAgents();

        EntityUtils._entityTracker.disposeEntity(this);
    }

    /**
     * Notify that the chunk the entity is in is unloading.
     */
    synchronized void notifyChunkUnload(ChunkInfo info) {

        _updateAgents.update("onUnload", info);
    }

    /**
     * Update the entity instance.
     *
     * @param entity  The new entity instance.
     */
    synchronized void updateEntity(Entity entity) {

        _recent = entity;
        _world = entity.getWorld();

        _updateAgents.update("onUpdate", entity);
    }
}
