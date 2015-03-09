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

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Utility to filter and identify {@link org.bukkit.entity.EntityType}'s by
 * property.
 */
public class EntityTypes {

    private EntityTypes() {}

    private static Multimap<EntityType, EntityTypeProperty> _properties =
            MultimapBuilder.enumKeys(EntityType.class).hashSetValues().build();

    private static Multimap<EntityTypeProperty, EntityType> _typeByProperty =
            MultimapBuilder.hashKeys().enumSetValues(EntityType.class).build();

    /**
     * Determine if an entity type has a property.
     *
     * @param type          The {@link org.bukkit.entity.EntityType} to check.
     * @param propertyName  The name of the property to check.
     */
    public static boolean hasProperty(EntityType type, String propertyName) {
        PreCon.notNull(type);
        PreCon.notNull(propertyName);

        return hasProperty(type, new EntityTypeProperty(propertyName));
    }

    /**
     * Determine if an entity type has a property.
     *
     * @param type      The {@link org.bukkit.entity.EntityType} to check.
     * @param property  The {@link EntityTypeProperty} to check.
     */
    public static boolean hasProperty(EntityType type, EntityTypeProperty property) {
        PreCon.notNull(type);
        PreCon.notNull(property);

        return _properties.get(type).contains(property);
    }

    /**
     * Get all {@link org.bukkit.entity.EntityType}'s that match the specified properties.
     *
     * @param properties  The {@link EntityTypeProperty}'s to check for.
     */
    public static Set<EntityType> get(EntityTypeProperty... properties) {
        PreCon.notNull(properties);

        Set<EntityType> results = EnumSet.noneOf(EntityType.class);

        for (EntityTypeProperty property : properties) {
            Collection<EntityType> stored = _typeByProperty.get(property);

            Iterator<EntityType> iterator = results.iterator();

            while (iterator.hasNext()) {
                EntityType type = iterator.next();

                if (!stored.contains(type))
                    iterator.remove();
            }

            results.addAll(stored);
        }

        return results;
    }

    /**
     * Get all {@link EntityTypeProperty}'s of the specified {@link org.bukkit.entity.EntityType}.
     *
     * @param type  The entity type to check.
     */
    public static Set<EntityTypeProperty> getProperties(EntityType type) {
        PreCon.notNull(type);

        Collection<EntityTypeProperty> stored = _properties.get(type);

        return new HashSet<>(stored);
    }

