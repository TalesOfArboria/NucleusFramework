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

import com.jcwhatever.nucleus.nms.INmsTitleHandler;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;

import java.lang.reflect.Field;
import javax.annotation.Nullable;

/**
 * Minecraft Title packet sender for NMS version v1_8_R1
 */
public final class NmsTitleHandler_v1_8_R1 implements INmsTitleHandler {

    private static Field _footerField;

    static {
        try {
            _footerField = PacketPlayOutPlayerListHeaderFooter.class.getDeclaredField("b");
            _footerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(Player player, String jsonTitle, @Nullable String jsonSubtitle, int fadeIn, int stay, int fadeOut) {
        PreCon.notNull(player);
        PreCon.notNullOrEmpty(jsonTitle);

        IChatBaseComponent titleComponent = ChatSerializer.a(jsonTitle);

        CraftPlayer craftPlayer = (CraftPlayer)player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;

        PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        connection.sendPacket(timesPacket);

        if (jsonSubtitle != null) {
            IChatBaseComponent subTitleComponent = ChatSerializer.a(jsonSubtitle);
            PacketPlayOutTitle subTitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subTitleComponent);
            connection.sendPacket(subTitlePacket);
        }

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleComponent);
        connection.sendPacket(titlePacket);
    }

    @Override
    public void sendTab(Player player, @Nullable String jsonHeader, @Nullable String jsonFooter) {
        PreCon.notNull(player);

        if (jsonHeader == null && jsonFooter == null)
            return;

        IChatBaseComponent headerComponent = ChatSerializer.a(jsonHeader);

        PacketPlayOutPlayerListHeaderFooter packet = jsonHeader != null
                ? new PacketPlayOutPlayerListHeaderFooter(headerComponent)
                : new PacketPlayOutPlayerListHeaderFooter();

        if (jsonFooter != null && _footerField != null) {

            IChatBaseComponent footerComponent = ChatSerializer.a(jsonFooter);

            try {
                _footerField.set(packet, footerComponent);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        CraftPlayer craftPlayer = (CraftPlayer)player;
        PlayerConnection connection = craftPlayer.getHandle().playerConnection;

        connection.sendPacket(packet);
    }
}
