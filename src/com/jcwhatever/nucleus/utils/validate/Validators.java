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

package com.jcwhatever.nucleus.utils.validate;

import com.jcwhatever.nucleus.providers.npc.Npcs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Common validators.
 */
public final class Validators {

    private Validators() {}

    /**
     * Validates {@link Entity}'s are instances of {@link LivingEntity}.
     */
    public static final IValidator<Entity> LIVING_ENTITIES =
            new LivingEntitiesValidator();

    /**
     * Validates {@link Entity}'s are NOT instances of {@link Player}.
     */
    public static final IValidator<Entity> NON_PLAYER_ENTITIES =
            new NonPlayerEntitiesValidator();

    /**
     * Validates {@link Entity}'s are instances of {@link Player}.
     */
    public static final IValidator<Entity> PLAYER_ENTITIES =
            new PlayerEntitiesValidator();

    /**
     * Validates {@link Entity}'s are NOT NPC's.
     */
    public static final IValidator<Entity> NON_NPC_ENTITIES =
            new NonNPCEntitiesValidator();

    /**
     * Validates {@link LivingEntity}'s are NOT instances of {@link Player}.
     */
    public static final IValidator<LivingEntity> NON_PLAYER_LIVING_ENTITIES =
            new NonPlayerLivingEntitiesValidator();

    /**
     * Validates {@link LivingEntity}'s are instances of {@link Player}.
     */
    public static final IValidator<LivingEntity> PLAYER_LIVING_ENTITIES =
            new PlayerLivingEntitiesValidator();

    /**
     * Validates {@link LivingEntity}'s are NOT NPC's.
     */
    public static final IValidator<LivingEntity> NON_NPC_LIVING_ENTITIES =
            new NonNPCLivingEntitiesValidator();

    /**
     * Validates {@link Player}'s are NOT NPC's.
     */
    public static final IValidator<Player> NON_NPC_PLAYERS =
            new NonNPCPlayersValidator();

    private static class LivingEntitiesValidator implements IValidator<Entity> {

        @Override
        public boolean isValid(Entity entity) {
            return entity instanceof LivingEntity;
        }
    }

    private static class NonPlayerLivingEntitiesValidator implements IValidator<LivingEntity> {

        @Override
        public boolean isValid(LivingEntity entity) {
            return !(entity instanceof Player);
        }
    }

    private static class PlayerLivingEntitiesValidator implements IValidator<LivingEntity> {

        @Override
        public boolean isValid(LivingEntity entity) {
            return entity instanceof Player;
        }
    }

    private static class NonNPCLivingEntitiesValidator implements IValidator<LivingEntity> {

        @Override
        public boolean isValid(LivingEntity entity) {
            return !Npcs.isNpc(entity);
        }
    }

    private static class PlayerEntitiesValidator implements IValidator<Entity> {

        @Override
        public boolean isValid(Entity entity) {
            return entity instanceof Player;
        }
    }

    private static class NonPlayerEntitiesValidator implements IValidator<Entity> {

        @Override
        public boolean isValid(Entity entity) {
            return !(entity instanceof Player);
        }
    }

    private static class NonNPCEntitiesValidator implements IValidator<Entity> {

        @Override
        public boolean isValid(Entity entity) {
            return !Npcs.isNpc(entity);
        }
    }

    private static class NonNPCPlayersValidator implements IValidator<Player> {

        @Override
        public boolean isValid(Player player) {
            return !Npcs.isNpc(player);
        }
    }
}
