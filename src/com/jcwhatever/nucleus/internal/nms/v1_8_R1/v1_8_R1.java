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

import com.jcwhatever.nucleus.utils.reflection.Fields;
import com.jcwhatever.nucleus.utils.reflection.ReflectedType;
import com.jcwhatever.nucleus.utils.reflection.Reflection;

import org.bukkit.entity.Player;

/**
 * Reflected types for NMS version v1_8_R1
 */
public class v1_8_R1 {

    static Reflection reflection = new Reflection("v1_8_R1");

    static ReflectedType _PacketPlayOutTitle = reflection.nmsType("PacketPlayOutTitle");

    static ReflectedType _PacketPlayOutChat = reflection.nmsType("PacketPlayOutChat");

    static ReflectedType _PacketPlayOutPlayerListHeaderFooter = reflection.nmsType("PacketPlayOutPlayerListHeaderFooter");

    static ReflectedType _ChatSerializer = reflection.nmsType("ChatSerializer")
            .methodAlias("serialize", "a", String.class);

    static ReflectedType _Packet = reflection.nmsType("Packet");

    static ReflectedType _PlayerConnection = reflection.nmsType("PlayerConnection")
            .method("sendPacket", _Packet.getHandle());

    static ReflectedType _CraftPlayer = reflection.craftType("entity.CraftPlayer")
            .method("getHandle");

    static ReflectedType _EntityPlayer = reflection.nmsType("EntityPlayer");

    /**
     * Send an NMS packet.
     *
     * @param player  The player to send the packet to.
     * @param packet  The packet to send.
     */
    void sendPacket(Player player, Object packet) {

        Fields fields = _EntityPlayer.reflect(_CraftPlayer.reflect(player).invoke("getHandle")).getFields();

        _PlayerConnection.reflect(fields.get("playerConnection")).invoke("sendPacket", packet);
    }
}
