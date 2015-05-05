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
import com.jcwhatever.nucleus.managed.particles.types.ICloudParticle;
import com.jcwhatever.nucleus.utils.PreCon;

/**
 * Implementation of {@link ICloudParticle}.
 */
class CloudParticle extends AbstractAreaVectorParticle implements ICloudParticle {

    private float _speed = 1.0f;

    /**
     * Constructor.
     */
    CloudParticle() {
        super(ParticleType.CLOUD);
    }

    @Override
    public float getSpeed() {
        return _speed;
    }

    @Override
    public void setSpeed(float speed) {
        PreCon.positiveNumber(speed);

        _speed = 1.0f;
    }
}
