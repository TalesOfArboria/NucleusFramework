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

import com.jcwhatever.nucleus.collections.observer.agent.AgentHashMap;
import com.jcwhatever.nucleus.collections.observer.agent.AgentMap;
import com.jcwhatever.nucleus.regions.data.ChunkInfo;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.UpdateAgent;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.bukkit.World;
import org.bukkit.entity.Entity;

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
    private boolean _isDisposed;

    private AgentMap<String, UpdateAgent<?>> _updateAgents = new AgentHashMap<String, UpdateAgent<?>>(3)
                    .set("onUpdate", new UpdateAgent<Entity>())
                    .set("onUnload", new UpdateAgent<ChunkInfo>())
                    .set("onDeath", new UpdateAgent<Entity>());

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
        return _recent.getLocation().getChunk().isLoaded();//ent_isChunkLoaded;
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

        UpdateAgent<?> subject = _updateAgents.get("onUnload");
        subject.addSubscriber(subscriber);

        return this;
    }

    /**
     * Add an event handler to track when the entity instance is
     * updated.
     *
     * @param subscriber  The handler.
     * @return
     */
    public synchronized TrackedEntity onUpdate(UpdateSubscriber<Entity> subscriber) {
        PreCon.notNull(subscriber);

        UpdateAgent<?> subject = _updateAgents.get("onUpdate");
        subject.addSubscriber(subscriber);

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

        UpdateAgent<?> subject = _updateAgents.get("onDeath");
        subject.addSubscriber(subscriber);

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

        @SuppressWarnings("unchecked")
        UpdateAgent<Entity> deathSubject = (UpdateAgent<Entity>)_updateAgents.get("onDeath");
        deathSubject.update(getEntity());

        _world = null;
        _updateAgents.dispose();

        EntityUtils._entityTracker.disposeEntity(this);
    }

    /**
     * Notify that the chunk the entity is in is unloading.
     */
    synchronized void notifyChunkUnload(ChunkInfo info) {

        @SuppressWarnings("unchecked")
        UpdateAgent<ChunkInfo> subject = (UpdateAgent<ChunkInfo>)_updateAgents.get("onUnload");
        subject.update(info);
    }

    /**
     * Update the entity instance.
     *
     * @param entity  The new entity instance.
     */
    synchronized void updateEntity(Entity entity) {

        _recent = entity;

        @SuppressWarnings("unchecked")
        UpdateAgent<Entity> subject = (UpdateAgent<Entity>)_updateAgents.get("onUpdate");
        subject.update(entity);
    }
}
