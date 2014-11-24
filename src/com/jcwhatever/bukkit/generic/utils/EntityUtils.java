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


package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

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

    /**
     * Get the closest entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param range         The search range.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double range) {
        return getClosestEntity(sourceEntity, range, range, range, null);
    }

    /**
     * Get the closest entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param range         The search range.
     * @param validator     The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double range,
                                          @Nullable EntryValidator<Entity> validator) {
        return getClosestEntity(sourceEntity, range, range, range, validator);
    }

    /**
     * Get the closest entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param rangeX        The search range on the X axis.
     * @param rangeY        The search range on the Y axis.
     * @param rangeZ        The search range on the Z axis.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double rangeX, double rangeY, double rangeZ) {
        return getClosestEntity(sourceEntity, rangeX, rangeY, rangeZ, null);
    }

    /**
     * Find the entity closest to the specified entity.
     *
     * @param sourceEntity  The entity source to search from.
     * @param rangeX        The search range on the X axis.
     * @param rangeY        The search range on the Y axis.
     * @param rangeZ        The search range on the Z axis.
     * @param validator     The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double rangeX, double rangeY, double rangeZ,
                                          @Nullable EntryValidator<Entity> validator) {
        PreCon.notNull(sourceEntity);
        PreCon.positiveNumber(rangeX);
        PreCon.positiveNumber(rangeY);
        PreCon.positiveNumber(rangeZ);

        List<Entity> entities = sourceEntity.getNearbyEntities(rangeX, rangeY, rangeZ);

        Entity closest = null;
        double closestDist = 0.0D;

        for (Entity entity : entities) {
            if (validator != null && !validator.isValid(entity))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = sourceEntity.getLocation().distanceSquared( entity.getLocation() )) < closestDist) {
                closest = entity;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param range         The search range.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity, double range) {
        return getClosestLivingEntity(sourceEntity, range, range, range, null);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param range         The search range.
     * @param validator     The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity, double range,
                                                      @Nullable EntryValidator<LivingEntity> validator) {
        return getClosestLivingEntity(sourceEntity, range, range, range, validator);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param rangeX        The search range on the X axis.
     * @param rangeY        The search range on the Y axis.
     * @param rangeZ        The search range on the Z axis.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity,
                                                      double rangeX, double rangeY, double rangeZ) {
        return getClosestLivingEntity(sourceEntity, rangeX, rangeY, rangeZ, null);
    }

    /**
     * Get the closest living entity to a player.
     *
     * @param sourceEntity  The entity source to search from.
     * @param rangeX        The search range on the X axis.
     * @param rangeY        The search range on the Y axis.
     * @param rangeZ        The search range on the Z axis.
     * @param validator     The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity,
                                                      double rangeX, double rangeY, double rangeZ,
                                                      @Nullable EntryValidator<LivingEntity> validator) {
        PreCon.notNull(sourceEntity);
        PreCon.positiveNumber(rangeX);
        PreCon.positiveNumber(rangeY);
        PreCon.positiveNumber(rangeZ);

        List<Entity> entities = sourceEntity.getNearbyEntities(rangeX, rangeY, rangeZ);

        LivingEntity closest = null;
        double closestDist = 0.0D;

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity))
                continue;

            LivingEntity livingEntity = (LivingEntity)entity;

            if (validator != null && !validator.isValid(livingEntity))
                continue;

            double dist = 0.0D;
            if (closest == null || (dist = sourceEntity.getLocation().distanceSquared( entity.getLocation() )) < closestDist) {
                closest = livingEntity;
                closestDist = dist;
            }
        }

        return closest;
    }
}
