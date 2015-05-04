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

import com.jcwhatever.nucleus.managed.particles.IDirectionalParticle;
import com.jcwhatever.nucleus.managed.particles.ParticleType;

import org.bukkit.util.Vector;

/**
 * Abstract directional particle.
 */
abstract class AbstractDirectionalParticle extends AbstractParticle implements IDirectionalParticle {

    private final DirectionalHelper _direction = new DirectionalHelper();

    /**
     * Constructor.
     *
     * @param type The particle type.
     */
    AbstractDirectionalParticle(ParticleType type) {
        super(type);
    }

    @Override
    public Vector getVector() {
        return _direction.getVector();
    }

    @Override
    public Vector getVector(Vector output) {
        return _direction.getVector(output);
    }

    @Override
    public void setVector(Vector vector) {
        _direction.setVector(vector);
    }
}
