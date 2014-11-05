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


package com.jcwhatever.bukkit.generic.extended;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps extra properties to Bukkits EntityType enum and
 * provides a way to get a list of materials based on those
 * properties.
 */
public enum EntityTypeExt {

    ARROW                (EntityType.ARROW,                 EP.PROJECTILE),
    BAT                  (EntityType.BAT,                   EP.FLY),
    BLAZE                (EntityType.BLAZE,                 EP.HOSTILE, EP.FLY, EP.SHOOTS),
    BOAT                 (EntityType.BOAT),
    CAVE_SPIDER          (EntityType.CAVE_SPIDER,           EP.HOSTILE, EP.CLIMB, EP.WALKS),
    CHICKEN              (EntityType.CHICKEN,               EP.WALKS),
    COMPLEXT_PART        (EntityType.COMPLEX_PART),
    COW                  (EntityType.COW,                   EP.WALKS),
    CREEPER              (EntityType.CREEPER,               EP.HOSTILE, EP.EXPLODES, EP.WALKS),
    DROPPED_ITEM         (EntityType.DROPPED_ITEM),
    EGG                  (EntityType.EGG,                   EP.PROJECTILE),
    ENDER_CRYSTAL        (EntityType.ENDER_CRYSTAL),
    ENDER_DRAGON         (EntityType.ENDER_DRAGON,          EP.HOSTILE, EP.FLY, EP.BOSS),
    ENDER_PEARL          (EntityType.ENDER_PEARL,           EP.PROJECTILE),
    ENDER_SIGNAL         (EntityType.ENDER_SIGNAL),
    ENDERMAN             (EntityType.ENDERMAN,              EP.HOSTILE, EP.TELEPORT, EP.WALKS),
    EXPERIENCE_ORB       (EntityType.EXPERIENCE_ORB),
    FALLING_BLOCK        (EntityType.FALLING_BLOCK),
    FIREBALL             (EntityType.FIREBALL,              EP.PROJECTILE),
    FIREWORK             (EntityType.FIREWORK,              EP.PROJECTILE),
    FISHING_HOOK         (EntityType.FISHING_HOOK),
    GHAST                (EntityType.GHAST,                 EP.HOSTILE, EP.FLY, EP.SHOOTS),
    GIANT                (EntityType.GIANT,                 EP.HOSTILE, EP.WALKS),
    HORSE                (EntityType.HORSE,                 EP.RIDE, EP.WALKS),
    IRON_GOLEM           (EntityType.IRON_GOLEM,            EP.WALKS),
    ITEM_FRAME           (EntityType.ITEM_FRAME),
    LEASH_HITCH          (EntityType.LEASH_HITCH),
    LIGHTNING            (EntityType.LIGHTNING,             EP.PROJECTILE),
    MAGMA_CUBE           (EntityType.MAGMA_CUBE,            EP.HOSTILE, EP.WALKS),
    MINECART             (EntityType.MINECART,              EP.RIDE),
    MINECART_CHEST       (EntityType.MINECART_CHEST),
    MINECART_FURNACE     (EntityType.MINECART_FURNACE),
    MINECART_HOPPER      (EntityType.MINECART_HOPPER),
    MINECART_MOB_SPAWNER (EntityType.MINECART_MOB_SPAWNER),
    MINECART_TNT         (EntityType.MINECART_TNT),
    MUSHROOM_COW         (EntityType.MUSHROOM_COW,          EP.WALKS),
    OCELOT               (EntityType.OCELOT,                EP.WALKS),
    PAINTING             (EntityType.PAINTING),
    PIG                  (EntityType.PIG,                   EP.RIDE, EP.WALKS),
    PIG_ZOMBIE           (EntityType.PIG_ZOMBIE,            EP.HOSTILE, EP.WALKS),
    PLAYER               (EntityType.PLAYER),
    PRIMED_TNT           (EntityType.PRIMED_TNT,            EP.EXPLODES),
    SHEEP                (EntityType.SHEEP,                 EP.WALKS),
    SILVERFISH           (EntityType.SILVERFISH,            EP.HOSTILE, EP.WALKS),
    SKELETON             (EntityType.SKELETON,              EP.HOSTILE, EP.SHOOTS, EP.WALKS),
    SLIME                (EntityType.SLIME,                 EP.HOSTILE, EP.WALKS),
    SMALL_FIREBALL       (EntityType.SMALL_FIREBALL,        EP.PROJECTILE),
    SNOWBALL             (EntityType.SNOWBALL,              EP.PROJECTILE),
    SNOWMAN              (EntityType.SNOWMAN,               EP.WALKS),
    SPIDER               (EntityType.SPIDER,                EP.HOSTILE, EP.CLIMB, EP.WALKS),
    SPLASH_POTION        (EntityType.SPLASH_POTION,         EP.PROJECTILE),
    SQUID                (EntityType.SQUID,                 EP.SWIMS),
    THROWN_EXP_BOTTLE    (EntityType.THROWN_EXP_BOTTLE,     EP.PROJECTILE),
    UNKNOWN              (EntityType.UNKNOWN),
    VILLAGER             (EntityType.VILLAGER,              EP.WALKS),
    WEATHER              (EntityType.WEATHER),
    WITCH                (EntityType.WITCH,                 EP.HOSTILE, EP.SHOOTS, EP.WALKS),
    WITHER               (EntityType.WITHER,                EP.HOSTILE, EP.SHOOTS, EP.FLY, EP.BOSS),
    WITHER_SKULL         (EntityType.WITHER_SKULL,          EP.PROJECTILE),
    WOLF                 (EntityType.WOLF,                  EP.HOSTILE, EP.WALKS),
    ZOMBIE               (EntityType.ZOMBIE,                EP.HOSTILE, EP.WALKS)
    ;


