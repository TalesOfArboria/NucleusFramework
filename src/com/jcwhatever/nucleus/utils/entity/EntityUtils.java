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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.entity.ITrackedEntity;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@link org.bukkit.entity.Entity} helper utilities
 */
public final class EntityUtils {

    private EntityUtils() {}

    private static final Location SOURCE_ENTITY_LOCATION = new Location(null, 0, 0, 0);
    private static final Location TARGET_ENTITY_LOCATION = new Location(null, 0, 0, 0);
    private static final Location NEARBY_ENTITY_LOCATION = new Location(null, 0, 0, 0);

    /**
     * Find an {@link org.bukkit.entity.Entity} in a {@link org.bukkit.World}
     * by its integer ID.
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
     * Find an {@link org.bukkit.entity.Entity} in a {@link org.bukkit.Chunk}
     * by its integer ID.
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
     * Find an {@link org.bukkit.entity.Entity} in a {@link org.bukkit.World}
     * by its unique ID.
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
     * Find an {@link org.bukkit.entity.Entity} in a {@link org.bukkit.Chunk}
     * by its unique ID.
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
     * Guarantees the removal of an {@link org.bukkit.entity.Entity} even
     * if the entity instance is outdated or the chunk it's in
     * is not loaded.
     *
     * @param entity  The entity to remove.
     *
     * @return  True if the entity was found and removed.
     */
    public static boolean removeEntity(Entity entity) {
        PreCon.notNull(entity);

        return removeEntity(entity.getLocation().getChunk(), entity.getUniqueId());
    }

    /**
     * Remove an {@link org.bukkit.entity.Entity} from a
     * {@link org.bukkit.Chunk}, even if the chunk is not loaded.
     *
     * @param chunk     The chunk the entity is in.
     * @param entityId  The entities unique ID.
     *
     * @return  True if the entity was found and removed.
     */
    public static boolean removeEntity(Chunk chunk, UUID entityId) {
        PreCon.notNull(chunk);
        PreCon.notNull(entityId);

        Entity entity = getEntityByUUID(chunk, entityId);
        if (entity == null)
            return false;

        entity.remove();

        return true;
    }

    /**
     * Get the damaging {@link org.bukkit.entity.Entity}. Returns the entityDamager
     * unless it's a {@link org.bukkit.entity.Projectile}, in which case the projectile
     * source is returned. If the projectile does not have a source then the projectile
     * is returned.
     *
     * @param entityDamager  The entity that has caused direct damage.
     */
    public static Entity getDamager(Entity entityDamager) {

        if (entityDamager instanceof Projectile) {
            ProjectileSource source = ((Projectile) entityDamager).getShooter();
            if (source instanceof Entity) {
                return (Entity)source;
            }
        }

        return entityDamager;
    }

