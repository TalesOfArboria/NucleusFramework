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

import com.jcwhatever.nucleus.managed.particles.IColoredParticle;
import com.jcwhatever.nucleus.managed.particles.ParticleType;

import org.bukkit.Color;

/**
 * Abstract implementation of a particle with color.
 */
abstract class AbstractColorParticle extends AbstractParticle implements IColoredParticle {

    private final ColorHelper _color = new ColorHelper();

    /**
     * Constructor.
     *
     * @param type The particle type.
     */
    AbstractColorParticle(ParticleType type) {
        super(type);
    }

    @Override
    public Color getColor() {
        return _color.getColor();
    }

    @Override
    public void setColor(Color color) {
        _color.setColor(color);
    }

    @Override
    public double getRed() {
        return _color.getRed();
    }

    @Override
    public double getGreen() {
        return _color.getGreen();
    }

    @Override
    public double getBlue() {
        return _color.getBlue();
    }
}
