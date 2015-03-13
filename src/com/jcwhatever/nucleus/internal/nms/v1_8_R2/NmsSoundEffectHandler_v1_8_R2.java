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

package com.jcwhatever.nucleus.internal.nms.v1_8_R2;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.nms.INmsSoundEffectHandler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Minecraft named sound effect packet sender for NMS version v1_8_R2
 */
public class NmsSoundEffectHandler_v1_8_R2 extends v1_8_R2 implements INmsSoundEffectHandler {

    public NmsSoundEffectHandler_v1_8_R2() {

    }

    @Override
    public void send(final Player player, final String soundName,
                     final double x, final double y, final double z,
                     final float volume, final float pitch) {

        if (Bukkit.isPrimaryThread()) {
            syncSend(player, soundName, x, y, z, volume, pitch);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(player, soundName, x, y, z, volume, pitch);
                }
            });
        }
    }

    private void syncSend(Player player, String soundName,
                          double x, double y, double z, float volume, float pitch) {

        try {
            Object packet = _PacketPlayOutNamedSoundEffect.construct("new", soundName, x, y, z, volume, pitch);

            sendPacket(player, packet);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            _isAvailable = false;
        }
    }
}
