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

package com.jcwhatever.nucleus.internal.managed.particles;

import com.jcwhatever.nucleus.managed.particles.IParticleEffect;
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
import com.jcwhatever.nucleus.utils.PreCon;

import javax.annotation.Nullable;

/**
 * Implementation of {@link IParticleEffectFactory}.
 */
public final class InternalParticleEffectFactory implements IParticleEffectFactory {

    @Nullable
    @Override
    public <T extends IParticleEffect> T create(ParticleType<T> type) {
        PreCon.notNull(type);

        @SuppressWarnings("unchecked")
        T result = (T)getParticleInstance(type);

        return result;
    }

    private Object getParticleInstance(ParticleType type) {

        Class<?> clazz = type.getParticleClass();

        if (clazz == IBarrierParticle.class)
            return new BarrierParticle();

        if (clazz == IBlockCrackParticle.class)
            return new BlockCrackParticle();

        if (clazz == IBlockDustParticle.class)
            return new BlockDustParticle();

        if (clazz == ICloudParticle.class)
            return new CloudParticle();

        if (clazz == ICriticalHitMagicParticle.class)
            return new CriticalHitMagicParticle();

        if (clazz == ICriticalHitParticle.class)
            return new CriticalHitParticle();

        if (clazz == IDripLavaParticle.class)
            return new DripLavaParticle();

        if (clazz == IDripWaterParticle.class)
            return new DripWaterParticle();

        if (clazz == IEnchantmentTableParticle.class)
            return new EnchantmentTableParticle();

        if (clazz == IExplosionHugeParticle.class)
            return new ExplosionHugeParticle();

        if (clazz == IExplosionLargeParticle.class)
            return new ExplosionLargeParticle();

        if (clazz == IExplosionNormalParticle.class)
            return new ExplosionNormalParticle();

        if (clazz == IFireworksSparkParticle.class)
            return new FireworksSparkParticle();

        if (clazz == IFlameParticle.class)
            return new FlameParticle();

        if (clazz == IFootstepParticle.class)
            return new FootstepParticle();

        if (clazz == IHeartParticle.class)
            return new HeartParticle();

        if (clazz == IItemCrackParticle.class)
            return new ItemCrackParticle();

        if (clazz == ILavaParticle.class)
            return new LavaParticle();

        if (clazz == INoteParticle.class)
            return new NoteParticle();

        if (clazz == IPortalParticle.class)
            return new PortalParticle();

        if (clazz == IRedstoneDustParticle.class)
            return new RedstoneDustParticle();

        if (clazz == ISlimeParticle.class)
            return new SlimeParticle();

        if (clazz == ISmokeLargeParticle.class)
            return new SmokeLargeParticle();

        if (clazz == ISmokeNormalParticle.class)
            return new SmokeNormalParticle();

        if (clazz == ISnowballParticle.class)
            return new SnowballParticle();

        if (clazz == ISnowShovelParticle.class)
            return new SnowShovelParticle();

        if (clazz == ISpellInstantParticle.class)
            return new SpellInstantParticle();

        if (clazz == ISpellMobAmbientParticle.class)
            return new SpellMobAmbientParticle();

        if (clazz == ISpellMobParticle.class)
            return new SpellMobParticle();

        if (clazz == ISpellParticle.class)
            return new SpellParticle();

        if (clazz == ISpellWitchParticle.class)
            return new SpellWitchParticle();

        if (clazz == ISuspendedDepthParticle.class)
            return new SuspendedDepthParticle();

        if (clazz == ITownAuraParticle.class)
            return new TownAuraParticle();

        if (clazz == IVillagerAngryParticle.class)
            return new VillagerAngryParticle();

        if (clazz == IVillagerHappyParticle.class)
            return new VillagerHappyParticle();

        if (clazz == IWaterBubbleParticle.class)
            return new WaterBubbleParticle();

        if (clazz == IWaterDropletParticle.class)
            return new WaterDropletParticle();

        if (clazz == IWaterSplashParticle.class)
            return new WaterSplashParticle();

        if (clazz == IWaterSuspendedParticle.class)
            return new WaterSuspendedParticle();

        if (clazz == IWaterWakeParticle.class)
            return new WaterWakeParticle();

        return null;
    }
}
