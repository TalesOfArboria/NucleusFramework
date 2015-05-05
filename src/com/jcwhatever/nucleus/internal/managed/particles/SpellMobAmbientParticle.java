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

import com.jcwhatever.nucleus.managed.particles.ParticleType;
import com.jcwhatever.nucleus.managed.particles.types.ISpellMobAmbientParticle;

/**
 * Implementation of {@link ISpellMobAmbientParticle}.
 */
class SpellMobAmbientParticle extends AbstractRGBColorParticle
        implements ISpellMobAmbientParticle {

    private final AreaHelper _area = new AreaHelper();

    /**
     * Constructor.
     */
    SpellMobAmbientParticle() {
        super(ParticleType.SPELL_MOB_AMBIENT);
    }

    @Override
    public double getXArea() {
        return _area.getXArea();
    }

    @Override
    public double getYArea() {
        return _area.getYArea();
    }

    @Override
    public double getZArea() {
        return _area.getZArea();
    }

    @Override
    public void setXArea(double areaSize) {
        _area.setXArea(areaSize);
    }

    @Override
    public void setYArea(double areaSize) {
        _area.setYArea(areaSize);
    }

    @Override
    public void setZArea(double areaSize) {
        _area.setZArea(areaSize);
    }

    @Override
    public void setArea(double areaSize) {
        _area.setArea(areaSize);
    }
}