    /**
     * Represents properties of a Bukkit EntityType.
     */
    public enum EntityProperty {
        /**
         * Represents an entity that can fly.
         */
        FLY(1),
        /**
         * Represents an entity that moves on the ground.
         */
        WALKS(2),
        /**
         * Represents an entity whose natural environment
         * is in the water.
         */
        SWIMS(4),
        /**
         * Represents an entity capable of climbing up walls.
         */
        CLIMB(8),
        /**
         * Represents an entity that can be ridden using a saddle.
         */
        RIDE(16),
        /**
         * Represents an entity with AI
         */
        ALIVE(32),
        /**
         * Represents an entity that is or can be hostile towards
         * players.
         */
        HOSTILE(64),
        /**
         * Represents an entity that moves based on a vector.
         */
        PROJECTILE(128),
        /**
         * Represents an entity that fires projectiles.
         */
        SHOOTS(256),
        /**
         * Represents an entity that explodes.
         */
        EXPLODES(512),
        /**
         * Represents an entity that can teleport to a different location.
         */
        TELEPORT(1024),
        /**
         * Represents a Boss Mob entity
         */
        BOSS(2048);

        private final int _bit;

        EntityProperty(int bit) {
            _bit = bit;
        }

        public int getBit() {
            return _bit;
        }
    }

    /**
     * Private copy of EntityProperty to reduce amount of code
     * and create readability in enum constructors.
     */
    private enum EP {
        FLY(1),
        WALKS(2),
        SWIMS(4),
        CLIMB(8),
        RIDE(16),
        // MOB 32,
        HOSTILE(64),
        PROJECTILE(128),
        SHOOTS(256),
        EXPLODES(512),
        TELEPORT(1024),
        BOSS(2048);

        private final int _bit;

        EP(int bit) {
            _bit = bit;
        }

        public int getBit() {
            return _bit;
        }
    }

    private static Map<EntityType, EntityTypeExt> _entityMap;
    private static Map<Integer, List<EntityTypeExt>> _matchCache = new HashMap<Integer, List<EntityTypeExt>>(100);

    private final EntityType _entityType;
    private final int _flags;

    private boolean _canFly;
    private boolean _canWalk;
    private boolean _canSwim;
    private boolean _canClimb;
    private boolean _canShoot;
    private boolean _canExplode;
    private boolean _canTeleport;
    private boolean _isRideable;
    private boolean _isHostile;
    private boolean _isProjectile;
    private boolean _isBoss;

    EntityTypeExt(EntityType type, EP... properties) {

        int flags = 0;
        for (EP property : properties) {
            flags |= property.getBit();
        }

        if (type != null) {
            if (type.isAlive())
                flags |= EntityProperty.ALIVE.getBit();
        }

        _flags = flags;
        _entityType = type;
        _canFly = hasProp(EntityProperty.FLY);
        _canWalk = hasProp(EntityProperty.WALKS);
        _canSwim = hasProp(EntityProperty.SWIMS);
        _canClimb = hasProp(EntityProperty.CLIMB);
        _canShoot = hasProp(EntityProperty.SHOOTS);
        _canExplode = hasProp(EntityProperty.EXPLODES);
        _canTeleport = hasProp(EntityProperty.TELEPORT);
        _isRideable = hasProp(EntityProperty.RIDE);
        _isHostile = hasProp(EntityProperty.HOSTILE);
        _isProjectile = hasProp(EntityProperty.PROJECTILE);
        _isBoss = hasProp(EntityProperty.BOSS);
    }