    /**
     * Removes {@link org.bukkit.entity.EntityType}'s from the provided
     * {@link java.util.Collection} that have the specified {@link EntityTypeProperty}'s.
     *
     * <p>Requires the provided collections iterator to implement the remove method.</p>
     *
     * @param types       The collection of entity types to modify.
     * @param properties  The properties to remove from the collection.
     */
    public static void remove(Collection<EntityType> types,
                              EntityTypeProperty... properties) {
        PreCon.notNull(types);
        PreCon.notNull(properties);

        Iterator<EntityType> iterator = types.iterator();

        while (iterator.hasNext()) {
            EntityType type = iterator.next();

            for (EntityTypeProperty property : properties) {
                if (hasProperty(type, property)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Determine if the entity type can fly.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#FLY
     */
    public static boolean canFly(EntityType type) {
        return hasProperty(type, EntityTypeProperty.FLY);
    }

    /**
     * Determine if the entity type can walk.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#WALK
     */
    public static boolean canWalk(EntityType type) {
        return hasProperty(type, EntityTypeProperty.WALK);
    }

    /**
     * Determine if the entity type can swim.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#SWIM
     */
    public static boolean canSwim(EntityType type) {
        return hasProperty(type, EntityTypeProperty.SWIM);
    }

    /**
     * Determine if the entity type can climb.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#CLIMB
     */
    public static boolean canClimb(EntityType type) {
        return hasProperty(type, EntityTypeProperty.CLIMB);
    }

    /**
     * Determine if the entity type can be ridden by players.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#RIDE
     */
    public static boolean canRide(EntityType type) {
        return hasProperty(type, EntityTypeProperty.RIDE);
    }

    /**
     * Determine if the entity type can have a saddle attached.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#SADDLE
     */
    public static boolean canSaddle(EntityType type) {
        return hasProperty(type, EntityTypeProperty.SADDLE);
    }

    /**
     * Determine if the entity type can shoot projectiles.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#SHOOT
     */
    public static boolean canShoot(EntityType type) {
        return hasProperty(type, EntityTypeProperty.SHOOT);
    }

    /**
     * Determine if the entity type can teleport itself.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#TELEPORT
     */
    public static boolean canTeleport(EntityType type) {
        return hasProperty(type, EntityTypeProperty.TELEPORT);
    }

    /**
     * Determine if the entity type can explode.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#EXPLODE
     */
    public static boolean canExplode(EntityType type) {
        return hasProperty(type, EntityTypeProperty.EXPLODE);
    }

    /**
     * Determine if the entity type is alive.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#ALIVE
     */
    public static boolean isAlive(EntityType type) {
        return hasProperty(type, EntityTypeProperty.ALIVE);
    }

    /**
     * Determine if the entity type is always hostile towards players.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#HOSTILE
     */
    public static boolean isHostile(EntityType type) {
        return hasProperty(type, EntityTypeProperty.HOSTILE);
    }

    /**
     * Determine if the entity type is hostile when aggravated by
     * a player action.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#HOSTILE_AGRO
     */
    public static boolean isHostileAgro(EntityType type) {
        return hasProperty(type, EntityTypeProperty.HOSTILE_AGRO);
    }

    /**
     * Determine if the entity type is a projectile.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#PROJECTILE
     */
    public static boolean isProjectile(EntityType type) {
        return hasProperty(type, EntityTypeProperty.PROJECTILE);
    }

    /**
     * Determine if the entity type is a boss.
     *
     * @param type  The {@link org.bukkit.entity.EntityType} to check.
     *
     * @see  EntityTypeProperty#BOSS
     */
    public static boolean isBoss(EntityType type) {
        return hasProperty(type, EntityTypeProperty.BOSS);
    }

    /**
     * Determine if the entity type is an animal.
     *
     * @param type The {@link org.bukkit.entity.EntityType} to check.
     * @see EntityTypeProperty#ANIMAL
     */
    public static boolean isAnimal(EntityType type) {
        return hasProperty(type, EntityTypeProperty.ANIMAL);
    }

    /**
     * Determine if the entity type is a monster.
     *
     * @param type The {@link org.bukkit.entity.EntityType} to check.
     * @see EntityTypeProperty#MONSTER
     */
    public static boolean isMonster(EntityType type) {
        return hasProperty(type, EntityTypeProperty.MONSTER);
    }

    private static void add(EntityType type, EntityTypeProperty... properties) {
        _properties.putAll(type, Arrays.asList(properties));
        for (EntityTypeProperty property : properties) {
            _typeByProperty.put(property, type);
        }
    }

    static {

        add(EntityType.ARMOR_STAND);

        add(EntityType.ARROW,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.BAT,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.FLY);

        add(EntityType.BLAZE,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.FLY,
                EntityTypeProperty.SHOOT);

        add(EntityType.BOAT,
                EntityTypeProperty.SWIM);

        add(EntityType.CAVE_SPIDER,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.CLIMB,
                EntityTypeProperty.WALK);

        add(EntityType.CHICKEN,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.COMPLEX_PART);

        add(EntityType.COW,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.CREEPER,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.EXPLODE,
                EntityTypeProperty.WALK);

        add(EntityType.DROPPED_ITEM);

        add(EntityType.EGG,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.ENDER_CRYSTAL);

        add(EntityType.ENDER_DRAGON,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.FLY,
                EntityTypeProperty.BOSS);

        add(EntityType.ENDER_PEARL,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.ENDER_SIGNAL);

        add(EntityType.ENDERMAN,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE_AGRO,
                EntityTypeProperty.TELEPORT,
                EntityTypeProperty.WALK);

        add(EntityType.EXPERIENCE_ORB);

        add(EntityType.FALLING_BLOCK);

        add(EntityType.FIREBALL,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.FIREWORK,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.FISHING_HOOK);

        add(EntityType.GHAST,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.FLY,
                EntityTypeProperty.SHOOT);

        add(EntityType.GIANT,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.WALK);

        add(EntityType.GUARDIAN,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.SWIM);

        add(EntityType.HORSE,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.RIDE,
                EntityTypeProperty.SADDLE,
                EntityTypeProperty.WALK);

        add(EntityType.IRON_GOLEM,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE_AGRO,
                EntityTypeProperty.WALK);

        add(EntityType.ITEM_FRAME);

        add(EntityType.LEASH_HITCH);

        add(EntityType.LIGHTNING,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.MAGMA_CUBE,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.WALK);

        add(EntityType.MINECART,
                EntityTypeProperty.RIDE);

        add(EntityType.MINECART_CHEST);

        add(EntityType.MINECART_FURNACE);

        add(EntityType.MINECART_HOPPER);

        add(EntityType.MINECART_MOB_SPAWNER);

        add(EntityType.MINECART_TNT);

        add(EntityType.MUSHROOM_COW,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.OCELOT,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.PAINTING);

        add(EntityType.PIG,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.RIDE,
                EntityTypeProperty.SADDLE,
                EntityTypeProperty.WALK);

        add(EntityType.PIG_ZOMBIE,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE_AGRO,
                EntityTypeProperty.WALK);

        add(EntityType.PLAYER);

        add(EntityType.PRIMED_TNT,
                EntityTypeProperty.EXPLODE);

        add(EntityType.RABBIT,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.SHEEP,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.SILVERFISH,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.WALK);

        add(EntityType.SKELETON,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.SHOOT,
                EntityTypeProperty.WALK);

        add(EntityType.SLIME,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.WALK);

        add(EntityType.SMALL_FIREBALL,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.SNOWBALL,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.SNOWMAN,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.SPIDER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.CLIMB,
                EntityTypeProperty.WALK);

        add(EntityType.SPLASH_POTION,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.SQUID,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.SWIM);

        add(EntityType.THROWN_EXP_BOTTLE,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.UNKNOWN);

        add(EntityType.VILLAGER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.WALK);

        add(EntityType.WEATHER);

        add(EntityType.WITCH,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.SHOOT,
                EntityTypeProperty.WALK);

        add(EntityType.WITHER,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.SHOOT,
                EntityTypeProperty.FLY,
                EntityTypeProperty.BOSS);

        add(EntityType.WITHER_SKULL,
                EntityTypeProperty.PROJECTILE);

        add(EntityType.WOLF,
                EntityTypeProperty.ANIMAL,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE_AGRO,
                EntityTypeProperty.WALK);

        add(EntityType.ZOMBIE,
                EntityTypeProperty.MONSTER,
                EntityTypeProperty.ALIVE,
                EntityTypeProperty.HOSTILE,
                EntityTypeProperty.WALK);
    }
}
