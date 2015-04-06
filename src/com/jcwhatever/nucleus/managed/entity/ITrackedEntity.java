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

package com.jcwhatever.nucleus.managed.entity;

import com.jcwhatever.nucleus.utils.coords.ChunkCoords;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

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
public interface ITrackedEntity {

    /**
     * The entities unique ID.
     */
    UUID getUniqueId();

    /**
     * Get the most recent entity instance.
     */
    Entity getEntity();

    /**
     * Get the world the entity is in.
     */
    World getWorld();

    /**
     * Determine if the chunk the entity is in is loaded.
     */
    boolean isChunkLoaded();

    /**
     * Add an event handler to handle when the chunk the entity
     * is in is unloaded.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    ITrackedEntity onUnload(UpdateSubscriber<ChunkCoords> subscriber);

    /**
     * Add an event handler to track when the entity instance is
     * updated.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    ITrackedEntity onUpdate(UpdateSubscriber<Entity> subscriber);

    /**
     * Add an event handler to handle when the entity is removed.
     *
     * @param subscriber  The handler.
     *
     * @return  Self for chaining.
     */
    ITrackedEntity onDeath(UpdateSubscriber<Entity> subscriber);

    /**
     * Determine if the tracked object is disposed.
     */
    boolean isDisposed();
}