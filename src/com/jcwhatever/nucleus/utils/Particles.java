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

package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.particles.IParticleEffectFactory;
import com.jcwhatever.nucleus.managed.particles.ParticleType;
import com.jcwhatever.nucleus.managed.particles.types.IBarrierParticle;
import com.jcwhatever.nucleus.managed.particles.types.IBlockCrackParticle;
import com.jcwhatever.nucleus.managed.particles.types.IBlockDustParticle;
import com.jcwhatever.nucleus.managed.particles.types.ICloudParticle;
import com.jcwhatever.nucleus.managed.particles.types.ICriticalHitMagicParticle;
import com.jcwhatever.nucleus.managed.particles.types.ICriticalHitParticle;
import com.jcwhatever.nucleus.managed.particles.types.IDripLavaParticle;
import com.jcwhatever.nucleus.managed.particles.types.IDripWaterParticle;
import com.jcwhatever.nucleus.managed.particles.types.IEnchantmentTableParticle;
import com.jcwhatever.nucleus.managed.particles.types.IExplosionHugeParticle;
import com.jcwhatever.nucleus.managed.particles.types.IExplosionLargeParticle;
import com.jcwhatever.nucleus.managed.particles.types.IExplosionNormalParticle;
import com.jcwhatever.nucleus.managed.particles.types.IFireworksSparkParticle;
import com.jcwhatever.nucleus.managed.particles.types.IFlameParticle;
import com.jcwhatever.nucleus.managed.particles.types.IFootstepParticle;
import com.jcwhatever.nucleus.managed.particles.types.IHeartParticle;
import com.jcwhatever.nucleus.managed.particles.types.IItemCrackParticle;
import com.jcwhatever.nucleus.managed.particles.types.ILavaParticle;
import com.jcwhatever.nucleus.managed.particles.types.INoteParticle;
import com.jcwhatever.nucleus.managed.particles.types.IPortalParticle;
import com.jcwhatever.nucleus.managed.particles.types.IRedstoneDustParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISlimeParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISmokeLargeParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISmokeNormalParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISnowShovelParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISnowballParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISpellInstantParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISpellMobAmbientParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISpellMobParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISpellParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISpellWitchParticle;
import com.jcwhatever.nucleus.managed.particles.types.ISuspendedDepthParticle;
import com.jcwhatever.nucleus.managed.particles.types.ITownAuraParticle;
import com.jcwhatever.nucleus.managed.particles.types.IVillagerAngryParticle;
import com.jcwhatever.nucleus.managed.particles.types.IVillagerHappyParticle;
import com.jcwhatever.nucleus.managed.particles.types.IWaterBubbleParticle;
import com.jcwhatever.nucleus.managed.particles.types.IWaterDropletParticle;
import com.jcwhatever.nucleus.managed.particles.types.IWaterSplashParticle;
import com.jcwhatever.nucleus.managed.particles.types.IWaterSuspendedParticle;
import com.jcwhatever.nucleus.managed.particles.types.IWaterWakeParticle;

/**
 * Particle effect factory utility.
 */
public final class Particles {

    private Particles() {}

    /**
     * Barrier block effect.
     */
    public static IBarrierParticle createBarrier() {
        return factory().create(ParticleType.BARRIER);
    }

    /**
     * Block break and sprinting effect.
     */
    public static IBlockCrackParticle createBlockCrack() {
        return factory().create(ParticleType.BLOCK_CRACK);
    }

    /**
     * Entity land on ground effect.
     */
    public static IBlockDustParticle createBlockDust() {
        return factory().create(ParticleType.BLOCK_DUST);
    }

    /**
     * Entity death effect.
     */
    public static ICloudParticle createCloud() {
        return factory().create(ParticleType.CLOUD);
    }

    /**
     * Critical hit with enchanted weapon effect.
     */
    public static ICriticalHitMagicParticle createCriticalHitMagic() {
        return factory().create(ParticleType.CRITICAL_HIT_MAGIC);
    }

    /**
     * Critical hit and arrow hit effect.
     */
    public static ICriticalHitParticle createCriticalHit() {
        return factory().create(ParticleType.CRITICAL_HIT);
    }

    /**
     * Lava dripping from ceiling effect.
     */
    public static IDripLavaParticle createDripLava() {
        return factory().create(ParticleType.DRIP_LAVA);
    }

    /**
     * Water dripping from ceiling effect.
     */
    public static IDripWaterParticle createDripWater() {
        return factory().create(ParticleType.DRIP_WATER);
    }

    /**
     * Enchantment table near book shelves effect.
     */
    public static IEnchantmentTableParticle createEnchantmentTable() {
        return factory().create(ParticleType.ENCHANTMENT_TABLE);
    }

    /**
     * TNT/Creeper explosion effect.
     */
    public static IExplosionHugeParticle createExplosionHuge() {
        return factory().create(ParticleType.EXPLOSION_HUGE);
    }

    /**
     * Ghast fireball and wither skull explosion effect.
     */
    public static IExplosionLargeParticle createExplosionLarge() {
        return factory().create(ParticleType.EXPLOSION_LARGE);
    }

