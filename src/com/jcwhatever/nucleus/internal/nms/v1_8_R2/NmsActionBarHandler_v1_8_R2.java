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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.nms.INmsActionBarHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Minecraft Action Bar sender for NMS version v1_8_R2
 */
public final class NmsActionBarHandler_v1_8_R2 extends v1_8_R2 implements INmsActionBarHandler {

    /**
     * Send the action bar packet.
     *
     * @param player    The player to send the text to.
     * @param rawText  The Json text.
     */
    @Override
    public void send(Player player, String rawText) {
        PreCon.notNull(player);
        PreCon.notNull(rawText);

        String jsonText = "{text:\"" + TextUtils.format(rawText).replace("\"", "\\\"") + "\"}";

        sendJson(player, jsonText);
    }

    @Override
    public void sendJson(final Player player, final String jsonText) {
        PreCon.notNull(player);
        PreCon.notNull(jsonText);

        if (Bukkit.isPrimaryThread()) {
            syncSend(player, jsonText);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(player, jsonText);
                }
            });
        }
    }

    private void syncSend(Player player, String text) {

        try {

            Object titleComponent = _ChatSerializer.invokeStatic("serialize", text);

            Object packet = _PacketPlayOutChat.construct("new", titleComponent, (byte) 2);

            sendPacket(player, packet);

        }
        catch (RuntimeException e) {
            e.printStackTrace();
            _isAvailable = false;
        }
    }
}
