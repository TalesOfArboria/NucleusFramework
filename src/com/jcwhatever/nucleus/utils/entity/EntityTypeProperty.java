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

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Represents a property of an {@link org.bukkit.entity.EntityType}.
 *
 * @see EntityTypes
 */
public class EntityTypeProperty implements INamedInsensitive {

    /**
     * The entity can fly.
     */
    public static final EntityTypeProperty FLY = new EntityTypeProperty("Fly");
    /**
     * The entity can walk on the ground or otherwise generally move along the ground.
     */
    public static final EntityTypeProperty WALK = new EntityTypeProperty("Walk");
    /**
     * The entity can swim.
     */
    public static final EntityTypeProperty SWIM = new EntityTypeProperty("Swim");
    /**
     * The entity can climb.
     */
    public static final EntityTypeProperty CLIMB = new EntityTypeProperty("Fly");
    /**
     * The entity can be mounted by right clicking.
     */
    public static final EntityTypeProperty RIDE = new EntityTypeProperty("Ride");
    /**
     * The entity can have a saddle attached to it.
     */
    public static final EntityTypeProperty SADDLE = new EntityTypeProperty("Saddle");
    /**
     * The entity is alive.
     */
    public static final EntityTypeProperty ALIVE = new EntityTypeProperty("Alive");
    /**
     * The entity is generally always hostile towards players.
     */
    public static final EntityTypeProperty HOSTILE = new EntityTypeProperty("Hostile");
    /**
     * The entity is hostile towards players when aggravated through a player action.
     */
    public static final EntityTypeProperty HOSTILE_AGRO = new EntityTypeProperty("Hostile_Agro");
    /**
     * The entity is a projectile.
     */
    public static final EntityTypeProperty PROJECTILE = new EntityTypeProperty("Projectile");
    /**
     * The entity shoots projectiles.
     */
    public static final EntityTypeProperty SHOOT = new EntityTypeProperty("Shoots");
    /**
     * The entity can teleport itself.
     */
    public static final EntityTypeProperty TELEPORT = new EntityTypeProperty("Teleport");
    /**
     * The entity can explode.
     */
    public static final EntityTypeProperty EXPLODE = new EntityTypeProperty("Explode");
    /**
     * The entity is a boss.
     */
    public static final EntityTypeProperty BOSS = new EntityTypeProperty("Boss");
    /**
     * The entity is an animal.
     * <p/>
     * <p>Refers to generally non-hostile mobs that represent real life creatures.</p>
     */
    public static final EntityTypeProperty ANIMAL = new EntityTypeProperty("Animal");
    /**
     * The entity is a monster.
     * <p/>
     * <p>Refers to any living mob that is hostile and/or non-natural.</p>
     */
    public static final EntityTypeProperty MONSTER = new EntityTypeProperty("Monster");

    private final String _name;
    private final String _searchName;

    public EntityTypeProperty(String name) {
        PreCon.notNullOrEmpty(name);

        _name = name;
        _searchName = name.toLowerCase();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    @Override
    public int hashCode() {
        return _searchName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntityTypeProperty &&
                ((EntityTypeProperty) obj)._searchName.equals(_searchName);
    }

    @Override
    public String toString() {
        return _name;
    }
}