    /**
     * TNT/Creeper explosion effect.
     */
    public static IExplosionNormalParticle createExplosionNormal() {
        return factory().create(ParticleType.EXPLOSION_NORMAL);
    }

    /**
     * Fireworks launch effect.
     */
    public static IFireworksSparkParticle createFireworksSpark() {
        return factory().create(ParticleType.FIREWORKS_SPARK);
    }

    /**
     * Monster spawner, torch, furnaces and magma cube flame effect.
     */
    public static IFlameParticle createFlame() {
        return factory().create(ParticleType.FLAME);
    }

    public static IFootstepParticle createFootstep() {
        return factory().create(ParticleType.FOOTSTEP);
    }

    /**
     * Animal taming and breeding effect.
     */
    public static IHeartParticle createHeart() {
        return factory().create(ParticleType.HEART);
    }

    /**
     * Tool break and egg hit effect.
     */
    public static IItemCrackParticle createItemCrack() {
        return factory().create(ParticleType.ITEM_CRACK);
    }

    /**
     * Lava block effect.
     */
    public static ILavaParticle createLava() {
        return factory().create(ParticleType.LAVA);
    }

    /**
     * Note block effect.
     */
    public static INoteParticle createNote() {
        return factory().create(ParticleType.NOTE);
    }

    /**
     * Nether portal, endermen, ender pearls, etc. effect.
     */
    public static IPortalParticle createPortal() {
        return factory().create(ParticleType.PORTAL);
    }

    /**
     * Redstone and redstone related block effect.
     */
    public static IRedstoneDustParticle createRedstoneDust() {
        return factory().create(ParticleType.RED_DUST);
    }

    /**
     * Slime entity effect.
     */
    public static ISlimeParticle createSlime() {
        return factory().create(ParticleType.SLIME);
    }

    /**
     * Smoke from fire and blazes.
     */
    public static ISmokeLargeParticle createSmokeLarge() {
        return factory().create(ParticleType.SMOKE_LARGE);
    }

    /**
     * Smoke from torches, end portals, brewing stands, TNT, droppers and
     * dispensers.
     */
    public static ISmokeNormalParticle createSmokeNormal() {
        return factory().create(ParticleType.SMOKE_NORMAL);
    }

    /**
     * Thrown snowball hit effect.
     */
    public static ISnowballParticle createSnowballPoof() {
        return factory().create(ParticleType.SNOWBALL_POOF);
    }

    public static ISnowShovelParticle createSnowShovel() {
        return factory().create(ParticleType.SNOW_SHOVEL);
    }

    /**
     * Instant splash potion break effect.
     */
    public static ISpellInstantParticle createSpellInstant() {
        return factory().create(ParticleType.SPELL_INSTANT);
    }

    /**
     * Entity enchanted by beacon effect.
     */
    public static ISpellMobAmbientParticle createSpellMobAmbient() {
        return factory().create(ParticleType.SPELL_MOB_AMBIENT);
    }

    /**
     * Entity enchanted effect.
     */
    public static ISpellMobParticle createSpellMob() {
        return factory().create(ParticleType.SPELL_MOB);
    }

    /**
     * Splash potion break effect.
     */
    public static ISpellParticle createSpell() {
        return factory().create(ParticleType.SPELL);
    }

    /**
     * Witch entity effect.
     */
    public static ISpellWitchParticle createSpellWitch() {
        return factory().create(ParticleType.SPELL_WITCH);
    }

    /**
     * Bedrock and void effect.
     */
    public static ISuspendedDepthParticle createSuspendedDepth() {
        return factory().create(ParticleType.SUSPENDED_DEPTH);
    }

    /**
     * Mycelium block effect.
     */
    public static ITownAuraParticle createTownAura() {
        return factory().create(ParticleType.TOWN_AURA);
    }

    /**
     * Villager entity when trading or using bone meal effect.
     */
    public static IVillagerHappyParticle createVillagerHappy() {
        return factory().create(ParticleType.VILLAGER_HAPPY);
    }

    /**
     * Villager entity effect when attacked.
     */
    public static IVillagerAngryParticle createVillagerAngry() {
        return factory().create(ParticleType.VILLAGER_ANGRY);
    }

    /**
     * Swimming entity effect.
     */
    public static IWaterBubbleParticle createWaterBubble() {
        return factory().create(ParticleType.WATER_BUBBLE);
    }

    /**
     * Rain water hitting the ground effect.
     */
    public static IWaterDropletParticle createWaterDroplet() {
        return factory().create(ParticleType.WATER_DROPLET);
    }

    /**
     * Swimming entity and shaking wolves effect.
     */
    public static IWaterSplashParticle createWaterSplash() {
        return factory().create(ParticleType.WATER_SPLASH);
    }

    /**
     * Water effect.
     */
    public static IWaterSuspendedParticle createWaterSuspended() {
        return factory().create(ParticleType.WATER_SUSPENDED);
    }

    /**
     * Fishing effect.
     */
    public static IWaterWakeParticle createWaterWake() {
        return factory().create(ParticleType.WATER_WAKE);
    }

    public static IParticleEffectFactory factory() {
        return Nucleus.getParticleEffects();
    }
}
