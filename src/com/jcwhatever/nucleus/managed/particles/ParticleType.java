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

package com.jcwhatever.nucleus.managed.particles;

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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler.INmsParticleType;

/**
 * Particle effect type.
 */
public class ParticleType<I extends IParticleEffect> implements INmsParticleType {

    public static final ParticleType<IExplosionNormalParticle> EXPLOSION_NORMAL =
            new ParticleType<>("EXPLOSION_NORMAL", IExplosionNormalParticle.class);

    public static final ParticleType<IExplosionLargeParticle> EXPLOSION_LARGE =
            new ParticleType<>("EXPLOSION_LARGE", IExplosionLargeParticle.class);

    public static final ParticleType<IExplosionHugeParticle> EXPLOSION_HUGE =
            new ParticleType<>("EXPLOSION_HUGE", IExplosionHugeParticle.class);

    public static final ParticleType<IFireworksSparkParticle> FIREWORKS_SPARK =
            new ParticleType<>("FIREWORKS_SPARK", IFireworksSparkParticle.class);

    public static final ParticleType<IWaterBubbleParticle> WATER_BUBBLE =
            new ParticleType<>("WATER_BUBBLE", IWaterBubbleParticle.class);

    public static final ParticleType<IWaterSplashParticle> WATER_SPLASH =
            new ParticleType<>("WATER_SPLASH", IWaterSplashParticle.class);

    public static final ParticleType<IWaterWakeParticle> WATER_WAKE =
            new ParticleType<>("WATER_WAKE", IWaterWakeParticle.class);

    public static final ParticleType<IWaterSuspendedParticle> WATER_SUSPENDED =
            new ParticleType<>("SUSPENDED", IWaterSuspendedParticle.class);

    public static final ParticleType<ISuspendedDepthParticle> SUSPENDED_DEPTH =
            new ParticleType<>("SUSPENDED_DEPTH", ISuspendedDepthParticle.class);

    public static final ParticleType<ICriticalHitParticle> CRITICAL_HIT =
            new ParticleType<>("CRIT", ICriticalHitParticle.class);

    public static final ParticleType<ICriticalHitMagicParticle> CRITICAL_HIT_MAGIC =
            new ParticleType<>("CRIT_MAGIC", ICriticalHitMagicParticle.class);

    public static final ParticleType<IDripWaterParticle> DRIP_WATER =
            new ParticleType<>("DRIP_WATER", IDripWaterParticle.class);

    public static final ParticleType<IDripLavaParticle> DRIP_LAVA =
            new ParticleType<>("DRIP_LAVA", IDripLavaParticle.class);

    public static final ParticleType<ISmokeNormalParticle> SMOKE_NORMAL =
            new ParticleType<>("SMOKE_NORMAL", ISmokeNormalParticle.class);

    public static final ParticleType<ISmokeLargeParticle> SMOKE_LARGE =
            new ParticleType<>("SMOKE_LARGE", ISmokeLargeParticle.class);

    public static final ParticleType<ISpellParticle> SPELL =
            new ParticleType<>("SPELL", ISpellParticle.class);

    public static final ParticleType<ISpellInstantParticle> SPELL_INSTANT =
            new ParticleType<>("SPELL_INSTANT", ISpellInstantParticle.class);

    public static final ParticleType<ISpellMobParticle> SPELL_MOB =
            new ParticleType<>("SPELL_MOB", ISpellMobParticle.class);

    public static final ParticleType<ISpellMobAmbientParticle> SPELL_MOB_AMBIENT =
            new ParticleType<>("SPELL_MOB_AMBIENT", ISpellMobAmbientParticle.class);

    public static final ParticleType<ISpellWitchParticle> SPELL_WITCH =
            new ParticleType<>("SPELL_WITCH", ISpellWitchParticle.class);

    public static final ParticleType<IVillagerAngryParticle> VILLAGER_ANGRY =
            new ParticleType<>("VILLAGER_ANGRY", IVillagerAngryParticle.class);

    public static final ParticleType<IVillagerHappyParticle> VILLAGER_HAPPY =
            new ParticleType<>("VILLAGER_HAPPY", IVillagerHappyParticle.class);

    public static final ParticleType<ITownAuraParticle> TOWN_AURA =
            new ParticleType<ITownAuraParticle>("TOWN_AURA", ITownAuraParticle.class);

    public static final ParticleType<INoteParticle> NOTE =
            new ParticleType<>("NOTE", INoteParticle.class);

    public static final ParticleType<IPortalParticle> PORTAL =
            new ParticleType<>("PORTAL", IPortalParticle.class);

    public static final ParticleType<IEnchantmentTableParticle> ENCHANTMENT_TABLE =
            new ParticleType<>("ENCHANTMENT_TABLE", IEnchantmentTableParticle.class);

    public static final ParticleType<IFlameParticle> FLAME =
            new ParticleType<>("FLAME", IFlameParticle.class);

    public static final ParticleType<ILavaParticle> LAVA =
            new ParticleType<>("LAVA", ILavaParticle.class);

    public static final ParticleType<IFootstepParticle> FOOTSTEP =
            new ParticleType<>("FOOTSTEP", IFootstepParticle.class);

    public static final ParticleType<ICloudParticle> CLOUD =
            new ParticleType<>("CLOUD", ICloudParticle.class);

    public static final ParticleType<IRedstoneDustParticle> RED_DUST =
            new ParticleType<>("REDSTONE", IRedstoneDustParticle.class);

    public static final ParticleType<ISnowballParticle> SNOWBALL_POOF =
            new ParticleType<>("SNOWBALL", ISnowballParticle.class);

    public static final ParticleType<ISnowShovelParticle> SNOW_SHOVEL =
            new ParticleType<>("SNOW_SHOVEL", ISnowShovelParticle.class);

    public static final ParticleType<ISlimeParticle> SLIME =
            new ParticleType<>("SLIME", ISlimeParticle.class);

    public static final ParticleType<IHeartParticle> HEART =
            new ParticleType<>("HEART", IHeartParticle.class);

    public static final ParticleType<IBarrierParticle> BARRIER =
            new ParticleType<>("BARRIER", IBarrierParticle.class);

    public static final ParticleType<IWaterDropletParticle> WATER_DROPLET =
            new ParticleType<>("WATER_DROP", IWaterDropletParticle.class);

    public static final ParticleType<IItemCrackParticle> ITEM_CRACK =
            new ParticleType<>("ITEM_CRACK", IItemCrackParticle.class);

    public static final ParticleType<IBlockCrackParticle> BLOCK_CRACK =
            new ParticleType<>("BLOCK_CRACK", IBlockCrackParticle.class);

    public static final ParticleType<IBlockDustParticle> BLOCK_DUST =
            new ParticleType<>("BLOCK_DUST", IBlockDustParticle.class);

    private final String _name;
    private final int[] _ints;
    private final Class<I> _particleClass;

    /**
     * Constructor.
     *
     * @param name           The name of the NMS particle enum constant.
     * @param particleClass  The particle interface class used for the particle type.
     * @param packetInts     Ints used by NMS to insert data into the name of the particle.
     */
    public ParticleType(String name, Class<I> particleClass, int... packetInts) {
        PreCon.notNull(name);
        PreCon.notNull(packetInts);

        _name = name;
        _particleClass = particleClass;
        _ints = packetInts;
    }

    /**
     * Get the particle interface class.
     */
    public Class<I> getParticleClass() {
        return _particleClass;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public int[] getPacketInts() {
        if (_ints.length == 0)
            return new int[0];

        return _ints;
    }
}
