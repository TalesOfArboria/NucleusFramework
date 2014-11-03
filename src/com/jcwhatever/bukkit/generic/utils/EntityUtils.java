/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Entity helper utilities
 */
public class EntityUtils {

    private EntityUtils() {}


    /**
     * Find an entity in a world by its integer ID.
     *
     * @param world  The world to look in.
     * @param id     The entity ID.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static Entity getEntityById(World world, int id) {
        PreCon.notNull(world);

        List<Entity> entities = world.getEntities();

        for (Entity entity : entities) {
            if (entity.getEntityId() == id)
                return entity;
        }

        return null;
    }

    /**
     * Find an entity in a chunk by its integer ID.
     *
     * @param chunk  The chunk to look in.
     * @param id     The entity ID.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static Entity getEntityById(Chunk chunk, int id) {
        PreCon.notNull(chunk);

        Entity[] entities = chunk.getEntities();

        for (Entity entity : entities) {
            if (entity.getEntityId() == id)
                return entity;
        }

        return null;
    }

    /**
     * Find an entity in a world by its unique ID.
     *
     * @param world     The world to look in.
     * @param uniqueId  The entity unique ID.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static Entity getEntityByUUID(World world, UUID uniqueId) {
        PreCon.notNull(world);
        PreCon.notNull(uniqueId);

        List<Entity> entities = world.getEntities();

        for (Entity entity : entities) {
            if (entity.getUniqueId().equals(uniqueId))
                return entity;
        }

        return null;
    }


    /**
     * Find an entity in a world by its unique ID.
     *
     * @param chunk     The chunk to look in.
     * @param uniqueId  The entity unique ID.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static Entity getEntityByUUID(Chunk chunk, UUID uniqueId) {
        PreCon.notNull(chunk);
        PreCon.notNull(uniqueId);

        Entity[] entities = chunk.getEntities();

        for (Entity entity : entities) {
            if (entity.getUniqueId().equals(uniqueId))
                return entity;
        }

        return null;
    }
}
