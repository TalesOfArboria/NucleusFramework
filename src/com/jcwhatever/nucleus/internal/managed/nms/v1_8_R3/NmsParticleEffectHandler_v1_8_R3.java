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

package com.jcwhatever.nucleus.internal.managed.nms.v1_8_R3;

import com.jcwhatever.nucleus.utils.nms.INmsParticleEffectHandler;
import org.bukkit.entity.Player;

/**
 * Minecraft particle effect packet sender for NMS version v1_8_R3
 */
public class NmsParticleEffectHandler_v1_8_R3 extends v1_8_R3 implements INmsParticleEffectHandler {

    @Override
    public void send(Player player, INmsParticleType particleType, boolean force,
                     double x, double y, double z,
                     double offsetX, double offsetY, double offsetZ,
                     float data, int count) {

        Object enumParticle = _EnumParticle.getEnum(particleType.getName());

        Object packet = _PacketPlayOutWorldParticles.construct("new", enumParticle, force,
                (float)x, (float)y, (float)z,
                (float)offsetX, (float)offsetY, (float)offsetZ,
                data, count, particleType.getPacketInts());

        sendPacket(player, packet);
    }
}
