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

package com.jcwhatever.nucleus.internal.nms.v1_8_R1;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.reflection.IReflectedInstance;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.nms.INmsTitleHandler;
import com.jcwhatever.nucleus.utils.text.SimpleJSONBuilder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Minecraft Title packet sender for NMS version v1_8_R1
 */
public final class NmsTitleHandler_v1_8_R1 extends v1_8_R1 implements INmsTitleHandler {

    /**
     * Send the packet to a player.
     *
     * @param player        The player to send the title to.
     * @param rawTitle     The Json title text.
     * @param rawSubtitle  Optional Json subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    @Override
    public void send(final Player player,
                     final String rawTitle,
                     @Nullable final String rawSubtitle,
                     final int fadeIn, final int stay, final int fadeOut) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(rawTitle);

        String jsonTitle = SimpleJSONBuilder.text(rawTitle);
        String jsonSubtitle = rawSubtitle != null
                ? SimpleJSONBuilder.text(rawSubtitle)
                : null;

        sendJson(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);

    }

    /**
     * Send the packet to a player.
     *
     * @param player        The player to send the title to.
     * @param jsonTitle     The title text.
     * @param jsonSubtitle  Optional subtitle text.
     * @param fadeIn        The fade-in time.
     * @param stay          The stay time.
     * @param fadeOut       The fade-out time.
     */
    @Override
    public void sendJson(final Player player,
                         final String jsonTitle,
                         @Nullable final String jsonSubtitle,
                         final int fadeIn, final int stay, final int fadeOut) {

        if (Bukkit.isPrimaryThread()) {
            syncSend(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
        }
        else {
            Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    syncSend(player, jsonTitle, jsonSubtitle, fadeIn, stay, fadeOut);
                }
            });
        }
    }

    private void syncSend(Player player, String title, @Nullable String subTitle,
                          int fadeIn, int stay, int fadeOut) {

        try {

            IReflectedInstance connection = getConnection(player);

            // times packet
            Object timesPacket = _PacketPlayOutTitle.construct("newTimes", fadeIn, stay, fadeOut);
            connection.invoke("sendPacket", timesPacket);

            // sub title packet
            if (subTitle != null) {
                Object subTitleComponent = _ChatSerializer.invokeStatic("serialize", subTitle);
                Object subTitlePacket = _PacketPlayOutTitle.construct(
                        "new", _EnumTitleAction.getEnum("SUBTITLE"), subTitleComponent);

                connection.invoke("sendPacket", subTitlePacket);
            }

            // title packet
            Object titleComponent = _ChatSerializer.invokeStatic("serialize", title);
            Object titlePacket = _PacketPlayOutTitle.construct(
                    "new", _EnumTitleAction.getEnum("TITLE"), titleComponent);

            connection.invoke("sendPacket", titlePacket);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            _isAvailable = false;
        }
    }
}
