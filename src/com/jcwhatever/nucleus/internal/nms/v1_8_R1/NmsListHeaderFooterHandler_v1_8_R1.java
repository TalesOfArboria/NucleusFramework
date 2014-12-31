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

import com.jcwhatever.nucleus.nms.INmsListHeaderFooterHandler;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Minecraft Tab List Header Footer Title  packet sender for NMS version v1_8_R1
 */
public class NmsListHeaderFooterHandler_v1_8_R1 extends v1_8_R1 implements INmsListHeaderFooterHandler {

    /**
     * Send the packet to a player.
     *
     * @param player      The player to send the header/footer to.
     * @param jsonHeader  The Json header text.
     * @param jsonFooter  The Json footer text.
     */
    @Override
    public void send(Player player, @Nullable String jsonHeader, @Nullable String jsonFooter) {
        PreCon.notNull(player);

        if (jsonHeader == null && jsonFooter == null)
            return;

        Object headerComponent = _ChatSerializer.invokeStatic("serialize", jsonHeader);

        // create packet instance based on the presence of a header
        Object packet = jsonHeader != null
                ? _PacketPlayOutPlayerListHeaderFooter.construct("newHeader", headerComponent) // header constructor
                : _PacketPlayOutPlayerListHeaderFooter.construct("new"); // no header constructor

        if (jsonFooter != null) {

            Object footerComponent = _ChatSerializer.invokeStatic("serialize", jsonFooter);

            // insert footer into packet footer field
            _PacketPlayOutPlayerListHeaderFooter.reflect(packet).set("footer", footerComponent);
        }

        // send packet
        sendPacket(player, packet);
    }
}