    /**
     * Get the Bukkit {@code EntityType}
     */
    public EntityType getType() {
        return _entityType;
    }

    /**
     * Determine if the entity can fly.
     */
    public boolean canFly() {
        return _canFly;
    }

    /**
     * Determine if the entity moves on the ground.
     */
    public boolean canWalk() {
        return _canWalk;
    }

    /**
     * Determine if the entities natural environment is
     * in the water.
     */
    public boolean canSwim() {
        return _canSwim;
    }

    /**
     * Determine if the entity is a projectile fired
     * from a source using a vector.
     */
    public boolean isProjectile() {
        return _isProjectile;
    }

    /**
     * Determine if the entity is a living entity.
     */
    public boolean isAlive() {
        return _entityType.isAlive();
    }

    /**
     * Determine if the entity is or can be hostile towards
     * players.
     */
    public boolean isHostile() {
        return _isHostile;
    }

    /**
     * Determine if the entity can explode.
     */
    public boolean canExplode() {
        return _canExplode;
    }

    /**
     * Determine if the entity can fire projectiles.
     */
    public boolean canShoot() {
        return _canShoot;
    }

    /**
     * Determine if the entity is capable of climbing up walls.
     */
    public boolean canClimb() {
        return _canClimb;
    }

    /**
     * Determine if the entity is a boss mob.
     * @return
     */
    public boolean isBoss() {
        return _isBoss;
    }

    /**
     * Determine if the entity can be ridden using right click.
     */
    public boolean isRideable() {
        return _isRideable;
    }

    /**
     * Determine if the entity can teleport to another location.
     */
    public boolean canTeleport() {
        return _canTeleport;
    }

    /**
     * Determine if the entity can be spawned.
     */
    public boolean isSpawnable() {
        return _entityType.isSpawnable();
    }

    /**
     * Determine if the entity has the specified property.
     *
     * @param property  The property to check.
     */
    public boolean hasProp(EntityProperty property) {
        return (_flags & property.getBit()) == property.getBit();
    }


    /**
     * Get an {@code EntityTypeExt} enum constant that represents the
     * specified Bukkit {@code EntityType} enum constant.
     *
     * @param type  The Bukkit {@code EntityType}.
     */
    public static EntityTypeExt from(EntityType type) {
        PreCon.notNull(type);

        buildEntityMap();

        EntityTypeExt result = _entityMap.get(type);
        return result != null ? result : EntityTypeExt.UNKNOWN;
    }

    /**
     * Get a list of {@code EntityTypeExt} that have the specified properties.
     *
     * @param properties  The properties to check for.
     */
    public static List<EntityTypeExt> getMatching(EntityProperty... properties) {
        int flags = 0;

        for (EntityProperty property : properties) {
            flags |= property.getBit();
        }

        List<EntityTypeExt> matches = _matchCache.get(flags);

        if (matches != null)
            return new ArrayList<EntityTypeExt>(matches);

        matches = new ArrayList<EntityTypeExt>(100);

        for (EntityTypeExt ext : EntityTypeExt.values()) {

            boolean add = true;

            for (EntityProperty prop : properties) {
                if (!ext.hasProp(prop)) {
                    add = false;
                    break;
                }
            }

            if (add) {
                matches.add(ext);
            }

        }

        _matchCache.put(flags, matches);

        return new ArrayList<EntityTypeExt>(matches);
    }

    /**
     * Get the entity type using its non-case sensitive enum string.
     *
     * @param name  The name of the entity.
     */
    @Nullable
    public static EntityType entityTypeFromString(String name) {
        PreCon.notNullOrEmpty(name);

        try {
            return EntityType.valueOf(name.toUpperCase());
        }
        catch (Exception e) {
            return null;
        }
    }

    // build the entity type to extended entity type  map.
    private static void buildEntityMap() {
        if (_entityMap != null)
            return;

        _entityMap = new EnumMap<>(EntityType.class);

        for (EntityTypeExt ext : EntityTypeExt.values()) {
            if (ext.getType() != null)
                _entityMap.put(ext.getType(), ext);
        }

    }
}