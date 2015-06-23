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
import com.jcwhatever.nucleus.managed.particles.types.INoteParticle;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Implementation of {@link INoteParticle}.
 */
class NoteParticle extends AbstractParticle implements INoteParticle {

    private final AreaHelper _area = new AreaHelper();
    private double _color;

    /**
     * Constructor.
     */
    NoteParticle() {
        super(ParticleType.NOTE);
    }

    @Override
    public double getColor() {
        return _color;
    }

    @Override
    public void setColor(double color) {
        PreCon.positiveNumber(color);

        _color = color;
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

    @Override
    protected void showParticleTo(INmsParticleEffectHandler handler,
                                 Collection<? extends Player> players,
                                  double x, double y, double z, int count) {

        if (count > 0) {
            handler.send(players, getType(), true, x, y, z,
                    getXArea(), getYArea(), getZArea(), 1, count);
        }
        else {

            for (int i=0; i <= count; i++) {

                double gx = x + Rand.getGaussian(0, getXArea());
                double gy = y + Rand.getGaussian(0, getYArea());
                double gz = z + Rand.getGaussian(0, getZArea());

                handler.send(players, getType(), true, gx, gy, gz,
                        getColor(), 0, 0, 1, 0);
            }
        }
    }
}