    /**
     * Get the closest {@link org.bukkit.entity.Entity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radius        The radius entities must be within to be returned.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double radius) {
        return getClosestEntity(sourceEntity, radius, radius, radius, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.Entity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radius        The radius entities must be within to be returned.
     * @param validator     The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity, double radius,
                                          @Nullable IValidator<Entity> validator) {
        return getClosestEntity(sourceEntity, radius, radius, radius, validator);
    }

    /**
     * Get the closest {@link org.bukkit.entity.Entity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     */
    @Nullable
    public static Entity getClosestEntity(Entity sourceEntity,
                                          double radiusX, double radiusY, double radiusZ) {
        return getClosestEntity(sourceEntity, radiusX, radiusY, radiusZ, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.Entity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     * @param validator     The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(final Entity sourceEntity,
                                          double radiusX, double radiusY, double radiusZ,
                                          @Nullable final IValidator<Entity> validator) {
        PreCon.notNull(sourceEntity);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        Location sourceLocation = getEntityLocation(sourceEntity, SOURCE_ENTITY_LOCATION);
        List<Entity> entities = sourceEntity.getNearbyEntities(radiusX, radiusY, radiusZ);

        return getClosestEntity(sourceLocation, entities, new IValidator<Entity>() {
            @Override
            public boolean isValid(Entity entity) {
                return !entity.equals(sourceEntity) &&
                        (validator == null || validator.isValid(entity));
            }
        });
    }

    /**
     * Get the closest {@link org.bukkit.entity.Entity} to a {@link org.bukkit.Location}.
     *
     * @param sourceLocation  The location to check.
     * @param entities        The list of entities to check.
     * @param validator       The validator used to determine if an entity is a candidate.
     */
    @Nullable
    public static Entity getClosestEntity(Location sourceLocation,
                                          List<Entity> entities,
                                          @Nullable IValidator<Entity> validator) {
        PreCon.notNull(sourceLocation);
        PreCon.notNull(entities);

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : entities) {

            if (!entity.isValid())
                continue;

            if (!entity.getWorld().equals(sourceLocation.getWorld()))
                continue;

            Location targetLocation = getEntityLocation(entity, TARGET_ENTITY_LOCATION);

            double dist = sourceLocation.distanceSquared(targetLocation);
            if (dist < closestDist &&
                    (validator == null || validator.isValid(entity))) {

                closest = entity;
                closestDist = dist;
            }
        }

        return closest;
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radius        The radius entities must be within to be returned.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity, double radius) {
        return getClosestLivingEntity(sourceEntity, radius, radius, radius, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radius        The radius entities must be within to be returned.
     * @param validator     The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity, double radius,
                                                      @Nullable IValidator<LivingEntity> validator) {
        return getClosestLivingEntity(sourceEntity, radius, radius, radius, validator);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Entity sourceEntity,
                                                      double radiusX, double radiusY, double radiusZ) {
        return getClosestLivingEntity(sourceEntity, radiusX, radiusY, radiusZ, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceEntity  The entity source to search from.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     * @param validator     The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(final Entity sourceEntity,
                                                      double radiusX, double radiusY, double radiusZ,
                                                      @Nullable final IValidator<LivingEntity> validator) {
        PreCon.notNull(sourceEntity);

        Location sourceLocation = getEntityLocation(sourceEntity, SOURCE_ENTITY_LOCATION);
        return getClosestLivingEntity(sourceLocation, radiusX, radiusY, radiusZ,
                new IValidator<LivingEntity>() {
                    @Override
                    public boolean isValid(LivingEntity entity) {
                        return !entity.equals(sourceEntity) &&
                                (validator == null || validator.isValid(entity));
                    }
                });
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.Location}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceLocation  The location to search from.
     * @param radius          The radius entities must be within to be returned.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Location sourceLocation, double radius) {
        return getClosestLivingEntity(sourceLocation, radius, radius, radius, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.Location}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param sourceLocation  The location to search from.
     * @param radius          The radius entities must be within to be returned.
     * @param validator       The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Location sourceLocation, double radius,
                                                      @Nullable IValidator<LivingEntity> validator) {
        return getClosestLivingEntity(sourceLocation, radius, radius, radius, validator);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.Location}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceLocation  The location to search from.
     * @param radiusX         The x-axis radius entities must be within to be returned.
     * @param radiusY         The y-axis radius entities must be within to be returned.
     * @param radiusZ         The z-axis radius entities must be within to be returned.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Location sourceLocation,
                                                      double radiusX, double radiusY, double radiusZ) {
        return getClosestLivingEntity(sourceLocation, radiusX, radiusY, radiusZ, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to the specified source
     * {@link org.bukkit.Location}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param sourceLocation  The location to search from.
     * @param radiusX         The x-axis radius entities must be within to be returned.
     * @param radiusY         The y-axis radius entities must be within to be returned.
     * @param radiusZ         The z-axis radius entities must be within to be returned.
     * @param validator       The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Location sourceLocation,
                                                      double radiusX, double radiusY, double radiusZ,
                                                      @Nullable final IValidator<LivingEntity> validator) {
        PreCon.notNull(sourceLocation);

        List<Entity> nearbyEntities = getNearbyEntities(sourceLocation, radiusX, radiusY, radiusZ,
                new IValidator<Entity>() {

                    @Override
                    public boolean isValid(Entity entity) {
                        return entity instanceof LivingEntity &&
                                (validator == null || validator.isValid((LivingEntity)entity));
                    }
                });

        return (LivingEntity)getClosestEntity(sourceLocation, nearbyEntities, null);
    }

    /**
     * Get the closest {@link org.bukkit.entity.LivingEntity} to a {@link org.bukkit.Location}.
     *
     * @param sourceLocation  The location to check.
     * @param entities        The list of entities to check.
     * @param validator       The validator used to determine if a living entity is a candidate.
     */
    @Nullable
    public static LivingEntity getClosestLivingEntity(Location sourceLocation, List<Entity> entities,
                                                      @Nullable IValidator<LivingEntity> validator) {
        PreCon.notNull(sourceLocation);
        PreCon.notNull(entities);

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : entities) {

            if (!(entity instanceof LivingEntity))
                continue;

            if (!entity.getWorld().equals(sourceLocation.getWorld()))
                continue;

            Location targetLocation = getEntityLocation(entity, TARGET_ENTITY_LOCATION);

            double dist = sourceLocation.distanceSquared(targetLocation);
            if (dist < closestDist &&
                    (validator == null || validator.isValid((LivingEntity)entity))) {

                closest = entity;
                closestDist = dist;
            }
        }

        return (LivingEntity)closest;
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param location  The location to check from.
     * @param radius    The radius entities must be within to be returned.
     */
    public static List<Entity> getNearbyEntities(Location location, double radius) {
        return getNearbyEntities(location, radius, radius, radius, null);
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>The radius is spherical.</p>
     *
     * @param location   The location to check from.
     * @param radius     The radius entities must be within to be returned.
     * @param validator  Optional validator used to validate each entity within the radius.
     */
    public static List<Entity> getNearbyEntities(Location location, double radius,
                                                 @Nullable IValidator<Entity> validator) {
        return getNearbyEntities(location, radius, radius, radius, validator);
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param location  The location to check from.
     * @param radiusX   The x-axis radius entities must be within to be returned.
     * @param radiusY   The y-axis radius entities must be within to be returned.
     * @param radiusZ   The z-axis radius entities must be within to be returned.
     */
    public static List<Entity> getNearbyEntities(Location location,
                                                 double radiusX, double radiusY, double radiusZ) {
        return getNearbyEntities(location, radiusX, radiusY, radiusZ, null);
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * @param location   The location to check from.
     * @param radiusX    The x-axis radius entities must be within to be returned.
     * @param radiusY    The y-axis radius entities must be within to be returned.
     * @param radiusZ    The z-axis radius entities must be within to be returned.
     * @param validator  Optional validator used to validate each entity within the radius.
     */
    public static List<Entity> getNearbyEntities(Location location,
                                                 double radiusX, double radiusY, double radiusZ,
                                                 @Nullable IValidator<Entity> validator) {
        PreCon.notNull(location);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        List<Entity> results = new ArrayList<>(15);

        World world = location.getWorld();
        if (world == null)
            return results;

        int xStart = getStartChunk(location.getX(), radiusX);
        int xEnd = getEndChunk(location.getX(), radiusX);
        int zStart = getStartChunk(location.getZ(), radiusZ);
        int zEnd = getEndChunk(location.getZ(), radiusZ);

        for (int x = xStart; x <= xEnd; x++) {
            for (int z = zStart; z <= zEnd; z++) {

                Chunk chunk = world.getChunkAt(x, z);
                if (!chunk.isLoaded())
                    chunk.load();

                Entity[] entities = chunk.getEntities();

                for (Entity entity : entities) {

                    if (!entity.isValid())
                        continue;

                    if (!entity.getWorld().equals(world))
                        continue;

                    Location entityLocation = getEntityLocation(entity, NEARBY_ENTITY_LOCATION);

                    if (!LocationUtils.isInRange(location, entityLocation, radiusX, radiusY, radiusZ))
                        continue;

                    if (validator != null && !validator.isValid(entity))
                        continue;

                    results.add(entity);
                }
            }
        }

        return results;
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>The radius is spherical.</p>
     *
     * <p>The source entity is not included in the result.</p>
     *
     * @param sourceEntity  The source entity to check from.
     * @param radius        The radius entities must be within to be returned.
     * @param validator     Optional validator used to validate each entity within the radius.
     */
    public static List<Entity> getNearbyEntities(Entity sourceEntity, double radius,
                                                 @Nullable IValidator<Entity> validator) {
        return getNearbyEntities(sourceEntity, radius, radius, radius, validator);
    }

    /**
     * Get entities within a specified radius of a {@link org.bukkit.Location}.
     *
     * <p>If all radius values are equal, the radius is spherical. Otherwise the radius is cuboid.</p>
     *
     * <p>The source entity is not included in the result.</p>
     *
     * @param sourceEntity  The source entity to check from.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     * @param validator     Optional validator used to validate each entity within the radius.
     */
    public static List<Entity> getNearbyEntities(Entity sourceEntity,
                                                 double radiusX, double radiusY, double radiusZ,
                                                 @Nullable IValidator<Entity> validator) {
        PreCon.notNull(sourceEntity);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        List<Entity> results = new ArrayList<>(15);

        World world = sourceEntity.getWorld();
        if (world == null)
            return results;

        Location location = sourceEntity.getLocation(SOURCE_ENTITY_LOCATION);

        int xStart = getStartChunk(location.getX(), radiusX);
        int xEnd = getEndChunk(location.getX(), radiusX);
        int zStart = getStartChunk(location.getZ(), radiusZ);
        int zEnd = getEndChunk(location.getZ(), radiusZ);

        for (int x = xStart; x <= xEnd; x++) {
            for (int z = zStart; z <= zEnd; z++) {

                Chunk chunk = world.getChunkAt(x, z);
                if (!chunk.isLoaded())
                    chunk.load();

                Entity[] entities = chunk.getEntities();

                for (Entity entity : entities) {

                    if (!entity.isValid())
                        continue;

                    if (sourceEntity.getUniqueId().equals(entity.getUniqueId()))
                        continue;

                    if (!entity.getWorld().equals(world))
                        continue;

                    Location entityLocation = getEntityLocation(entity, NEARBY_ENTITY_LOCATION);

                    if (!LocationUtils.isInRange(location, entityLocation, radiusX, radiusY, radiusZ))
                        continue;

                    if (validator != null && !validator.isValid(entity))
                        continue;

                    results.add(entity);
                }
            }
        }

        return results;
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a source entity.
     *
     * @param sourceEntity  The source entity to check from.
     * @param type          The {@link EntityType} to search for.
     * @param radius        The radius entities must be within to be returned.
     */
    public static boolean hasNearbyEntityType(Entity sourceEntity, EntityType type, double radius) {
        return hasNearbyEntityType(sourceEntity, type, radius, radius, radius, null);
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a source entity.
     *
     * @param sourceEntity   The source entity to check from.
     * @param type           The {@link EntityType} to search for.
     * @param radius         The radius entities must be within to be returned.
     * @param validator      Optional validator used to validate each entity of the specified type
     *                       found within the radius.
     */
    public static boolean hasNearbyEntityType(Entity sourceEntity, EntityType type, double radius,
                                              @Nullable IValidator<Entity> validator) {
        return hasNearbyEntityType(sourceEntity, type, radius, radius, radius, validator);
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a source entity.
     *
     * @param sourceEntity  The source entity to check from.
     * @param type          The {@link EntityType} to search for.
     * @param radiusX       The x-axis radius entities must be within to be returned.
     * @param radiusY       The y-axis radius entities must be within to be returned.
     * @param radiusZ       The z-axis radius entities must be within to be returned.
     * @param validator     Optional validator used to validate each entity of the specified type
     *                      found within the radius.
     */
    public static boolean hasNearbyEntityType(Entity sourceEntity, EntityType type,
                                              double radiusX, double radiusY, double radiusZ,
                                              @Nullable IValidator<Entity> validator) {
        PreCon.notNull(sourceEntity);
        PreCon.notNull(type);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        List<Entity> entities = sourceEntity.getNearbyEntities(radiusX, radiusY, radiusZ);

        for (Entity entity : entities) {

            if (!entity.isValid())
                continue;

            if (entity.getType() != type)
                continue;

            if (validator != null && !validator.isValid(entity))
                continue;

            return true;
        }

        return false;
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a location.
     *
     * @param location   The location to check from.
     * @param type       The {@link EntityType} to search for.
     * @param radius     The radius entities must be within to be returned.
     */
    public static boolean hasNearbyEntityType(Location location, EntityType type, double radius) {
        return hasNearbyEntityType(location, type, radius, radius, radius, null);
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a location.
     *
     * @param location   The location to check from.
     * @param type       The {@link EntityType} to search for.
     * @param radius     The radius entities must be within to be returned.
     * @param validator  Optional validator used to validate each entity of the specified type
     *                   found within the radius.
     */
    public static boolean hasNearbyEntityType(Location location, EntityType type, double radius,
                                              @Nullable IValidator<Entity> validator) {
        return hasNearbyEntityType(location, type, radius, radius, radius, validator);
    }

    /**
     * Determine if there is an entity of a specified type within the specified radius
     * of a location.
     *
     * @param location   The location to check from.
     * @param type       The {@link EntityType} to search for.
     * @param radiusX    The x-axis radius entities must be within to be returned.
     * @param radiusY    The y-axis radius entities must be within to be returned.
     * @param radiusZ    The z-axis radius entities must be within to be returned.
     * @param validator  Optional validator used to validate each entity of the specified type
     *                   found within the radius.
     */
    public static boolean hasNearbyEntityType(Location location, EntityType type,
                                              double radiusX, double radiusY, double radiusZ,
                                              @Nullable IValidator<Entity> validator) {
        PreCon.notNull(location);
        PreCon.notNull(type);
        PreCon.positiveNumber(radiusX);
        PreCon.positiveNumber(radiusY);
        PreCon.positiveNumber(radiusZ);

        World world = location.getWorld();
        if (world == null)
            return false;

        int xStart = getStartChunk(location.getX(), radiusX);
        int xEnd = getEndChunk(location.getX(), radiusX);
        int zStart = getStartChunk(location.getZ(), radiusZ);
        int zEnd = getEndChunk(location.getZ(), radiusZ);

        for (int x = xStart; x <= xEnd; x++) {
            for (int z = zStart; z <= zEnd; z++) {

                Chunk chunk = world.getChunkAt(x, z);
                if (!chunk.isLoaded())
                    chunk.load();

                Entity[] entities = chunk.getEntities();

                for (Entity entity : entities) {

                    if (!entity.isValid())
                        continue;

                    if (entity.getType() != type)
                        continue;

                    if (!entity.getWorld().equals(world))
                        continue;

                    Location entityLocation = getEntityLocation(entity, NEARBY_ENTITY_LOCATION);

                    if (!LocationUtils.isInRange(location, entityLocation, radiusX, radiusY, radiusZ))
                        continue;

                    if (validator != null && !validator.isValid(entity))
                        continue;

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get a monster spawn egg from an entity type.
     *
     * @param type  The entity type.
     *
     * @return  The spawn egg as an item stack or null if the type does not
     * have an equivalent spawn egg.
     */
    @Nullable
    public static ItemStack getEgg(EntityType type) {
        PreCon.notNull(type);

        switch (type) {
            case CREEPER:
            case SKELETON:
            case SPIDER:
            case GIANT:
            case ZOMBIE:
            case SLIME:
            case GHAST:
            case PIG_ZOMBIE:
            case ENDERMAN:
            case CAVE_SPIDER:
            case SILVERFISH:
            case BLAZE:
            case MAGMA_CUBE:
            case ENDER_DRAGON:
            case WITHER:
            case ENDERMITE:
            case GUARDIAN:
            case PIG:
            case SHEEP:
            case COW:
            case CHICKEN:
            case SQUID:
            case WOLF:
            case MUSHROOM_COW:
            case SNOWMAN:
            case OCELOT:
            case IRON_GOLEM:
            case HORSE:
            case RABBIT:
                return new ItemStack(Material.MONSTER_EGG, 1, (byte)type.getTypeId());
            default:
                return null;
        }
    }

    /*
    * Get the root entity in a passenger/vehicle entity relationship
    */
    public static Entity getRootVehicle(Entity entity) {
        while (entity.getVehicle() != null) {
            entity = entity.getVehicle();
        }
        return entity;
    }

    /**
     * Get the entities location. If the method is invoked from the primary thread, the result
     * is returned in the provided output location, otherwise a new location object is returned.
     *
     * @param entity  The entity to get a location from.
     * @param output  The output location.
     *
     * @return  The output location if invoked on primary thread, otherwise a new location object.
     */
    public static Location getEntityLocation(Entity entity, Location output) {
        return Bukkit.isPrimaryThread()
                ? entity.getLocation(output)
                : entity.getLocation();
    }

    /**
     * Get the entities location. If the method is invoked from the specified thread, the result
     * is returned in the provided output location, otherwise a new location object is returned.
     *
     * @param entity  The entity to get a location from.
     * @param output  The output location.
     * @param thread  The thread to check.
     *
     * @return  The output location if invoked on primary thread, otherwise a new location object.
     */
    public static Location getEntityLocation(Entity entity, Location output, Thread thread) {
        return Thread.currentThread().equals(thread)
                ? entity.getLocation(output)
                : entity.getLocation();
    }

    /**
     * Returns a {@link ITrackedEntity} object used to get the latest instance of an
     * {@link org.bukkit.entity.Entity}.
     *
     * <p>This is used when an entity reference needs to be kept. Entity references are changed
     * whenever the entity is in a chunk that loads/unloads. The entity id can change and references
     * become outdated and no longer represent the intended entity.</p>
     *
     * @param entity  The entity to track.
     */
    public static ITrackedEntity trackEntity(Entity entity) {
        PreCon.notNull(entity);

        return Nucleus.getEntityTracker().trackEntity(entity);
    }

    private static int getStartChunk(double center, double radius) {
        double start = center - radius - 2.0D;
        double chunkStart = Math.floor(start / 16.0D);
        return (int)chunkStart;
    }

    private static int getEndChunk(double center, double radius) {
        double end = center + radius + 2.0D;
        double chunkEnd = Math.floor(end / 16.0D);
        return (int)chunkEnd;
    }
}
