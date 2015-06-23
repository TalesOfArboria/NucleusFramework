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

package com.jcwhatever.nucleus.utils.nms;

import com.jcwhatever.nucleus.mixins.INamed;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface for NucleusFramework's Minecraft Particle Effect handler.
 *
 * @see NmsUtils
 */
public interface INmsParticleEffectHandler {

    /**
     * Send a particle effect to a collection of players.
     *
     * @param players   The players.
     * @param force     True to force particle, otherwise false.
     * @param particle  The particle effect to send.
     * @param x         The X coordinates to display the effect.
     * @param y         The Y coordinates to display the effect.
     * @param z         The Z coordinates to display the effect.
     * @param dataX     The offset from the X coordinates.
     * @param dataY     The offset from the Y coordinates.
     * @param dataZ     The offset from the Z coordinates.
     * @param data      Particle effect data.
     * @param count     The number of particles to display.
     */
    void send(Collection<? extends Player> players, INmsParticleType particle,
              boolean force,
              double x, double y, double z,
              double dataX, double dataY, double dataZ,
              float data, int count);

    interface INmsParticleType extends INamed {

        int[] getPacketInts();
    }
}
